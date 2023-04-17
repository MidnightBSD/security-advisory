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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.*;
import org.midnightbsd.advisory.model.nvd2.*;
import org.midnightbsd.advisory.repository.*;
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

  @Autowired private CvssMetrics3Repository cvssMetrics3Repository;

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
        productRepository.findByNameAndVersionAndVendor(cpe.getProduct(), cpe.getVersion(), v);
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
            try {
              Cpe parsed = CpeParser.parse(cpeMatch.getCriteria());
              final Vendor v = createOrFetchVendor(parsed);
              final Product p = createOrFetchProduct(parsed, v);

              if (cpeMatch.getVulnerable()) {
                  advProducts.add(p);
              }
            } catch (CpeParsingException e) {
              log.error("Unable to parse CPE: {}", cpeMatch.getCriteria(), e);
            }
          }
      }
    }

    return advProducts;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void importVulnerability(final Vulnerability vulnerability) {
      final Cve cve = vulnerability.getCve();
      Advisory advisory = new Advisory();

      var a = advisoryService.getByCveId(vulnerability.getCve().getId());
      if (a !=null) {
        // TODO: handle updates
        log.error("Advisory {} already exists", vulnerability.getCve().getId());
        return;
      }

      advisory.setCveId(vulnerability.getCve().getId());
      log.info("Processing {}", advisory.getCveId());

      if (cve.getWeaknesses() != null) {
        advisory.setProblemType(getProblemType(cve));
      }

      advisory.setPublishedDate(cve.getPublished());
      if (cve.getLastModified() == null) {
        advisory.setLastModifiedDate(cve.getPublished());
      } else {
        advisory.setLastModifiedDate(cve.getLastModified());
      }

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

        if (cve.getMetrics() != null && !CollectionUtils.isEmpty(cve.getMetrics().getCvssMetricV31())) {
          var cvssMetrics3 = new HashSet<CvssMetrics3>();
          for (var metric : cve.getMetrics().getCvssMetricV31()) {
            CvssMetrics3 metrics3 = new CvssMetrics3();
            metrics3.setSource(metric.getSource());
            metrics3.setType(metric.getType());
            metrics3.setExploitabilityScore(Double.toString(metric.getExploitabilityScore()));
            metrics3.setImpactScore(Double.toString(metric.getImpactScore()));

            metrics3.setAccessComplexity(metric.getCvssData().getAccessComplexity());
            metrics3.setAccessVector(metric.getCvssData().getAccessVector());
            metrics3.setAuthentication(metric.getCvssData().getAuthentication());
            metrics3.setAvailabilityImpact(metric.getCvssData().getAvailabilityImpact());
            metrics3.setConfidentialityImpact(metric.getCvssData().getConfidentialityImpact());
            metrics3.setIntegrityImpact(metric.getCvssData().getIntegrityImpact());
            metrics3.setAttackVector(metric.getCvssData().getAttackVector());
            metrics3.setVersion(metric.getCvssData().getVersion());
            metrics3.setBaseScore(Double.toString(metric.getCvssData().getBaseScore()));
            metrics3.setBaseSeverity(metric.getCvssData().getBaseSeverity());
            metrics3.setScope(metric.getCvssData().getScope());
            metrics3.setVectorString(metric.getCvssData().getVectorString());
            metrics3.setUserInteraction(metric.getCvssData().getUserInteraction());
            metrics3.setAttackComplexity(metric.getCvssData().getAttackComplexity());
            metrics3.setPrivilegesRequired(metrics3.getPrivilegesRequired());
            metrics3.setAdvisory(advisory);
            cvssMetrics3.add(metrics3);
          }
          cvssMetrics3Repository.saveAllAndFlush(cvssMetrics3);
        }

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
                configNode = configNodeRepository.saveAndFlush(configNode); // save top level item

                cpe(node, configNode);
              }
            }
          }
        }
      }

      searchIndex(advisoryService.get(advisory.getId())); // we fetch it again to pick up configurations.
  }

  private void cpe(Node node, ConfigNode configNode) {
    try {
      for (final CpeMatch nodeCpe : node.getCpeMatch()) {
        final ConfigNodeCpe cpe = new ConfigNodeCpe();
        cpe.setCpe23Uri(nodeCpe.getCriteria());
        cpe.setVulnerable(nodeCpe.getVulnerable());
        cpe.setMatchCriteriaId(nodeCpe.getMatchCriteriaId());
        cpe.setConfigNode(configNode);
        cpe.setVersionEndExcluding(nodeCpe.getVersionEndExcluding());

        configNodeCpeRepository.save(cpe);
      }
      configNodeCpeRepository.flush();
    } catch (Exception e) {
      log.error("Unable to save CPE: {}", node.getCpeMatch(), e);
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
