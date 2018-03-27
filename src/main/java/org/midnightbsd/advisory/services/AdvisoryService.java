package org.midnightbsd.advisory.services;

import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Advisory createIfNotExists(final String cveId) {
        final Advisory arch = getByCveId(cveId);
        if (arch != null)
            return arch;

        return save(Advisory.builder().cveId(cveId).build());
    }
}
