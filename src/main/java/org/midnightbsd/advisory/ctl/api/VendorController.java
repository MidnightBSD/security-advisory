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

import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.services.VendorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** @author Lucas Holt */
@RestController
@RequestMapping("/api/vendor")
public class VendorController {

  private final VendorService vendorService;

  public VendorController(final VendorService vendorService) {
    this.vendorService = vendorService;
  }

  @GetMapping(value = {"", "/"})
  public ResponseEntity<Page<Vendor>> list(Pageable page) {
    return ResponseEntity.ok(vendorService.get(page));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Vendor> get(@PathVariable("id") int id) {
    Vendor vendor = vendorService.get(id);
    if (vendor == null)
      return ResponseEntity.notFound().build();

    return ResponseEntity.ok(vendor);
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<Vendor> get(@PathVariable("name") String name) {
    Vendor vendor = vendorService.getByName(name);
    if (vendor == null)
      return ResponseEntity.notFound().build();

    return ResponseEntity.ok(vendor);
  }
}
