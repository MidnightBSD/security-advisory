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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.dto.AdvisoryDto;
import org.midnightbsd.advisory.dto.AdvisorySummaryDto;
import org.midnightbsd.advisory.dto.PackageQuery;
import org.midnightbsd.advisory.dto.PackageVulnerability;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.Product;
import org.midnightbsd.advisory.model.search.NvdItem;
import org.midnightbsd.advisory.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import us.springett.parsers.cpe.Cpe;
import us.springett.parsers.cpe.CpeParser;
import us.springett.parsers.cpe.exceptions.CpeParsingException;

/**
 * Business logic backing the MCP tool surface ({@link McpTools}). Every method that builds a DTO
 * from JPA entities does so inside its own transaction, so the resulting DTOs are detached and safe
 * to serialize after the transaction closes.
 *
 * @author Lucas Holt
 */
@Slf4j
@Service
public class McpService {

  private final AdvisoryService advisoryService;
  private final SearchService searchService;
  private final ProductRepository productRepository;

  public McpService(
      final AdvisoryService advisoryService,
      final SearchService searchService,
      final ProductRepository productRepository) {
    this.advisoryService = advisoryService;
    this.searchService = searchService;
    this.productRepository = productRepository;
  }

  /* ------------------------------------------------------------------ lookups */

  /** Full detail for a single CVE, or {@code null} when it is not tracked. */
  @Transactional(readOnly = true)
  public AdvisoryDto getCve(final String cveId) {
    final Advisory advisory = advisoryService.getByCveId(cveId);
    return advisory == null ? null : AdvisoryDto.from(advisory);
  }

  /** Free-text search over indexed CVE id / description. */
  public Page<NvdItem> search(final String term, final Pageable page) {
    return searchService.find(term, page);
  }

  /**
   * Match advisories from a (possibly partial) CPE 2.3 identifier. When {@code includeVersion} is
   * true the vendor/product/version ranges are evaluated, otherwise only vendor/product are used.
   */
  @Transactional(readOnly = true)
  public List<AdvisoryDto> cpeMatch(
      final String cpe, final boolean includeVersion, final Date startDate)
      throws CpeParsingException {
    final Cpe parsed = parseCpe(cpe);
    final List<Advisory> advisories =
        includeVersion
            ? advisoryService.getByVendorAndProductAndVersion(
                parsed.getVendor(), parsed.getProduct(), parsed.getVersion(), startDate)
            : advisoryService.getByVendorAndProduct(
                parsed.getVendor(), parsed.getProduct(), startDate);
    return advisories.stream().map(AdvisoryDto::from).toList();
  }

  /** All advisories that reference a product by name, as lightweight summaries. */
  @Transactional(readOnly = true)
  public List<AdvisorySummaryDto> productSummaries(final String productName) {
    return advisoryService.getByProduct(productName).stream().map(AdvisorySummaryDto::from).toList();
  }

  /**
   * Check a single installed package against the advisory database. The lookup is version aware when
   * a version is supplied; if no vendor is given every vendor that ships a product with the same
   * name is considered.
   */
  @Transactional(readOnly = true)
  public PackageVulnerability checkPackage(final PackageQuery query) {
    final Map<String, AdvisorySummaryDto> byCve = new LinkedHashMap<>();

    if (StringUtils.hasText(query.version())) {
      final List<String> vendors =
          StringUtils.hasText(query.vendor())
              ? List.of(query.vendor())
              : vendorsForProduct(query.name());
      for (final String vendor : vendors) {
        for (final Advisory advisory :
            advisoryService.getByVendorAndProductAndVersion(
                vendor, query.name(), query.version(), null)) {
          byCve.putIfAbsent(advisory.getCveId(), AdvisorySummaryDto.from(advisory));
        }
      }
    } else {
      // No version supplied: report every advisory known for the product name.
      for (final Advisory advisory : advisoryService.getByProduct(query.name())) {
        byCve.putIfAbsent(advisory.getCveId(), AdvisorySummaryDto.from(advisory));
      }
    }

    return PackageVulnerability.of(query, new ArrayList<>(byCve.values()));
  }

  private List<String> vendorsForProduct(final String productName) {
    return productRepository.findByName(productName).stream()
        .map(Product::getVendor)
        .filter(v -> v != null && StringUtils.hasText(v.getName()))
        .map(v -> v.getName())
        .distinct()
        .toList();
  }

  /* ----------------------------------------------------------------- utilities */

  /**
   * Parse a CPE 2.3 string, tolerating identifiers from mports that omit the trailing {@code other}
   * field after an architecture token (mirrors {@code CpeController}).
   */
  Cpe parseCpe(final String cpe) throws CpeParsingException {
    String localCpe = cpe;
    if (cpe.startsWith("cpe:2.3") && (cpe.endsWith("x64") || cpe.endsWith("x86"))) {
      localCpe = cpe + ":0";
    }
    return CpeParser.parse(localCpe);
  }
}
