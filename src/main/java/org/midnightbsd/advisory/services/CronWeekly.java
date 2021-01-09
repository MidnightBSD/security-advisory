package org.midnightbsd.advisory.services;

import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.nvd.CveData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;

/**
 * @author Lucas Holt
 */
@Slf4j
@Service
public class CronWeekly {
    private static final int DELAY_ONE_MINUTE = 1000 * 60;
    private static final int ONE_HOUR = DELAY_ONE_MINUTE * 60;
    private static final int ONE_DAY = ONE_HOUR * 24;
    private static final int ONE_WEEK = ONE_DAY * 7;
    private static final int START_YEAR = 2002;

    @Autowired
    private NvdFetchService nvdFetchService;

    @Autowired
    private NvdImportService nvdImportService;

    /**
     * Pull previous weeks
     */
    @Scheduled(fixedDelay = ONE_WEEK, initialDelay = ONE_HOUR)
    public void weekly() throws IOException {
        // https://static.nvd.nist.gov/feeds/json/cve/1.0/nvdcve-1.0-2018.json.gz

        final int year = Calendar.getInstance().getTime().getYear() + 1900;

        log.info("Fetching data from {} to {}", year, START_YEAR);

        for (int i = year; i >= START_YEAR; i--) {
            final String suffix = "nvdcve-1.0-" + Integer.toString(i) + ".json.gz";
            final CveData data = nvdFetchService.getNVDData(suffix);

            if (data == null) {
                log.warn("Data for {} invalid", i);
                continue;
            }

            log.info("Begin import of {} data", i);
            nvdImportService.importNvd(data);
        }

        log.info("Finished weekly import");
    }
}
