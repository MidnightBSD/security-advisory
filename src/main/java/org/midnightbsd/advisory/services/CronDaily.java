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

import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.nvd.CveDataPage;
import org.midnightbsd.advisory.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/** @author Lucas Holt */
@Slf4j
@Service
public class CronDaily {
  private static final int DELAY_ONE_MINUTE = 1000 * 60;
  private static final int ONE_DAY = DELAY_ONE_MINUTE * 60 * 24;

  private Date lastFetchedDate = null; // TODO: persist on startup


  @Autowired private NvdFetchService nvdFetchService;

  @Autowired private NvdImportService nvdImportService;

  @Scheduled(fixedDelay = ONE_DAY, initialDelay = DELAY_ONE_MINUTE)
  public void daily() throws InterruptedException {
    CveDataPage cveDataPage;
    long startIndex = 0L;

    log.info("Begin import of CVE data");
    if (lastFetchedDate == null) {
      cveDataPage = nvdFetchService.getPage(startIndex);
    } else {
      cveDataPage = nvdFetchService.getPage(lastFetchedDate, startIndex);
    }
    nvdImportService.importNvd(cveDataPage);
    Thread.sleep(6000L);
    startIndex += cveDataPage.getResultsPerPage();

    while (cveDataPage.getTotalResults() > cveDataPage.getStartIndex()) {
      if (lastFetchedDate == null) {
        cveDataPage = nvdFetchService.getPage(startIndex);
      } else {
        cveDataPage = nvdFetchService.getPage(lastFetchedDate, startIndex);
      }
      nvdImportService.importNvd(cveDataPage);
      Thread.sleep(6000L);

      startIndex += cveDataPage.getResultsPerPage();
    }

    lastFetchedDate = DateUtil.getCveApiDate(cveDataPage.getResult().getTimestamp());
    log.info("Finished daily import");
  }
}
