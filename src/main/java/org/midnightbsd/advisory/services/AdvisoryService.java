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


import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.Product;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.midnightbsd.advisory.repository.ProductRepository;
import org.midnightbsd.advisory.repository.VendorRepository;
import org.midnightbsd.advisory.util.VersionCompareUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import us.springett.parsers.cpe.Cpe;
import us.springett.parsers.cpe.CpeParser;

/** @author Lucas Holt */
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "advisory")
@Slf4j
@Service
public class AdvisoryService implements AppService<Advisory> {

  private final AdvisoryRepository repository;

  private final SearchService searchService;

  private final VendorRepository vendorRepository;

  private final ProductRepository productRepository;

  public AdvisoryService(final AdvisoryRepository repository, VendorRepository vendorRepository, ProductRepository productRepository, final SearchService searchService) {
    this.repository = repository;
    this.vendorRepository = vendorRepository;
    this.productRepository = productRepository;
    this.searchService = searchService;
  }

  public List<Advisory> list() {
    return repository.findAll();
  }

  public List<Advisory> getByProduct(final String productName) {
    return repository.findByProductName(productName);
  }

  @Cacheable(unless = "#result == null", key = "#vendorName")
  public List<Advisory> getByVendor(final String vendorName) {
    return repository.findByVendorName(vendorName);
  }


  public List<Advisory> getByVendorAndProduct(final String vendorName, final String productName, final Date startDate) {
    final List<List<Product>> products = getProducts(vendorName, productName);
    final List<Advisory> results = new ArrayList<>();

    for (final List<Product> smallerList : products) {
      List<Advisory> subset;
      if (startDate == null) {
        subset = repository.findByProductsIn(smallerList);
      } else {
        subset = repository.findByPublishedDateIsAfterProductsIn(startDate, smallerList);
      }
      results.addAll(subset);
    }

    return results;
  }

  /**
   * Implements a partial match algorithm by vendor / product / version combination.
   * Will not deal with AND + parentID relationship with OS or firmware. (if bug x only happens on windows... )
   * @param vendorName
   * @param productName
   * @param version
   * @param startDate
   * @return
   */
  @Transactional
  public List<Advisory> getByVendorAndProductAndVersion(
      final String vendorName,
      final String productName,
      final String version,
      final Date startDate) {

    List<Advisory> advisories = getByVendorAndProduct(vendorName, productName, startDate);
    List<Advisory> pruned = new ArrayList<>();
    for (final Advisory advisory : advisories) {
      boolean skip = false;
      for (var configNode : advisory.getConfigNodes()) {
        if (skip) break;

        for (var configNodeCpe : configNode.getConfigNodeCpes()) {
          try {
            Cpe parsed = CpeParser.parse(configNodeCpe.getCpe23Uri());

            // some records have a AND + parentID relationship with OS or firmware, we are doing partial match here
            if (!parsed.getVendor().equalsIgnoreCase(vendorName) || !parsed.getProduct().equalsIgnoreCase(productName))
              continue;

            if (!Boolean.TRUE.equals(configNodeCpe.getVulnerable())) {
              continue;
            }

            if (StringUtils.hasText(configNodeCpe.getVersionEndExcluding())) {
              if (VersionCompareUtil.compare(configNodeCpe.getVersionEndExcluding(), version)
                  >= 0) {
                skip = true; // not a match, done processing
                break;
              } else {
                pruned.add(advisory);
                skip = true; // done processing, found vulnerable
                break;
              }
            } else {
                if ("*".equals(parsed.getVersion())) {
                  pruned.add(advisory);
                  skip = true; // done
                  break;
                } else if (VersionCompareUtil.compare(parsed.getVersion(), version) >= 0) {
                  pruned.add(advisory);
                  skip = true; // done
                  break;
                }

            }
          } catch (Exception e) {
            log.error("Unable to parse CPE23 URI: {}", configNodeCpe.getCpe23Uri(), e);
          }
        }
      }
    }

    return pruned;
  }

  private List<List<Product>> getProducts(final String vendorName, final String productName) {
    final Vendor vendor = vendorRepository.findOneByName(vendorName);
    return Lists.partition(productRepository.findByNameAndVendor(productName, vendor), 1000);
  }

  public Page<Advisory> get(final Pageable page) {
    return repository.findAll(page);
  }

