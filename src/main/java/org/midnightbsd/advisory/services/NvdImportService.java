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
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.*;
import org.midnightbsd.advisory.model.nvd2.*;
import org.midnightbsd.advisory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
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

  @Autowired(required = false)
  private PlatformTransactionManager transactionManager;

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

    if (cve == null || cve.getConfigurations() == null)
      return advProducts;

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

  public void importVulnerability(final Vulnerability vulnerability) {
    final ImportResult importResult;
    if (transactionManager == null) {
      importResult = importVulnerabilityTransaction(vulnerability);
    } else {
      final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
      transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
      importResult = transactionTemplate.execute(status -> importVulnerabilityTransaction(vulnerability));
    }

    if (importResult != null && importResult.indexAfterCommit()) {
      searchIndex(advisoryService.get(importResult.advisoryId()));
    }
  }

  private ImportResult importVulnerabilityTransaction(final Vulnerability vulnerability) {
      final Cve cve = vulnerability.getCve();
      Advisory advisory = new Advisory();

      var a = advisoryService.getByCveId(vulnerability.getCve().getId());
      if (a !=null) {
        log.warn("Advisory {} already exists", vulnerability.getCve().getId());
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

      if (a == null) {
        advisory.setProducts(processVendorAndProducts(cve));
        advisory = advisoryService.saveWithoutIndex(advisory);
      } else {
        advisory.setProducts(mergeProducts(a.getProducts(), processVendorAndProducts(cve)));
        final boolean advisoryUpdate = hasAdvisoryUpdates(a, advisory);
        final boolean metricsUpdated = refreshCvssMetrics3(cve, a);
        final boolean configurationsUpdated = refreshConfigurations(cve, a);
        advisory = advisoryService.saveWithoutIndex(advisory);
        if (advisoryUpdate || metricsUpdated || configurationsUpdated) {
          return ImportResult.index(advisory.getId());
        }
        return ImportResult.none();
      }

      refreshCvssMetrics3(cve, advisory);

      refreshConfigurations(cve, advisory);

      // Fetch and index after this write transaction commits so the DB connection is released first.
      return ImportResult.index(advisory.getId());
  }

  private boolean refreshCvssMetrics3(final Cve cve, final Advisory advisory) {
    if (cve.getMetrics() == null || CollectionUtils.isEmpty(cve.getMetrics().getCvssMetricV31())) {
      return false;
    }

    if (advisory.getId() != 0) {
      cvssMetrics3Repository.deleteByAdvisoryId(advisory.getId());
      cvssMetrics3Repository.flush();
    }

    final Set<CvssMetrics3> cvssMetrics3 = new HashSet<>();
    for (var metric : cve.getMetrics().getCvssMetricV31()) {
      cvssMetrics3.add(cvssMetric(metric, advisory));
    }
    cvssMetrics3Repository.saveAllAndFlush(cvssMetrics3);
    return true;
  }

  private static CvssMetrics3 cvssMetric(
      final CvssMetricV31 metric, final Advisory advisory) {
    final CvssMetrics3 metrics3 = new CvssMetrics3();
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
    metrics3.setPrivilegesRequired(metric.getCvssData().getPrivilegesRequired());
    metrics3.setAdvisory(advisory);
    return metrics3;
  }

  private boolean refreshConfigurations(final Cve cve, final Advisory advisory) {
    if (cve.getConfigurations() == null) {
      return false;
    }

    final List<ConfigNode> existingNodes = configNodeRepository.findByAdvisoryId(advisory.getId());
    if (existingNodes != null && !existingNodes.isEmpty()) {
      configNodeCpeRepository.deleteByConfigNodeIn(existingNodes);
      configNodeCpeRepository.flush();
      configNodeRepository.deleteAll(existingNodes);
      configNodeRepository.flush();
    }

    log.info("Now save configurations for {}", advisory.getCveId());
    for (Configuration configuration : cve.getConfigurations()) {
      if (configuration.getNodes() != null) {
        for (final Node node : configuration.getNodes()) {
          if (node.getOperator() != null) {
            ConfigNode configNode = new ConfigNode();
            configNode.setAdvisory(advisory);
            configNode.setOperator(node.getOperator());
            configNode.setNegate(node.getNegate());
            configNode = configNodeRepository.saveAndFlush(configNode); // save top level item

            cpe(node, configNode);
          }
        }
      }
    }
    return true;
  }

  private void cpe(Node node, ConfigNode configNode) {
    try {
      if (node.getCpeMatch() == null) {
        return;
      }
      for (final CpeMatch nodeCpe : node.getCpeMatch()) {
        final ConfigNodeCpe cpe = new ConfigNodeCpe();
        cpe.setCpe23Uri(nodeCpe.getCriteria());
        cpe.setVulnerable(nodeCpe.getVulnerable());
        cpe.setMatchCriteriaId(nodeCpe.getMatchCriteriaId());
        cpe.setConfigNode(configNode);
        cpe.setVersionEndExcluding(nodeCpe.getVersionEndExcluding());
        cpe.setVersionStartExcluding(nodeCpe.getVersionStartExcluding());
        cpe.setVersionStartIncluding(nodeCpe.getVersionStartIncluding());
        cpe.setVersionEndIncluding(nodeCpe.getVersionEndIncluding());

        configNodeCpeRepository.save(cpe);
      }
      configNodeCpeRepository.flush();
    } catch (Exception e) {
      log.error("Unable to save CPE: {}", node.getCpeMatch(), e);
    }
  }

  private Set<Product> mergeProducts(
      final Set<Product> existingProducts, final Set<Product> newProducts) {
    final Set<Product> merged = new HashSet<>();
    if (existingProducts != null) {
      merged.addAll(existingProducts);
    }
    if (newProducts == null) {
      return merged;
    }
    for (final Product product : newProducts) {
      if (merged.stream().noneMatch(existingProduct -> sameProduct(existingProduct, product))) {
        merged.add(product);
      }
    }
    return merged;
  }

  private static boolean hasAdvisoryUpdates(final Advisory existingAdvisory, final Advisory advisory) {
    return different(advisory.getDescription(), existingAdvisory.getDescription())
        || different(advisory.getLastModifiedDate(), existingAdvisory.getLastModifiedDate())
        || different(advisory.getPublishedDate(), existingAdvisory.getPublishedDate())
        || different(advisory.getSeverity(), existingAdvisory.getSeverity())
        || different(advisory.getProblemType(), existingAdvisory.getProblemType())
        || !sameProducts(existingAdvisory.getProducts(), advisory.getProducts());
  }

  private static boolean sameProducts(final Set<Product> left, final Set<Product> right) {
    if (left == null || left.isEmpty()) {
      return right == null || right.isEmpty();
    }
    if (right == null || left.size() != right.size()) {
      return false;
    }
    return left.stream()
        .allMatch(
            leftProduct ->
                right.stream().anyMatch(rightProduct -> sameProduct(leftProduct, rightProduct)));
  }

  private static boolean different(final Object left, final Object right) {
    if (left == null) {
      return false;
    }
    return !left.equals(right);
  }

  private static boolean sameProduct(final Product left, final Product right) {
    return equalsIgnoreCase(left.getName(), right.getName())
        && equalsIgnoreCase(left.getVersion(), right.getVersion())
        && sameVendor(left.getVendor(), right.getVendor());
  }

  private static boolean sameVendor(final Vendor left, final Vendor right) {
    if (left == null || right == null) {
      return left == right;
    }
    return equalsIgnoreCase(left.getName(), right.getName());
  }

  private static boolean equalsIgnoreCase(final String left, final String right) {
    if (left == null || right == null) {
      return left == right;
    }
    return left.equalsIgnoreCase(right);
  }

  private void searchIndex(Advisory advisory) {
    if (advisory == null) {
      return;
    }
    try {
      log.info("Attempt to ES index CVE ID: {}", advisory.getCveId());
      searchService.index(advisory);
    } catch (Exception e) {
      log.error("Issue indexing advisory {}", advisory.getCveId(), e);
    }
  }

  private record ImportResult(Integer advisoryId, boolean indexAfterCommit) {

    private static ImportResult index(final int advisoryId) {
      return new ImportResult(advisoryId, true);
    }

    private static ImportResult none() {
      return new ImportResult(null, false);
    }
  }
}
