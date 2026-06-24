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
package org.midnightbsd.advisory.ctl.api;

import java.util.Date;
import java.util.List;
import org.midnightbsd.advisory.dto.AdvisoryDto;
import org.midnightbsd.advisory.services.AdvisoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

/** @author Lucas Holt */
@RestController
@RequestMapping("/api/advisory")
public class AdvisoryController {

  private final AdvisoryService advisoryService;

  @Autowired
  public AdvisoryController(final AdvisoryService advisoryService) {
    this.advisoryService = advisoryService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<AdvisoryDto>> list(Pageable page) {
    return ResponseEntity.ok(advisoryService.get(page).map(AdvisoryDto::from));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AdvisoryDto> get(@PathVariable("id") int id) {
    if (id < 1) return ResponseEntity.badRequest().build();
    var advisory = advisoryService.get(id);
    if (advisory == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(AdvisoryDto.from(advisory));
  }

  @GetMapping("/cve/{cveId}")
  public ResponseEntity<AdvisoryDto> get(@PathVariable("cveId") String cveId) {
    if (!StringUtils.hasText(cveId)) return ResponseEntity.badRequest().build();
    final String cleanedCveId = cveId.trim();
    if (cleanedCveId.isEmpty()) return ResponseEntity.badRequest().build();
    var advisory = advisoryService.getByCveId(cleanedCveId);
    if (advisory == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(AdvisoryDto.from(advisory));
  }

  @GetMapping("product/{name}")
  public ResponseEntity<List<AdvisoryDto>> getbyProduct(@PathVariable("name") String name) {
    if (!StringUtils.hasText(name)) return ResponseEntity.badRequest().build();
    final String cleanedName = name.trim();
    if (cleanedName.isEmpty()) return ResponseEntity.badRequest().build();
    return ResponseEntity.ok(
        advisoryService.getByProduct(cleanedName).stream().map(AdvisoryDto::from).toList());
  }

  @GetMapping("vendor/{name}")
  public ResponseEntity<List<AdvisoryDto>> getbyVendor(@PathVariable("name") String name) {
    if (!StringUtils.hasText(name)) return ResponseEntity.badRequest().build();
    final String cleanedName = name.trim();
    if (cleanedName.isEmpty()) return ResponseEntity.badRequest().build();
    return ResponseEntity.ok(
        advisoryService.getByVendor(cleanedName).stream().map(AdvisoryDto::from).toList());
  }

  @GetMapping("vendor/{name}/product/{product}")
  public ResponseEntity<List<AdvisoryDto>> getbyProduct(
      @PathVariable("name") String name,
      @PathVariable("product") String product,
      @RequestParam(required = false, name = "startDate")
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date startDate) {
    if (!StringUtils.hasText(name) || !StringUtils.hasText(product)) {
      return ResponseEntity.badRequest().build();
    }
    final String cleanedName = name.trim();
    final String cleanedProduct = product.trim();
    if (cleanedName.isEmpty() || cleanedProduct.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(
        advisoryService.getByVendorAndProduct(cleanedName, cleanedProduct, startDate).stream()
            .map(AdvisoryDto::from)
            .toList());
  }
}
