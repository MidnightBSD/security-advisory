package org.midnightbsd.advisory.util;

import org.apache.commons.codec.Charsets;
import org.apache.commons.compress.utils.IOUtils;

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
public class CompressUtil {
    public static byte[] decompressGzip(final byte[] contentBytes) throws IOException {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(contentBytes)), out);
            return out.toByteArray();
        }
    }

    public static byte[] decompressZip(final byte[] contentBytes) throws IOException {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IOUtils.copy(new ZipInputStream(new ByteArrayInputStream(contentBytes)), out);
            return out.toByteArray();
        }
    }

    public static byte[] decompressDeflate(final byte[] contentBytes) throws IOException {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            IOUtils.copy(new DeflaterInputStream(new ByteArrayInputStream(contentBytes)), out);
            return out.toByteArray();
        }
    }

    public static String extract(String contentType, byte[] responseBytes) throws IOException {
        String decompressed;
        try {
            decompressed = new String(decompressGzip(responseBytes), Charsets.UTF_8);
        } catch (final ZipException zip) {
            // fallback to raw string
            decompressed = new String(responseBytes, Charsets.UTF_8);
        }

        // try zip if gzip fails
        if (contentType.equalsIgnoreCase("application/x-zip") || contentType.equalsIgnoreCase("application/zip")) {
            try {
                decompressed = new String(decompressZip(responseBytes), Charsets.UTF_8);
            } catch (final ZipException ignored) {
                // fallback to raw string
                decompressed = new String(responseBytes, Charsets.UTF_8);
            }
        }

        if (contentType.equalsIgnoreCase("application/x-deflate") || contentType.equalsIgnoreCase("application/deflate")) {
            try {
                decompressed = new String(decompressDeflate(responseBytes), Charsets.UTF_8);
            } catch (final ZipException z3) {
                // fallback to raw string
                decompressed = new String(responseBytes, Charsets.UTF_8);
            }
        }

        return decompressed;
    }

}