  public Advisory get(final int id) {
    final Optional<Advisory> advisory = repository.findById(id);
    return advisory.orElse(null);
  }

  public Advisory getByCveId(final String cveId) {
    return repository.findOneByCveId(cveId);
  }

  @CacheEvict(allEntries = true)
  @Transactional
  public void batchSave(final List<Advisory> advisories) {
    log.info("Advisory batch save of {}", advisories.size());

    final List<Advisory> createList = new ArrayList<>();

    for (final Advisory advisory : advisories) {
      Advisory adv = repository.findOneByCveId(advisory.getCveId());
      if (adv == null) {
        createList.add(advisory);
      } else {
        boolean update = false; // dirty check

        log.info("Updating {}", adv.getCveId());

        if (advisory.getDescription() != null
            && !advisory.getDescription().equalsIgnoreCase(adv.getDescription())) {
          adv.setDescription(advisory.getDescription());
          update = true;
        }

        if (advisory.getLastModifiedDate() != null
            && advisory.getLastModifiedDate().compareTo(adv.getLastModifiedDate()) != 0) {
          adv.setLastModifiedDate(advisory.getLastModifiedDate());
          update = true;
        }

        if (advisory.getPublishedDate() != null
            && advisory.getPublishedDate().compareTo(adv.getPublishedDate()) != 0) {
          adv.setPublishedDate(advisory.getPublishedDate());
          update = true;
        }

        if (advisory.getSeverity() != null
            && !advisory.getSeverity().equalsIgnoreCase(adv.getSeverity())) {
          adv.setSeverity(advisory.getSeverity());
          update = true;
        }

        if (advisory.getProblemType() != null
            && advisory.getProblemType().equalsIgnoreCase(adv.getProblemType())) {
          adv.setProblemType(advisory.getProblemType());
          update = true;
        }

        if (update && advisory.getProducts() != null) {
          log.info("{} contains {} products", adv.getCveId(), advisory.getProducts().size());
          adv.setProducts(advisory.getProducts());
        }

        if (update) {
          adv = repository.save(adv);
          searchService.index(adv);
        }
      }
    }
    repository.flush();

    log.info("Saving {} new advisories", createList.size());

    long result = repository.saveAll(createList).stream().peek(searchService::index).count();
    log.info("Indexed {} new advisories", result);
    repository.flush();
  }

  @CacheEvict(allEntries = true)
  @Transactional
  public Advisory save(final Advisory advisory) {
    Advisory adv = repository.findOneByCveId(advisory.getCveId());
    if (adv == null) {
      log.info("Adding {}", advisory.getCveId());
      return repository.saveAndFlush(advisory);
    }

    boolean update = false; // dirty check

    log.info("Updating {}", adv.getCveId());

    if (advisory.getDescription() != null
        && !advisory.getDescription().equalsIgnoreCase(adv.getDescription())) {
      adv.setDescription(advisory.getDescription());
      update = true;
    }

    if (advisory.getLastModifiedDate() != null
        && (adv.getLastModifiedDate() == null
            || advisory.getLastModifiedDate().compareTo(adv.getLastModifiedDate()) != 0)) {
      adv.setLastModifiedDate(advisory.getLastModifiedDate());
      update = true;
    }

    if (advisory.getPublishedDate() != null
        && (adv.getPublishedDate() == null
            || advisory.getPublishedDate().compareTo(adv.getPublishedDate()) != 0)) {
      adv.setPublishedDate(advisory.getPublishedDate());
      update = true;
    }

    if (advisory.getSeverity() != null
        && !advisory.getSeverity().equalsIgnoreCase(adv.getSeverity())) {
      adv.setSeverity(advisory.getSeverity());
      update = true;
    }

    if (advisory.getProblemType() != null
        && advisory.getProblemType().equalsIgnoreCase(adv.getProblemType())) {
      adv.setProblemType(advisory.getProblemType());
      update = true;
    }

    if (update && advisory.getProducts() != null) {
      log.info("{} contains {} products", adv.getCveId(), advisory.getProducts().size());
      adv.setProducts(advisory.getProducts());
    }

    if (update) {
      adv = repository.saveAndFlush(adv);
      searchService.index(adv);
    }

    return adv;
  }
}
