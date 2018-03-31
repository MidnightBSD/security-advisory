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
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * @author Lucas Holt
 */
@Slf4j
@Service
public class NvdFetchService {

    @Value("${nvdfeed.baseUrl}")
    private String nvdfeedUrl;

    @Autowired
    private ObjectMapper objectMapper;

    public static byte[] decompress(final byte[] contentBytes) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(contentBytes)), out);

            return out.toByteArray();
        } catch (final IOException io) {
            log.error(io.getMessage(), io);
            throw new RuntimeException(io);
        }
    }

    public CveData getNVDData(final String suffix) {
        final String url = nvdfeedUrl + suffix;

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
        } catch (final IOException e) {
            log.error("network call failed.", e);
        }
        return null;
    }

}
