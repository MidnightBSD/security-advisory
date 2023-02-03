/*
 * Copyright (c) 2017-2023 Lucas Holt
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


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;

/** @author Lucas Holt */
@Slf4j
public class DateUtil {

  private DateUtil() {
    super();
  }

  public static Date yesterday() {
    return subtractDays(Calendar.getInstance().getTime(), 1);
  }

  public static Date subtractDays(Date date, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, -days);

    return cal.getTime();
  }

  public static Date getCveApiDate(final String dt) {
    if (dt == null || dt.isEmpty()) return null;

    // 2018-02-20T21:29Z

    try {
      // deepcode ignore FixDateFormat: API we're calling doesn't output in JS date format
      final SimpleDateFormat iso8601Dateformat =
              new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US);
      return iso8601Dateformat.parse(dt);
    } catch (final Exception e) {
      log.error("Could not convert date string {}", dt, e);
    }

    return null;
  }

  public static String formatCveApiDate(final Date date) {
    if (date == null) return null;

    // they recommend yyyy-MM-dd'T'HH:mm:ss:SSS z but the api seems picky about the time zone format

    // 2019-01-01T00:00:00:000 UTC-05:00
    return formatDate(date, "yyyy-MM-dd'T'HH:mm:ss:'000' z");
  }

  public static String formatDate(final Date date, final String dateFormat) {
    if (date == null) return null;

    try {
      final SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.US);
      formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
      return formatter.format(date);
    } catch (final Exception e) {
      log.error("Could not convert date {}", date, e);
    }

    return null;
  }
}
