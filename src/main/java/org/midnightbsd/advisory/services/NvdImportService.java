package org.midnightbsd.advisory.services;

import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.*;
import org.midnightbsd.advisory.model.Product;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.model.nvd.*;
import org.midnightbsd.advisory.repository.ConfigNodeCpeRepository;
import org.midnightbsd.advisory.repository.ConfigNodeRepository;
import org.midnightbsd.advisory.repository.ProductRepository;
import org.midnightbsd.advisory.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author Lucas Holt
 */
@Slf4j
@Service
public class NvdImportService {

    @Autowired
    private AdvisoryService advisoryService;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ConfigNodeRepository configNodeRepository;

    @Autowired
    private ConfigNodeCpeRepository configNodeCpeRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void importNvd(final CveData cveData) {
        if (cveData == null)
            throw new IllegalArgumentException("cveData");
        if (cveData.getCveItems() == null || cveData.getCveItems().isEmpty())
            throw new IllegalArgumentException("cveData.getItems()");

        for (CveItem cveItem : cveData.getCveItems()) {
            final Cve cve = cveItem.getCve();
            Advisory advisory = new Advisory();

            if (cve.getCveDataMeta() == null) {
                log.warn("invalid meta data");
                continue;
            } else {
                advisory.setCveId(cve.getCveDataMeta().getID());

                log.info("Processing " + advisory.getCveId());
            }

            if (cve.getProblemType() != null && cve.getProblemType().getProblemTypeData() != null)  {
                final StringBuilder sb = new StringBuilder();
                for (final ProblemTypeData ptd : cve.getProblemType().getProblemTypeData()) {
                    for (final ProblemTypeDataDescription dd : ptd.getDescription()) {
                        sb.append(dd.getValue()).append(",");
                    }
                }
                advisory.setProblemType(sb.toString());
            }

            advisory.setPublishedDate(convertDate(cveItem.getPublishedDate()));
            advisory.setLastModifiedDate(convertDate(cveItem.getLastModifiedDate()));

            if (cve.getDescription() != null && cve.getDescription().getDescriptionData() != null) {

                for(final DescriptionData descriptionData : cve.getDescription().getDescriptionData()) {
                   if (descriptionData.getLang().equalsIgnoreCase("en"))
                        advisory.setDescription(descriptionData.getValue());
                }
            }

            // determine severity
            if (cveItem.getImpact() != null) {
                if (cveItem.getImpact().getBaseMetricV2() != null) {
                    advisory.setSeverity(cveItem.getImpact().getBaseMetricV2().getSeverity());
                }
            }

            Set<Product> advProducts = new HashSet<>();

            if (cve.getAffects() != null && cve.getAffects().getVendor() != null) {
                log.info("Vendor count: " + cve.getAffects().getVendor().getVendorData().size());
                
                for (final VendorData vendorData : cve.getAffects().getVendor().getVendorData()) {
                    Vendor v = vendorRepository.findOneByName(vendorData.getVendorName());
                    if (v == null) {
                        v = new Vendor();
                        v.setName(vendorData.getVendorName());
                        v = vendorRepository.saveAndFlush(v);
                    }

                    log.info("Product count " +  vendorData.getProduct().getProductData().size());
                    for (final ProductData pd : vendorData.getProduct().getProductData()) {
                        for (final VersionData vd : pd.getVersion().getVersionData()) {
                            Product product = productRepository.findByNameAndVersionAndVendor(pd.getProductName(), vd.getVersionValue(), v);
                            if (product == null) {
                                product = new Product();
                                product.setName(pd.getProductName());
                                product.setVersion(vd.getVersionValue());
                                product.setVendor(v);
                                product = productRepository.saveAndFlush(product);
                            }

                            advProducts.add(product);
                        }
                    }
                }
            }

            advisory.setProducts(advProducts);
            advisory = advisoryService.save(advisory);

            // now save configurations
            if (cveItem.getConfigurations() != null && cveItem.getConfigurations().getNodes() != null) {
                for (Node node : cveItem.getConfigurations().getNodes()) {
                     if (node.getOperator() != null) {
                         ConfigNode configNode = new ConfigNode();
                         configNode.setAdvisory(advisory);
                         configNode.setOperator(node.getOperator());

                         configNode = configNodeRepository.save(configNode); // save top level item

                         if (node.getCpe() != null) {
                             for (NodeCpe nodeCpe : node.getCpe()) {
                                 ConfigNodeCpe cpe = new ConfigNodeCpe();

                                 cpe.setCpe22Uri(nodeCpe.getCpe22Uri());
                                 cpe.setCpe23Uri(nodeCpe.getCpe23Uri());
                                 cpe.setVulnerable(nodeCpe.getVulnerable());
                                 cpe.setConfigNode(configNode);

                                 configNodeCpeRepository.save(cpe);
                             }

                         }

                         if (node.getChildren() != null) {
                             for (Node childNode : node.getChildren()) {
                                 if (childNode.getOperator() != null) {
                                     ConfigNode cn = new ConfigNode();
                                     cn.setAdvisory(advisory);
                                     cn.setOperator(node.getOperator());
                                     cn.setParentId(configNode.getId());
                                     configNodeRepository.save(cn);

                                     if (childNode.getCpe() != null) {
                                         for (NodeCpe nodeCpe : childNode.getCpe()) {
                                             ConfigNodeCpe cpe = new ConfigNodeCpe();

                                             cpe.setCpe22Uri(nodeCpe.getCpe22Uri());
                                             cpe.setCpe23Uri(nodeCpe.getCpe23Uri());
                                             cpe.setVulnerable(nodeCpe.getVulnerable());
                                             cpe.setConfigNode(cn);

                                             configNodeCpeRepository.save(cpe);
                                         }
                                     }

                                    // currently do not support child child nodes.
                                 }
                             }
                         }

                     }
                }

                configNodeRepository.flush();
                configNodeCpeRepository.flush();
            }
        }
    }

    private Date convertDate(final String dt) {
        if (dt == null || dt.isEmpty())
            return null;

        // 2018-02-20T21:29Z

        try {
            SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.US);
            return ISO8601DATEFORMAT.parse(dt);
        } catch (Exception e) {
            log.error("Could not convert " + dt, e);
        }

        return null;
    }
}
