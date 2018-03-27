package org.midnightbsd.advisory.repository.search;

import org.midnightbsd.advisory.model.search.NvdItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Lucas Holt
 */
public interface NvdSearchRepository extends ElasticsearchRepository<NvdItem, Integer> {

    Page<NvdItem> findByCveIdContainsOrDescriptionContainsAllIgnoreCase(String name, String description, Pageable page);
}
