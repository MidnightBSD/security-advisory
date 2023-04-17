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
package org.midnightbsd.advisory.services;


import java.util.Collections;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.nvd2.Root;
import org.midnightbsd.advisory.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** @author Lucas Holt */
@Slf4j
@Service
public class NvdFetchService {

  @Value("${nvdfeed.serviceUrl}")
  private String nvdServiceUrl;

  @Value("${nvdfeed.apiKey}")
  private String apiKey;


  private final RestTemplate restTemplate;

  @Autowired
  public NvdFetchService(final RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
  }

  private Root get(final String url) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add("apiKey", apiKey);

    HttpEntity<String> entity = new HttpEntity<>(headers);
    var page = restTemplate.exchange(url, HttpMethod.GET, entity, Root.class);
    if (page.getStatusCode().is2xxSuccessful()) {
      return page.getBody();
    } else {
      log.error("Failed to fetch data from NvdFeed: {}", page.getStatusCode());
    }
    return null;
  }

  /**
   * In case we ever need to reload ALL data
   *
   * @param startIndex record to start page with
   * @return Single page of records
   */
  public Root getPage(final long startIndex) {
    final String url = String.format("%scves/2.0?noRejected&startIndex=%d", nvdServiceUrl,  startIndex);
    return get(url);
  }

  /**
   * Reload data since a specific date
   *
   * 120 day range max
   *
   * @param modStartDate CVE modified start date
   * @param modEndDate CVE modified end date
   * @param startIndex record to start page with
   * @return Single page of records
   */
  public Root getPage(final Date modStartDate, final Date modEndDate, final long startIndex) {
    final String url = String.format("%scves/2.0?noRejected&startIndex=%d&lastModStartDate=%s&lastModEndDate=%s",
            nvdServiceUrl, startIndex, DateUtil.formatCveApiDate(modStartDate), DateUtil.formatCveApiDate(modEndDate));
    return get(url);
  }
}
