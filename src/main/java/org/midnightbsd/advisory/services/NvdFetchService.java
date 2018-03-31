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
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

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

    public static byte[] decompressGzip(final byte[] contentBytes) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(contentBytes)), out);

            return out.toByteArray();
        }
    }

    public static byte[] decompressZip(final byte[] contentBytes) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            IOUtils.copy(new ZipInputStream(new ByteArrayInputStream(contentBytes)), out);

            return out.toByteArray();
        }
    }

    public static byte[] decompressDeflate(final byte[] contentBytes) throws IOException {
          try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

              IOUtils.copy(new DeflaterInputStream(new ByteArrayInputStream(contentBytes)), out);

              return out.toByteArray();
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

            if (response.getStatusLine().getStatusCode() != 200) {
                log.error("Unable to fetch with status code " +
                        response.getStatusLine().getStatusCode());
                return null;
            }

            org.apache.http.HttpEntity entity = response.getEntity();
            if (entity != null) {

                String contentType = entity.getContentType().getValue().toString();
                log.info("Content type is {}", contentType);

                if (contentType.equalsIgnoreCase(" text/html")) {
                    log.error("An error occurred and we got a web page");
                    return null;
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                entity.writeTo(baos);

                byte[] responseBytes = baos.toByteArray();

                String decompressed;
                try {
                    decompressed = new String(decompressGzip(responseBytes), Charsets.UTF_8);

                } catch (ZipException zip) {
                    // fallback to raw string
                    decompressed = new String(responseBytes, Charsets.UTF_8);
                }

                // try zip if gzip fails
                if (contentType.equalsIgnoreCase("application/x-zip") || contentType.equalsIgnoreCase("application/zip")) {
                    try {
                        decompressed = new String(decompressZip(responseBytes), Charsets.UTF_8);
                    } catch (ZipException z2) {

                    }
                 }

                if (contentType.equalsIgnoreCase("application/x-deflate") || contentType.equalsIgnoreCase("application/deflate")) {
                    try {
                        decompressed = new String(decompressDeflate(responseBytes), Charsets.UTF_8);
                    } catch (ZipException z3) {
                        // fallback to raw string
                        decompressed = new String(responseBytes, Charsets.UTF_8);
                    }
                }

                baos.close();

                return objectMapper.readValue(decompressed, CveData.class);
            }
        } catch (final IOException e) {
            log.error("network call failed.", e);
        }
        return null;
    }

}
