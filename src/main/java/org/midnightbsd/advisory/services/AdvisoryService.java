package org.midnightbsd.advisory.services;

import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lucas Holt
 */
@Service
public class AdvisoryService implements AppService<Advisory> {

    @Autowired
    private AdvisoryRepository repository;

    public List<Advisory> list() {
        return repository.findAll();
    }

    public Page<Advisory> get(final Pageable page) {
        return repository.findAll(page);
    }

    public Advisory get(final int id) {
        return repository.findOne(id);
    }

    public Advisory getByCveId(final String cveId) {
        return repository.findOneByCveId(cveId);
    }

    @Transactional
    public void batchSave(List<Advisory> advisories) {
        ArrayList<Advisory> createList = new ArrayList<>();

        for (Advisory advisory : advisories) {
            final Advisory adv = repository.findOneByCveId(advisory.getCveId());
            if (adv == null) {
                createList.add(advisory);
            }  else {
                // TODO: last modified date detection
                
                adv.setDescription(advisory.getDescription());
                adv.setLastModifiedDate(advisory.getLastModifiedDate());
                adv.setPublishedDate(advisory.getPublishedDate());
                adv.setSeverity(advisory.getSeverity());
                adv.setProblemType(advisory.getProblemType());
                // TODO: product updates
                repository.save(adv);
            }
        }
        repository.flush();

        repository.save(createList);
        repository.flush();
    }

    @Transactional
    public Advisory save(final Advisory advisory) {
        final Advisory adv = repository.findOneByCveId(advisory.getCveId());
        if (adv == null) {
            return repository.saveAndFlush(advisory);
        }

        if (adv.getDescription().equals(advisory.getDescription()))
            return adv;

        adv.setDescription(advisory.getDescription());
        return repository.saveAndFlush(adv);
    }
}
