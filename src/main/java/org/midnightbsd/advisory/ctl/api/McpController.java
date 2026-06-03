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
package org.midnightbsd.advisory.ctl.api;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.dto.AdvisoryDto;
import org.midnightbsd.advisory.dto.AdvisorySummaryDto;
import org.midnightbsd.advisory.dto.McpScanSessionDto;
import org.midnightbsd.advisory.dto.McpToolDto;
import org.midnightbsd.advisory.dto.PackageQuery;
import org.midnightbsd.advisory.dto.PackageVulnerability;
import org.midnightbsd.advisory.model.search.NvdItem;
import org.midnightbsd.advisory.services.McpService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import us.springett.parsers.cpe.exceptions.CpeParsingException;

/**
 * Model Context Protocol facing API. Provides discrete, tool-shaped endpoints an MCP client can use
 * to look up a CVE, search advisories, resolve a (partial) CPE, check whether installed packages are
 * vulnerable, and stream large result sets / operating-system-wide scans via Server-Sent Events.
 *
 * @author Lucas Holt
 */
@Slf4j
@RestController
@RequestMapping("/api/mcp")
public class McpController {

  private static final long STREAM_TIMEOUT_MS = 30 * 60 * 1000L;
  private static final int PROGRESS_FLUSH_EVERY = 25;
  private static final int MAX_STREAM_PAGES = 50;

  private final McpService mcpService;
  private final ExecutorService streamExecutor;

  public McpController(
      final McpService mcpService,
      @Qualifier("mcpStreamExecutor") final ExecutorService streamExecutor) {
    this.mcpService = mcpService;
    this.streamExecutor = streamExecutor;
  }

  /* ---------------------------------------------------------------- discovery */

