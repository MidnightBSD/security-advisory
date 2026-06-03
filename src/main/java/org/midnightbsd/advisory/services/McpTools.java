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

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.dto.AdvisoryDto;
import org.midnightbsd.advisory.dto.AdvisorySummaryDto;
import org.midnightbsd.advisory.dto.PackageQuery;
import org.midnightbsd.advisory.dto.PackageVulnerability;
import org.midnightbsd.advisory.model.search.NvdItem;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import us.springett.parsers.cpe.exceptions.CpeParsingException;

/**
 * MCP protocol tool surface. These methods are exported over the Model Context Protocol (Streamable
 * HTTP transport, mounted at {@code /api/mcp}) so MCP clients such as Claude or Codex can call them
 * as tools. They are thin, synchronous wrappers over {@link McpService}.
 *
 * @author Lucas Holt
 */
@Slf4j
@Component
public class McpTools {

  /** Default page size for {@code search_advisories} when the caller does not specify one. */
  private static final int DEFAULT_SEARCH_SIZE = 25;

  private final McpService mcpService;

  public McpTools(final McpService mcpService) {
    this.mcpService = mcpService;
  }

  @Tool(name = "get_cve", description = "Fetch full detail for a single CVE id, or null if untracked.")
  public AdvisoryDto getCve(
      @ToolParam(description = "CVE identifier, e.g. CVE-2021-44228") final String cveId) {
    return mcpService.getCve(cveId);
  }

  @Tool(
      name = "search_advisories",
      description = "Free-text search over CVE id and description; returns one page of matches.")
  public List<NvdItem> searchAdvisories(
      @ToolParam(description = "Search term, e.g. a product name or partial CVE id")
          final String term,
      @ToolParam(required = false, description = "Zero-based page number (default 0)")
          final Integer page,
      @ToolParam(required = false, description = "Results per page, 1-200 (default 25)")
          final Integer size) {
    final int pageNo = page == null ? 0 : Math.max(0, page);
    final int pageSize = size == null ? DEFAULT_SEARCH_SIZE : Math.clamp(size, 1, 200);
    return mcpService.search(term, PageRequest.of(pageNo, pageSize)).getContent();
  }

  @Tool(
      name = "match_cpe",
      description = "Match advisories from a (partial) CPE 2.3 identifier (vendor/product[/version]).")
  public List<AdvisoryDto> matchCpe(
      @ToolParam(description = "CPE 2.3 string, e.g. cpe:2.3:a:apache:log4j") final String cpe,
      @ToolParam(
              required = false,
              description = "Evaluate the version range too when true (default false)")
          final Boolean includeVersion) {
    try {
      return mcpService.cpeMatch(cpe, Boolean.TRUE.equals(includeVersion), null);
    } catch (final CpeParsingException e) {
      throw new IllegalArgumentException("invalid cpe: " + e.getMessage(), e);
    }
  }

  @Tool(
      name = "product_advisories",
      description = "List advisory summaries that reference a product by name.")
  public List<AdvisorySummaryDto> productAdvisories(
      @ToolParam(description = "Product name, e.g. openssl") final String name) {
    return mcpService.productSummaries(name);
  }

  @Tool(
      name = "check_package",
      description =
          "Check whether a single installed package is vulnerable. Version-aware when supplied; "
              + "vendor is optional and, when omitted, all vendors shipping the product are checked.")
  public PackageVulnerability checkPackage(
      @ToolParam(description = "Product name, e.g. openssl") final String name,
      @ToolParam(required = false, description = "Installed version, e.g. 3.0.1") final String version,
      @ToolParam(required = false, description = "Vendor name, e.g. openssl") final String vendor) {
    return mcpService.checkPackage(new PackageQuery(name, version, vendor));
  }

  @Tool(
      name = "check_packages",
      description = "Batch-check a list of installed packages for known vulnerabilities.")
  public List<PackageVulnerability> checkPackages(
      @ToolParam(description = "Packages to check (name required; version and vendor optional)")
          final List<PackageQuery> packages) {
    if (packages == null || packages.isEmpty()) {
      return List.of();
    }
    return packages.stream().map(mcpService::checkPackage).toList();
  }
}

