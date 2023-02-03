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


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.ConfigNode;
import org.midnightbsd.advisory.model.ConfigNodeCpe;
import org.midnightbsd.advisory.model.Product;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.model.nvd.*;
import org.midnightbsd.advisory.repository.ConfigNodeCpeRepository;
import org.midnightbsd.advisory.repository.ConfigNodeRepository;
import org.midnightbsd.advisory.repository.ProductRepository;
import org.midnightbsd.advisory.repository.VendorRepository;
import org.midnightbsd.advisory.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** @author Lucas Holt */
@Slf4j
@Service
public class NvdImportService {

  @Autowired private AdvisoryService advisoryService;

  @Autowired private VendorRepository vendorRepository;

  @Autowired private ProductRepository productRepository;

  @Autowired private ConfigNodeRepository configNodeRepository;

  @Autowired private ConfigNodeCpeRepository configNodeCpeRepository;

  private String getProblemType(final Cve cve) {
    final StringBuilder sb = new StringBuilder();
    for (final ProblemTypeData ptd : cve.getProblemType().getProblemTypeData()) {
      for (final ProblemTypeDataDescription dd : ptd.getDescription()) {
        sb.append(dd.getValue()).append(",");
      }
    }
    return sb.toString();
  }

  private Vendor createOrFetchVendor(final VendorData vendorData) {
    Vendor v = vendorRepository.findOneByName(vendorData.getVendorName());
    if (v == null) {
      v = new Vendor();
      v.setName(vendorData.getVendorName());
      v = vendorRepository.saveAndFlush(v);
    }
    return v;
  }

  private Product createOrFetchProduct(final ProductData pd, final VersionData vd, final Vendor v) {
    Product product =
        productRepository.findByNameAndVersionAndVendor(
            pd.getProductName(), vd.getVersionValue(), v);
    if (product == null) {
      product = new Product();
      product.setName(pd.getProductName());
      product.setVersion(vd.getVersionValue());
      product.setVendor(v);
      product = productRepository.saveAndFlush(product);
    }
    return product;
  }

  private Set<Product> processVendorAndProducts(final Cve cve) {
    final Set<Product> advProducts = new HashSet<>();
    if (cve.getAffects() == null || cve.getAffects().getVendor() == null) return advProducts;

    log.info("Vendor count: {}", cve.getAffects().getVendor().getVendorData().size());

    for (final VendorData vendorData : cve.getAffects().getVendor().getVendorData()) {
      final Vendor v = createOrFetchVendor(vendorData);

      log.info("Product count {}", vendorData.getProduct().getProductData().size());
      for (final ProductData pd : vendorData.getProduct().getProductData()) {
        for (final VersionData vd : pd.getVersion().getVersionData()) {
          advProducts.add(createOrFetchProduct(pd, vd, v));
        }
      }
    }

    return advProducts;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void importNvd(final CveDataPage cveDataPage) {
    var cveData = cveDataPage.getResult();
    if (cveData == null) throw new IllegalArgumentException("cveData");

    if (cveData.getCveItems() == null || cveData.getCveItems().isEmpty())
      throw new IllegalArgumentException("cveData.getItems()");

    for (final CveItem cveItem : cveData.getCveItems()) {
      final Cve cve = cveItem.getCve();
      Advisory advisory = new Advisory();

      if (cve.getCveDataMeta() == null) {
        log.warn("invalid metadata");
        continue;
      }

      advisory.setCveId(cve.getCveDataMeta().getID());
      log.info("Processing {}", advisory.getCveId());

      if (cve.getProblemType() != null && cve.getProblemType().getProblemTypeData() != null) {
        advisory.setProblemType(getProblemType(cve));
      }

      advisory.setPublishedDate(DateUtil.getCveApiDate(cveItem.getPublishedDate()));
      advisory.setLastModifiedDate(DateUtil.getCveApiDate(cveItem.getLastModifiedDate()));

      if (cve.getDescription() != null && cve.getDescription().getDescriptionData() != null) {
        for (final DescriptionData descriptionData : cve.getDescription().getDescriptionData()) {
          if (descriptionData.getLang().equalsIgnoreCase("en"))
            advisory.setDescription(descriptionData.getValue());
        }
      }

      // determine severity
      if (cveItem.getImpact() != null && cveItem.getImpact().getBaseMetricV2() != null) {
        advisory.setSeverity(cveItem.getImpact().getBaseMetricV2().getSeverity());
      }

      advisory.setProducts(processVendorAndProducts(cve));
      advisory = advisoryService.save(advisory);

      // now save configurations
      if (cveItem.getConfigurations() != null && cveItem.getConfigurations().getNodes() != null) {
        log.info("Now save configurations for {}", advisory.getCveId());
        for (final Node node : cveItem.getConfigurations().getNodes()) {
          if (node.getOperator() != null) {
            ConfigNode configNode = new ConfigNode();
            configNode.setAdvisory(advisory);
            configNode.setOperator(node.getOperator());

            configNode = configNodeRepository.save(configNode); // save top level item

            if (node.getCpe() != null) {
              for (final NodeCpe nodeCpe : node.getCpe()) {
                final ConfigNodeCpe cpe = new ConfigNodeCpe();

                cpe.setCpe22Uri(nodeCpe.getCpe22Uri());
                cpe.setCpe23Uri(nodeCpe.getCpe23Uri());
                cpe.setVulnerable(nodeCpe.getVulnerable());
                cpe.setConfigNode(configNode);

                configNodeCpeRepository.save(cpe);
              }
            }

            if (node.getChildren() != null) {
              for (final Node childNode : node.getChildren()) {
                if (childNode.getOperator() != null) {
                  final ConfigNode cn = new ConfigNode();
                  cn.setAdvisory(advisory);
                  cn.setOperator(node.getOperator());
                  cn.setParentId(configNode.getId());
                  configNodeRepository.save(cn);

                  if (childNode.getCpe() != null) {
                    for (final NodeCpe nodeCpe : childNode.getCpe()) {
                      final ConfigNodeCpe cpe = new ConfigNodeCpe();

                      cpe.setCpe22Uri(nodeCpe.getCpe22Uri());
                      cpe.setCpe23Uri(nodeCpe.getCpe23Uri());
                      cpe.setVulnerable(nodeCpe.getVulnerable());
                      cpe.setConfigNode(cn);

                      configNodeCpeRepository.save(cpe);
                    }
                  }

                  // currently, do not support child-child nodes.

                  try {
                    Thread.sleep(500L);
                  } catch (InterruptedException e) {
                    log.error("Issue sleeping during nvd import", e);
                  }
                }
              }
            }
          }
        }

        configNodeRepository.flush();
        configNodeCpeRepository.flush();

        try {
          Thread.sleep(200L);
        } catch (InterruptedException e) {
          log.error("Issue sleeping during nvd import", e);
        }
      }
    }
  }
}
