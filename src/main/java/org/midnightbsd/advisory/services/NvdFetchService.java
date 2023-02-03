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


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.midnightbsd.advisory.model.nvd.CveData;
import org.midnightbsd.advisory.model.nvd.CveDataPage;
import org.midnightbsd.advisory.util.CompressUtil;
import org.midnightbsd.advisory.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** @author Lucas Holt */
@Slf4j
@Service
public class NvdFetchService {

  @Value("${nvdfeed.serviceUrl}")
  private String nvdServiceUrl;

  @Autowired private ObjectMapper objectMapper;

  private RestTemplate restTemplate;

  @Autowired
  public NvdFetchService(final RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
  }

  /**
   * In case we ever need to reload ALL data
   *
   * @param startIndex record to start page with
   * @return Single page of records
   */
  public CveDataPage getPage(final long startIndex) {
    final String url = nvdServiceUrl + "cves/1.0?resultsPerPage=2000&startIndex={startIndex}";
    return restTemplate.getForObject(url, CveDataPage.class, startIndex);
  }

  /**
   * Reload data since a specific date
   *
   * @param modStartDate CVE modified start date
   * @param startIndex record to start page with
   * @return Single page of records
   */
  public CveDataPage getPage(final Date modStartDate, final long startIndex) {
    final String url =
        nvdServiceUrl
            + "cves/1.0?resultsPerPage=2000&startIndex={startIndex}&modStartDate={modStartDate}";
    return restTemplate.getForObject(
        url, CveDataPage.class, startIndex, DateUtil.formatCveApiDate(modStartDate));
  }
}
