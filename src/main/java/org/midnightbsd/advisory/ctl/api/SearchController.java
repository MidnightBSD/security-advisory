package org.midnightbsd.advisory.ctl.api;

import org.midnightbsd.advisory.model.search.NvdItem;
import org.midnightbsd.advisory.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lucas Holt
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {
    @Autowired
    private SearchService searchService;
    
    @GetMapping
    public Page<NvdItem> find(@RequestParam("term") String term, Pageable page) {
        return searchService.find(term, page);
    }
}
