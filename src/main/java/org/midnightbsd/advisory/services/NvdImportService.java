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


import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.ConfigNode;
import org.midnightbsd.advisory.model.ConfigNodeCpe;
import org.midnightbsd.advisory.model.Product;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.model.nvd2.*;
import org.midnightbsd.advisory.repository.ConfigNodeCpeRepository;
import org.midnightbsd.advisory.repository.ConfigNodeRepository;
import org.midnightbsd.advisory.repository.ProductRepository;
import org.midnightbsd.advisory.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import us.springett.parsers.cpe.Cpe;
import us.springett.parsers.cpe.CpeParser;
import us.springett.parsers.cpe.exceptions.CpeParsingException;

/** @author Lucas Holt */
@Slf4j
@Service
public class NvdImportService {

  @Autowired private AdvisoryService advisoryService;

  @Autowired private VendorRepository vendorRepository;

  @Autowired private ProductRepository productRepository;

  @Autowired private ConfigNodeRepository configNodeRepository;

  @Autowired private ConfigNodeCpeRepository configNodeCpeRepository;

  @Autowired private SearchService searchService;

  private String getProblemType(final Cve cve) {
    final StringBuilder sb = new StringBuilder();
    for (final Weakness weakness : cve.getWeaknesses()) {
      for (final Description dd : weakness.getDescription()) {
        sb.append(dd.getValue()).append(",");
      }
    }
    return sb.toString();
  }

  private Vendor createOrFetchVendor(final Cpe cpe) {
    Vendor v = vendorRepository.findOneByName(cpe.getVendor());
    if (v == null) {
      v = new Vendor();
      v.setName(cpe.getVendor());
      v = vendorRepository.saveAndFlush(v);
    }
    return v;
  }

  private Product createOrFetchProduct(final Cpe cpe, final Vendor v) {
    Product product =
        productRepository.findByNameAndVersionAndVendor(
            cpe.getProduct(), cpe.getVersion(), v);
    if (product == null) {
      product = new Product();
      product.setName(cpe.getProduct());
      product.setVersion(cpe.getVersion());
      product.setVendor(v);
      product = productRepository.saveAndFlush(product);
    }
    return product;
  }

  private Set<Product> processVendorAndProducts(final Cve cve) {
    final Set<Product> advProducts = new HashSet<>();

    if (cve == null || cve.getConfigurations() == null) return advProducts;

    for (Configuration configuration : cve.getConfigurations() ) {
      for (var node: configuration.getNodes()) {
        if (node.getCpeMatch() == null)
          continue;

        for (var cpeMatch: node.getCpeMatch()) {
          if (cpeMatch.getVulnerable()) {
            try {
              Cpe parsed = CpeParser.parse(cpeMatch.getCriteria());
              final Vendor v = createOrFetchVendor(parsed);
              advProducts.add(createOrFetchProduct(parsed, v));
            } catch (CpeParsingException e) {
              log.error("Unable to parse CPE: {}", cpeMatch.getCriteria(), e);
            }
          }
        }
      }
    }

    return advProducts;
  }

  private void sanityCheck(Root root) {
    if (root == null) throw new IllegalArgumentException("root");

    if (CollectionUtils.isEmpty(root.getVulnerabilities()))
      throw new IllegalArgumentException("root.getVulnerabilities()");
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void importNvd(final Root root) {
    sanityCheck(root);

    for (final Vulnerability vulnerability : root.getVulnerabilities()) {
      final Cve cve = vulnerability.getCve();
      Advisory advisory = new Advisory();

      advisory.setCveId(vulnerability.getCve().getId());
      log.info("Processing {}", advisory.getCveId());

      if (cve.getWeaknesses() != null) {
        advisory.setProblemType(getProblemType(cve));
      }

      advisory.setPublishedDate(cve.getPublished());
      advisory.setLastModifiedDate(cve.getLastModified());

      if (cve.getDescriptions() != null) {
        for (final Description descriptionData : cve.getDescriptions()) {
          if (descriptionData.getLang().equalsIgnoreCase("en"))
            advisory.setDescription(descriptionData.getValue());
        }
      }

      // determine severity
      if (cve.getMetrics() != null && !CollectionUtils.isEmpty(cve.getMetrics().getCvssMetricV2())) {
        advisory.setSeverity(cve.getMetrics().getCvssMetricV2().get(0).getBaseSeverity());
      }

      advisory.setProducts(processVendorAndProducts(cve));
      advisory = advisoryService.save(advisory);

      // now save configurations
      if (cve.getConfigurations() != null) {
        log.info("Now save configurations for {}", advisory.getCveId());
        for (Configuration configuration : cve.getConfigurations()) {
          if (configuration.getNodes() != null) {
          for (final Node node : configuration.getNodes()) {
            if (node.getOperator() != null) {
              ConfigNode configNode = new ConfigNode();
              configNode.setAdvisory(advisory);
              configNode.setOperator(node.getOperator());
              configNode = configNodeRepository.save(configNode); // save top level item

              cpe(node, configNode);

              sleep();

            }
            }
          }
        }

        configNodeRepository.flush();
        configNodeCpeRepository.flush();

        sleep();
      }

      searchIndex(advisory);
    }
  }

  private void cpe(Node node, ConfigNode configNode) {
      for (final CpeMatch nodeCpe : node.getCpeMatch()) {
        final ConfigNodeCpe cpe = new ConfigNodeCpe();

        if (nodeCpe.getMatchCriteriaId() == null || nodeCpe.getMatchCriteriaId().isEmpty()) {
          log.warn("No match criteria for {}", nodeCpe.getCriteria());
          continue;
          }

        cpe.setCpe23Uri(nodeCpe.getCriteria());
        cpe.setVulnerable(nodeCpe.getVulnerable());
        cpe.setMatchCriteriaId(nodeCpe.getMatchCriteriaId());
        cpe.setConfigNode(configNode);

        configNodeCpeRepository.save(cpe);
      }
  }

  private void sleep() {
    try {
      Thread.sleep(200L);
    } catch (InterruptedException e) {
      log.error("Issue sleeping during nvd import", e);
      Thread.currentThread().interrupt();
    }
  }

  private void searchIndex(Advisory advisory) {
    try {
      log.info("Attempt to ES index CVE ID: {}", advisory.getCveId());
      searchService.index(advisory);
    } catch (Exception e) {
      log.error("Issue indexing advisory {}", advisory.getCveId(), e);
    }
  }
}
