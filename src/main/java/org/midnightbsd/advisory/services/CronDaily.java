package org.midnightbsd.advisory.services;

import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.nvd.CveData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Lucas Holt
 */
@Slf4j
@Service
public class CronDaily {
    private static final int DELAY_ONE_MINUTE = 1000 * 60;
    private static final int ONE_DAY = DELAY_ONE_MINUTE * 60 * 24;
    private static final int TEN_MINUTES = DELAY_ONE_MINUTE * 10;
    private static final String RECENT_SUFFIX = "nvdcve-1.0-recent.json.gz";

    @Autowired
    private NvdFetchService nvdFetchService;

    @Autowired
    private NvdImportService nvdImportService;

    @Scheduled(fixedDelay = ONE_DAY, initialDelay = TEN_MINUTES)
    public void daily() throws IOException {
        final CveData recent = nvdFetchService.getNVDData(RECENT_SUFFIX);

        log.info("Begin import of recent data");
        nvdImportService.importNvd(recent);

        log.info("Finished daily import");
    }

}
