package org.midnightbsd.advisory.services;

import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lucas Holt
 */
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "advisory")
@Slf4j
@Service
public class AdvisoryService implements AppService<Advisory> {

    private final AdvisoryRepository repository;

    private final SearchService searchService;

    @Autowired
    public AdvisoryService(final AdvisoryRepository repository, final SearchService searchService) {
        this.repository = repository;
        this.searchService = searchService;
    }

    public List<Advisory> list() {
        return repository.findAll();
    }

    public List<Advisory> getByProduct(final String productName) {
        return repository.findByProductName(productName);
    }

    @Cacheable(unless = "#result == null", key = "#vendorName")
    public List<Advisory> getByVendor(final String vendorName) {
        return repository.findByVendorName(vendorName);
    }

    @Cacheable(unless = "#result == null", key = "#vendorName.concat(#productName)")
    public List<Advisory> getByVendorAndProduct(final String vendorName, final String productName) {
        return repository.findByVendorNameAAndProductsIsLike(vendorName, productName);
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

    @CacheEvict(allEntries = true)
    @Transactional
    public void batchSave(final List<Advisory> advisories) {
        log.info("Advisory batch save of " + advisories.size());

        final List<Advisory> createList = new ArrayList<>();

        for (final Advisory advisory : advisories) {
            Advisory adv = repository.findOneByCveId(advisory.getCveId());
            if (adv == null) {
                createList.add(advisory);
            } else {
                boolean update = false; // dirty check

                log.info("Updating " + adv.getCveId());

                if (advisory.getDescription() != null && !advisory.getDescription().equalsIgnoreCase(adv.getDescription())) {
                    adv.setDescription(advisory.getDescription());
                    update = true;
                }

                if (advisory.getLastModifiedDate() != null && advisory.getLastModifiedDate().compareTo(adv.getLastModifiedDate()) != 0) {
                    adv.setLastModifiedDate(advisory.getLastModifiedDate());
                    update = true;
                }

                if (advisory.getPublishedDate() != null && advisory.getPublishedDate().compareTo(adv.getPublishedDate()) != 0) {
                    adv.setPublishedDate(advisory.getPublishedDate());
                    update = true;
                }

                if (advisory.getSeverity() != null && !advisory.getSeverity().equalsIgnoreCase(adv.getSeverity())) {
                    adv.setSeverity(advisory.getSeverity());
                    update = true;
                }

                if (advisory.getProblemType() != null && advisory.getProblemType().equalsIgnoreCase(adv.getProblemType())) {
                    adv.setProblemType(advisory.getProblemType());
                    update = true;
                }

                if (update && advisory.getProducts() != null) {
                    log.info("{} contains {} products", adv.getCveId(), advisory.getProducts().size());
                    adv.setProducts(advisory.getProducts());
                }

                if (update) {
                    adv = repository.save(adv);
                    searchService.index(adv);
                }
            }
        }
        repository.flush();

        log.info("Saving {} new advisories", createList.size());
        repository.save(createList).stream().peek(searchService::index);
        repository.flush();
    }

    @CacheEvict(allEntries = true)
    @Transactional
    public Advisory save(final Advisory advisory) {
        Advisory adv = repository.findOneByCveId(advisory.getCveId());
        if (adv == null) {
            log.info("Adding " + advisory.getCveId());
            return repository.saveAndFlush(advisory);
        }

        boolean update = false; // dirty check

        log.info("Updating " + adv.getCveId());

        if (advisory.getDescription() != null && !advisory.getDescription().equalsIgnoreCase(adv.getDescription())) {
            adv.setDescription(advisory.getDescription());
            update = true;
        }

        if (advisory.getLastModifiedDate() != null && (adv.getLastModifiedDate() == null ||
                advisory.getLastModifiedDate().compareTo(adv.getLastModifiedDate()) != 0)) {
            adv.setLastModifiedDate(advisory.getLastModifiedDate());
            update = true;
        }

        if (advisory.getPublishedDate() != null && (adv.getPublishedDate() == null ||
                advisory.getPublishedDate().compareTo(adv.getPublishedDate()) != 0)) {
            adv.setPublishedDate(advisory.getPublishedDate());
            update = true;
        }

        if (advisory.getSeverity() != null && !advisory.getSeverity().equalsIgnoreCase(adv.getSeverity())) {
            adv.setSeverity(advisory.getSeverity());
            update = true;
        }

        if (advisory.getProblemType() != null && advisory.getProblemType().equalsIgnoreCase(adv.getProblemType())) {
            adv.setProblemType(advisory.getProblemType());
            update = true;
        }

        if (update && advisory.getProducts() != null) {
            log.info("{} contains {} products", adv.getCveId(), advisory.getProducts().size());
            adv.setProducts(advisory.getProducts());
        }

        if (update) {
            adv = repository.saveAndFlush(adv);
            searchService.index(adv);
        }

        return adv;
    }
}