  /** Self-describing manifest of the tools this MCP endpoint exposes. */
  @GetMapping(value = "/tools", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<McpToolDto>> tools() {
    return ResponseEntity.ok(
        List.of(
            new McpToolDto(
                "get_cve",
                "Fetch full detail for a single CVE id.",
                "GET",
                "/api/mcp/cve/{cveId}",
                false,
                List.of("cveId")),
            new McpToolDto(
                "search_advisories",
                "Free-text search over CVE id and description (paged).",
                "GET",
                "/api/mcp/search",
                false,
                List.of("term", "page", "size")),
            new McpToolDto(
                "stream_search",
                "Stream all search results for a term as Server-Sent Events.",
                "GET",
                "/api/mcp/search/stream",
                true,
                List.of("term", "size", "maxPages")),
            new McpToolDto(
                "match_cpe",
                "Match advisories from a (partial) CPE 2.3 identifier.",
                "GET",
                "/api/mcp/cpe",
                false,
                List.of("cpe", "includeVersion", "startDate")),
            new McpToolDto(
                "product_advisories",
                "List advisories that reference a product name.",
                "GET",
                "/api/mcp/product/{name}",
                false,
                List.of("name")),
            new McpToolDto(
                "check_package",
                "Check whether a specific product version is vulnerable.",
                "GET",
                "/api/mcp/product/{name}/version/{version}",
                false,
                List.of("name", "version", "vendor")),
            new McpToolDto(
                "check_packages",
                "Batch-check a list of installed packages for known vulnerabilities.",
                "POST",
                "/api/mcp/check",
                false,
                List.of("name", "version", "vendor")),
            new McpToolDto(
                "stream_check_packages",
                "Stream per-package vulnerability findings for an OS package manifest as SSE; "
                    + "progress is persisted and pollable via /api/mcp/scan/{id}.",
                "POST",
                "/api/mcp/check/stream",
                true,
                List.of("name", "version", "vendor")),
            new McpToolDto(
                "scan_status",
                "Poll the status of a streamed package scan session.",
                "GET",
                "/api/mcp/scan/{id}",
                false,
                List.of("id"))));
  }

  /* ------------------------------------------------------------------ lookups */

  @GetMapping(value = "/cve/{cveId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdvisoryDto> cve(@PathVariable("cveId") final String cveId) {
    final AdvisoryDto dto = mcpService.getCve(cveId);
    return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/search")
  public ResponseEntity<Page<NvdItem>> search(
      @RequestParam("term") final String term, final Pageable page) {
    return ResponseEntity.ok(mcpService.search(term, page));
  }

  @GetMapping(value = "/cpe", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> cpe(
      @RequestParam("cpe") final String cpe,
      @RequestParam(name = "includeVersion", required = false, defaultValue = "false")
          final boolean includeVersion,
      @RequestParam(name = "startDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          final Date startDate) {
    try {
      return ResponseEntity.ok(mcpService.cpeMatch(cpe, includeVersion, startDate));
    } catch (final CpeParsingException e) {
      return ResponseEntity.badRequest().body(Map.of("error", "invalid cpe: " + e.getMessage()));
    }
  }

  @GetMapping(value = "/product/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AdvisorySummaryDto>> product(@PathVariable("name") final String name) {
    return ResponseEntity.ok(mcpService.productSummaries(name));
  }

  @GetMapping(value = "/product/{name}/version/{version}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PackageVulnerability> productVersion(
      @PathVariable("name") final String name,
      @PathVariable("version") final String version,
      @RequestParam(name = "vendor", required = false) final String vendor) {
    return ResponseEntity.ok(mcpService.checkPackage(new PackageQuery(name, version, vendor)));
  }

  @PostMapping(
      value = "/check",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<PackageVulnerability>> check(
      @RequestBody final List<PackageQuery> packages) {
    if (packages == null || packages.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(packages.stream().map(mcpService::checkPackage).toList());
  }

  /* ---------------------------------------------------------------- streaming */

  /** Stream every search result for a term as SSE events named {@code result}. */
  @GetMapping(value = "/search/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamSearch(
      @RequestParam("term") final String term,
      @RequestParam(name = "size", required = false, defaultValue = "50") final int size,
      @RequestParam(name = "maxPages", required = false, defaultValue = "50") final int maxPages) {
    final SseEmitter emitter = new SseEmitter(STREAM_TIMEOUT_MS);
    final int pageSize = Math.clamp(size, 1, 200);
    final int pageLimit = Math.clamp(maxPages, 1, MAX_STREAM_PAGES);

    streamExecutor.execute(
        () -> {
          try {
            long sent = 0;
            for (int pageNo = 0; pageNo < pageLimit; pageNo++) {
              final Page<NvdItem> page = mcpService.search(term, PageRequest.of(pageNo, pageSize));
              for (final NvdItem item : page.getContent()) {
                emitter.send(SseEmitter.event().name("result").data(item));
                sent++;
              }
              if (page.isLast() || page.getContent().isEmpty()) {
                break;
              }
            }
            emitter.send(SseEmitter.event().name("complete").data(Map.of("count", sent)));
            emitter.complete();
          } catch (final IOException e) {
            log.debug("Search stream closed by client for term {}", term);
            emitter.complete();
          } catch (final Exception e) {
            log.error("Search stream failed for term {}", term, e);
            emitter.completeWithError(e);
          }
        });
    return emitter;
  }

  /**
   * Stream per-package vulnerability findings for a (potentially large) list of installed packages.
   * A scan session is persisted so a client can poll {@code /api/mcp/scan/{id}} for progress; the
   * first SSE event ({@code session}) carries that id.
   */
  @PostMapping(value = "/check/stream", consumes = MediaType.APPLICATION_JSON_VALUE)
  public SseEmitter streamCheck(
      @RequestBody final List<PackageQuery> packages,
      @RequestHeader(value = "User-Agent", required = false) final String userAgent) {
    final SseEmitter emitter = new SseEmitter(STREAM_TIMEOUT_MS);
    if (packages == null || packages.isEmpty()) {
      emitter.completeWithError(new IllegalArgumentException("no packages supplied"));
      return emitter;
    }

    final String sessionId = mcpService.createScanSession(packages.size(), userAgent);

    streamExecutor.execute(
        () -> {
          int processed = 0;
          int vulnerable = 0;
          try {
            mcpService.markRunning(sessionId);
            emitter.send(
                SseEmitter.event()
                    .name("session")
                    .data(Map.of("sessionId", sessionId, "total", packages.size())));

            for (final PackageQuery pkg : packages) {
              final PackageVulnerability result = mcpService.checkPackage(pkg);
              processed++;
              if (result.vulnerable()) {
                vulnerable++;
              }
              emitter.send(SseEmitter.event().name("result").data(result));
              if (processed % PROGRESS_FLUSH_EVERY == 0) {
                mcpService.recordProgress(sessionId, processed, vulnerable);
              }
            }

            mcpService.completeScanSession(sessionId, processed, vulnerable);
            emitter.send(
                SseEmitter.event()
                    .name("complete")
                    .data(
                        Map.of(
                            "sessionId", sessionId,
                            "processed", processed,
                            "vulnerable", vulnerable)));
            emitter.complete();
          } catch (final IOException e) {
            log.debug("Check stream {} closed by client", sessionId);
            mcpService.recordProgress(sessionId, processed, vulnerable);
            emitter.complete();
          } catch (final Exception e) {
            log.error("Check stream {} failed", sessionId, e);
            mcpService.failScanSession(sessionId, e.getMessage());
            emitter.completeWithError(e);
          }
        });
    return emitter;
  }

  @GetMapping(value = "/scan/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<McpScanSessionDto> scan(@PathVariable("id") final String id) {
    final McpScanSessionDto dto = mcpService.getScanSession(id);
    return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
  }
}