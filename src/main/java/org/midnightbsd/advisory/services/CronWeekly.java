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
package org.midnightbsd.advisory.services;


import java.io.IOException;
import java.util.Calendar;
import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.nvd.CveData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/** @author Lucas Holt */
@Slf4j
@Service
public class CronWeekly {
  private static final int DELAY_ONE_MINUTE = 1000 * 60;
  private static final int ONE_HOUR = DELAY_ONE_MINUTE * 60;
  private static final int ONE_DAY = ONE_HOUR * 24;
  private static final int ONE_WEEK = ONE_DAY * 7;
  private static final int START_YEAR = 2002;

  @Autowired private NvdFetchService nvdFetchService;

  @Autowired private NvdImportService nvdImportService;

  /** Pull previous weeks */
  @Scheduled(fixedDelay = ONE_WEEK, initialDelay = ONE_HOUR)
  public void weekly() throws IOException, InterruptedException {
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

      // sleep between imports
      Thread.sleep(30000L);
    }

    log.info("Finished weekly import");
  }
}
