/*
 * Copyright (c) 2017-2025 Lucas Holt
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
package org.midnightbsd.advisory.ctl;

import org.midnightbsd.advisory.model.search.NvdItem;
import org.midnightbsd.advisory.services.AdvisoryService;
import org.midnightbsd.advisory.services.SearchService;
import org.midnightbsd.advisory.services.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** @author Lucas Holt */
@Controller
@RequestMapping("/")
public final class HomeController {

  private static final int SEARCH_PAGE_SIZE = 10;

  private final VendorService vendorService;
  private final AdvisoryService advisoryService;
  private final SearchService searchService;

  @Autowired
  public HomeController(
      VendorService vendorService, AdvisoryService advisoryService, SearchService searchService) {
    this.vendorService = vendorService;
    this.advisoryService = advisoryService;
    this.searchService = searchService;
  }

  @GetMapping
  public String home(Model model) {
    model.addAttribute("vendors", vendorService.list());
    return "index";
  }

  @GetMapping("/advisory/{vendor}")
  public String advisory(@PathVariable("vendor") String vendor, Model model) {
    model.addAttribute("vendor", vendor);
    model.addAttribute("advisories", advisoryService.getByVendor(vendor));
    return "advisory";
  }

  @GetMapping("/search")
  public String search(
      @RequestParam(value = "term", required = false) String term,
      @RequestParam(value = "page", defaultValue = "0") int page,
      Model model) {
    model.addAttribute("term", term);
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", SEARCH_PAGE_SIZE);
    if (term != null && term.trim().length() >= 3) {
      Page<NvdItem> results =
          searchService.find(term.replace("'", ""), PageRequest.of(page, SEARCH_PAGE_SIZE));
      model.addAttribute("results", results);
    }
    return "search";
  }

  @GetMapping("/privacy")
  public String privacy() {
    return "privacy";
  }
}
