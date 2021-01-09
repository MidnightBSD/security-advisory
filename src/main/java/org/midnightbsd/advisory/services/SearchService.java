package org.midnightbsd.advisory.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.midnightbsd.advisory.model.Product;
import org.midnightbsd.advisory.model.search.Instance;
import org.midnightbsd.advisory.model.search.NvdItem;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.midnightbsd.advisory.repository.search.NvdSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Lucas Holt
 */
@Slf4j
@Service
public class SearchService {

    @Autowired
    private NvdSearchRepository nvdSearchRepository;

    @Autowired
    private AdvisoryRepository advisoryRepository;

   // @Cacheable(key="#p0.concat('-').concat(#p1.getPageNumber())", value = "search")
    public Page<NvdItem> find(String term, Pageable page) {
        return nvdSearchRepository.findByCveIdContainsOrDescriptionContainsAllIgnoreCase(term, term, page);
    }

    @CacheEvict(value = "search", allEntries = true)
    @Transactional
    @Async
    public void indexAllNvdItems() {
        try {
            Pageable pageable = PageRequest.of(0, 100);

            Page<org.midnightbsd.advisory.model.Advisory> advisories = advisoryRepository.findAll(pageable);
            for (int i = 0; i < advisories.getTotalPages(); i++) {
                final ArrayList<NvdItem> items = new ArrayList<>();

                for (final org.midnightbsd.advisory.model.Advisory adv : advisories) {
                    items.add(convert(adv));
                }

                log.debug("Saving a page of advisories to elasticsearch. pg {}", i);
                nvdSearchRepository.saveAll(items);

                pageable = PageRequest.of(i + 1, 100);
                advisories = advisoryRepository.findAll(pageable);
            }
        } catch (final ElasticsearchException es) {
            log.error(es.getDetailedMessage(), es);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @CacheEvict(value = "search", allEntries = true)
    @Transactional
    public void index(@NonNull final org.midnightbsd.advisory.model.Advisory adv) {
        log.debug("Indexing advisory {} id: {}",adv.getCveId(), adv.getId());
        nvdSearchRepository.save(convert(adv));
    }

    public NvdItem convert(@NonNull final org.midnightbsd.advisory.model.Advisory adv) {
        log.trace("Converting advisory {} id: {}", adv.getCveId(), adv.getId());
        
        final NvdItem nvdItem = new NvdItem();

        nvdItem.setId(adv.getId());

        nvdItem.setCveId(adv.getCveId());
        nvdItem.setDescription(adv.getDescription());
        nvdItem.setVersion(Calendar.getInstance().getTimeInMillis());

        final List<Instance> instances = new ArrayList<>();
        if (adv.getProducts() != null) {
            for (final Product instance : adv.getProducts()) {

                final Instance inst = new Instance();
                if (instance.getVendor() != null)
                    inst.setVendor(instance.getVendor().getName());
                if (instance.getName() != null)
                    inst.setProduct(instance.getName());
                inst.setVersion(instance.getVersion());
                instances.add(inst);
            }
        }
        nvdItem.setInstances(instances);

        return nvdItem;
    }
}
