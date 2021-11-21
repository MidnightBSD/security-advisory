/*
 * Copyright (c) 2017-2021 Lucas Holt
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.midnightbsd.advisory.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import org.apache.commons.codec.Charsets;
import org.apache.commons.compress.utils.IOUtils;

/** @author Lucas Holt */
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
    if (contentType.equalsIgnoreCase("application/x-zip")
        || contentType.equalsIgnoreCase("application/zip")) {
      try {
        decompressed = new String(decompressZip(responseBytes), Charsets.UTF_8);
      } catch (final ZipException ignored) {
        // fallback to raw string
        decompressed = new String(responseBytes, Charsets.UTF_8);
      }
    }

    if (contentType.equalsIgnoreCase("application/x-deflate")
        || contentType.equalsIgnoreCase("application/deflate")) {
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
