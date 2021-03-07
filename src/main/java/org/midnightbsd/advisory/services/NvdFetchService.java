package org.midnightbsd.advisory.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.midnightbsd.advisory.model.nvd.CveData;
import org.midnightbsd.advisory.model.nvd.CveDataPage;
import org.midnightbsd.advisory.util.CompressUtil;
import org.midnightbsd.advisory.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * @author Lucas Holt
 */
@Slf4j
@Service
public class NvdFetchService {

    @Deprecated(forRemoval = true)
    @Value("${nvdfeed.baseUrl}")
    private String nvdfeedUrl;

    @Value("${nvdfeed.serviceUrl}")
    private String nvdServiceUrl;

    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    @Autowired
    public NvdFetchService(final RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    /**
     * In case we ever need to reload ALL data
     * @param startIndex  record to start page with
     * @return Single page of records
     */
    public CveDataPage getPage(final int startIndex) {
        final String url = nvdServiceUrl + "cves/1.0?resultsPerPage=5000&startIndex={startIndex}";
        return restTemplate.getForObject(url, CveDataPage.class, startIndex);
    }

    /**
     * Reload data since a specific date
     * @param modStartDate CVE modified start date
     * @param startIndex record to start page with
     * @return Single page of records
     */                                                                  
    public CveDataPage getPage(final Date modStartDate, final int startIndex) {
        final String url = nvdServiceUrl + "cves/1.0?resultsPerPage=5000&startIndex={startIndex}&modStartDate={modStartDate}";
        return restTemplate.getForObject(url, CveDataPage.class, startIndex, DateUtil.formatCveApiDate(modStartDate));
    }

    /**
     * @deprecated CVE feeds retired
     */
    @Deprecated(forRemoval = true)
    public CveData getNVDData(final String suffix) {
        final String url = nvdfeedUrl + suffix;

        log.info("Fetching nvd data for {}", url);
        //  https://static.nvd.nist.gov/feeds/json/cve/1.0/nvdcve-1.0-recent.json.gz

        try {
            final HttpClient client = HttpClientBuilder.create().build();
            final HttpGet request = new HttpGet(url);

            final HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                log.error("Unable to fetch with status code {}",
                        response.getStatusLine().getStatusCode());
                return null;
            }

            final org.apache.http.HttpEntity entity = response.getEntity();
            if (entity != null) {

                final String contentType = entity.getContentType().getValue();
                log.info("Content type is {}", contentType);

                if (contentType.equalsIgnoreCase(" text/html")) {
                    log.error("An error occurred and we got a web page");
                    return null;
                }

                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                entity.writeTo(baos);

                final String decompressed = CompressUtil.extract(contentType, baos.toByteArray());
                baos.close();

                return objectMapper.readValue(decompressed, CveData.class);
            }
        } catch (final IOException e) {
            log.error("network call failed.", e);
        }
        return null;
    }
}
