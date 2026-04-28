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

import java.util.List;
import org.midnightbsd.advisory.dto.ProductDto;
import org.midnightbsd.advisory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** @author Lucas Holt */
@RestController
@RequestMapping("/api/product")
public class ProductController {

  private final ProductRepository productRepository;

  @Autowired
  public ProductController(final ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @GetMapping
  public ResponseEntity<Page<ProductDto>> list(final Pageable page) {
    return ResponseEntity.ok(productRepository.findAll(page).map(ProductDto::from));
  }

  @GetMapping("/{idOrVersion}")
  public ResponseEntity<List<ProductDto>> get(
      @PathVariable("idOrVersion") final String idOrVersion) {
    try {
      int id = Integer.parseInt(idOrVersion);
      return productRepository
          .findById(id)
          .map(p -> ResponseEntity.ok(List.of(ProductDto.from(p))))
          .orElseGet(() -> ResponseEntity.notFound().build());
    } catch (NumberFormatException e) {
      List<ProductDto> results =
          productRepository.findByVersion(idOrVersion).stream().map(ProductDto::from).toList();
      return results.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(results);
    }
  }

  @GetMapping("/name/{name}/version/{version}")
  public ResponseEntity<ProductDto> get(
      @PathVariable("name") final String name, @PathVariable("version") final String version) {
    var product = productRepository.findByNameAndVersion(name, version);
    if (product == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(ProductDto.from(product));
  }
}
