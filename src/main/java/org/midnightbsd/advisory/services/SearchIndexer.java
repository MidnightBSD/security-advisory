package org.midnightbsd.advisory.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@Profile("!test")
public class SearchIndexer {

    @Autowired
    private SearchService searchService;


    @Scheduled(fixedDelay = 1000 * 60 * 30, initialDelay = 120000)
    public void loadNewEntries() {
        log.info("Starting search indexer - Load all nvd items");
        
        // TODO: add timestamp so we can limit reindexing
        /*log.info("Search indexer - Load new entries from the last 30 minutes");

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -30);

        searchService.indexAllPackagesSince(cal.getTime());   */
        searchService.indexAllNvdItems();
    }

    /**
     * Load all nvd items at startup into elasticsearch
     */
    @PostConstruct
    public void initialize() {
        log.info("Starting search indexer - Load all nvd");

       searchService.indexAllNvdItems();
    }

}
