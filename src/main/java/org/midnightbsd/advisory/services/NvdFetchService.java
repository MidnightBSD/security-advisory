package org.midnightbsd.advisory.services;

import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.nvd.CveData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Lucas Holt
 */
@Slf4j
@Service
public class NvdFetchService {

    private static final String RECENT_SUFFIX = "nvdcve-1.0-recent.json.gz";

    @Value("${nvdfeed.baseUrl}")
    private String nvdfeedUrl;

    @Autowired
    private RestTemplate restTemplate;

    private static final int DELAY_ONE_MINUTE = 1000 * 60;
    private static final int ONE_DAY = DELAY_ONE_MINUTE * 60 * 24;
    private static final int ONE_WEEK = ONE_DAY * 7;

    @Autowired
    private NvdImportService nvdImportService;


    @Scheduled(fixedDelay = ONE_WEEK, initialDelay = DELAY_ONE_MINUTE)
    public void daily() {
        final CveData recent = getNVDData(RECENT_SUFFIX);
        nvdImportService.importNvd(recent);
    }


    /**
     * Pull previous weeks
     */
  /*  @Scheduled(fixedDelay = ONE_WEEK, initialDelay = DELAY_ONE_MINUTE)
    public void weekly() {
             for (int i = 2002; i <= Calendar.getInstance().getTime().getYear(); i++) {

             }
    }    */


    public CveData getNVDData(String suffix) {
        log.info("Fetching nvd data for " + suffix);
        return restTemplate.getForObject(nvdfeedUrl + "/" + suffix, CveData.class);
    }

}
