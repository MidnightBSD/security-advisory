package org.midnightbsd.advisory.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.midnightbsd.advisory.model.nvd.CveData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

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

    @Autowired
    private ObjectMapper objectMapper;

    private static final int DELAY_ONE_MINUTE = 1000 * 60;
    private static final int ONE_DAY = DELAY_ONE_MINUTE * 60 * 24;
    private static final int ONE_MONTH = ONE_DAY * 30;
    private static final int START_YEAR = 2002;

    @Autowired
    private NvdImportService nvdImportService;


    @Scheduled(fixedDelay = ONE_DAY, initialDelay = DELAY_ONE_MINUTE)
    public void daily() throws IOException {
        final CveData recent = getNVDData(RECENT_SUFFIX);

     //  log.info( "Dumped: " + objectMapper.writeValueAsString(recent));

        log.info("Begin import of recent data");
        nvdImportService.importNvd(recent);

        log.info("Finished daily import");
    }


    /**
     * Pull previous weeks
     */
    @Scheduled(fixedDelay = ONE_MONTH, initialDelay = DELAY_ONE_MINUTE)
    public void weekly() throws IOException {
        // https://static.nvd.nist.gov/feeds/json/cve/1.0/nvdcve-1.0-2018.json.gz

        final int year = Calendar.getInstance().getTime().getYear() + 1900;

        log.info("Fetching data from 2002 to " + year);

        for (int i = year; i >= START_YEAR; i--) {
            try {
                final String suffix = "nvdcve-1.0-" + Integer.toString(i) + ".json.gz";
                final CveData data = getNVDData(suffix);

                log.info("Begin import of " + i + " data");
                nvdImportService.importNvd(data);
            } catch (final ZipException zipException) {
                log.error("Invalid gz file", zipException);
            }
        }

        log.info("Finished weekly import");
    }


    public static byte[] decompress(byte[] contentBytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(contentBytes)), out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }


    public CveData getNVDData(String suffix) throws IOException {
        String url = nvdfeedUrl + suffix;
        log.info("Fetching nvd data for " + url);
        //  https://static.nvd.nist.gov/feeds/json/cve/1.0/nvdcve-1.0-recent.json.gz

        try {

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);


            HttpResponse response = client.execute(request);

            org.apache.http.HttpEntity entity = response.getEntity();
            if (entity != null) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                entity.writeTo(baos);

                byte[] responseBytes = baos.toByteArray();
                String decompressed = new String(decompress(responseBytes), Charsets.UTF_8);

                baos.close();

                return objectMapper.readValue(decompressed, CveData.class);
            }
        } catch (IOException e) {
            log.error("network call failed.", e);
        }
        return null;
    }

}
