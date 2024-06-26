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

import java.util.Calendar;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.nvd2.Root;
import org.midnightbsd.advisory.model.nvd2.Vulnerability;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;

/** @author Lucas Holt */
@Slf4j
@Service
public class CronDaily {
  private static final int DELAY_ONE_MINUTE = 1000 * 60;
  private static final int ONE_DAY = DELAY_ONE_MINUTE * 60 * 24;

  private Date lastFetchedDate = null;

  private final NvdFetchService nvdFetchService;

  private final NvdImportService nvdImportService;

  private final AdvisoryRepository advisoryRepository;

  public CronDaily(NvdFetchService nvdFetchService, NvdImportService nvdImportService, AdvisoryRepository advisoryRepository) {
    this.nvdFetchService = nvdFetchService;
    this.nvdImportService = nvdImportService;
    this.advisoryRepository = advisoryRepository;
  }

  @PostConstruct
  public void init() {
    if (lastFetchedDate == null) {
      var item = advisoryRepository.findByOrderByLastModifiedDateDesc(PageRequest.of(0, 1));
      if (item!= null && item.hasContent()) {
        lastFetchedDate = item.get().findFirst().get().getLastModifiedDate();
      }
    }
  }

  private Date maxDate(Date input) {
    Calendar c = Calendar.getInstance();
    c.setTime(input);
    c.add(Calendar.DATE, 90); // 90 days added, 120 max value
    return c.getTime();
  }

  @Scheduled(fixedDelay = ONE_DAY, initialDelay = DELAY_ONE_MINUTE)
  public void daily() throws InterruptedException {
    Root cveDataPage;
    long startIndex = 0L;

    log.warn("Begin import of CVE data");
    if (lastFetchedDate == null) {
      var item = advisoryRepository.findByOrderByLastModifiedDateDesc(PageRequest.of(0, 1));
      if (item!= null && item.hasContent()) {
        lastFetchedDate = item.get().findFirst().get().getLastModifiedDate();
      }
    }

    if (lastFetchedDate == null) {
      log.warn("Starting cron daily from the beginning");
      cveDataPage = nvdFetchService.getPage(startIndex);
    } else {
      log.info("Starting cron daily from {}", lastFetchedDate);
      cveDataPage = nvdFetchService.getPage(lastFetchedDate, maxDate(lastFetchedDate), startIndex);
    }
    log.warn("Loading first fetched page. total results: {}", cveDataPage.getTotalResults());
    importNvd(cveDataPage);
    Thread.sleep(6000L);
    startIndex += cveDataPage.getResultsPerPage();

    while (cveDataPage.getTotalResults() > cveDataPage.getStartIndex()) {
      log.warn("Starting fetch at {} of total {}", cveDataPage.getStartIndex(), cveDataPage.getTotalResults());
      if (lastFetchedDate == null) {
        cveDataPage = nvdFetchService.getPage(startIndex);
      } else {
        cveDataPage = nvdFetchService.getPage(lastFetchedDate, maxDate(lastFetchedDate), startIndex);
      }
      try {
        importNvd(cveDataPage);
      } catch (IllegalArgumentException e) {
        log.error("Failed sanity check. Page had null data?");
        break;
      }
      sleep(6000L);

      startIndex += cveDataPage.getResultsPerPage();
    }

    lastFetchedDate = cveDataPage.getTimestamp();
    log.info("Finished daily import");
  }

  public void importNvd(final Root root) {
    sanityCheck(root);

    for (final Vulnerability vulnerability : root.getVulnerabilities()) {
      nvdImportService.importVulnerability(vulnerability);
      sleep(200L);
    }
  }

  private void sanityCheck(Root root) {
    if (root == null) throw new IllegalArgumentException("root");

    if (CollectionUtils.isEmpty(root.getVulnerabilities()))
      throw new IllegalArgumentException("root.getVulnerabilities()");
  }

  private void sleep(long time) {
    try {
        Thread.sleep(time);
    } catch (InterruptedException e) {
      log.error("Issue sleeping during nvd import", e);
      Thread.currentThread().interrupt();
    }
  }
}
