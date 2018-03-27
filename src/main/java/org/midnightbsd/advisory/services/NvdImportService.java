package org.midnightbsd.advisory.services;

import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.Product;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.model.nvd.*;
import org.midnightbsd.advisory.repository.ProductRepository;
import org.midnightbsd.advisory.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Lucas Holt
 */
@Slf4j
@Service
public class NvdImportService {

    @Autowired
    private AdvisoryService advisoryService;


    //  @Autowired
    // private SearchService searchService;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void importNvd(final CveData cveData) {
        if (cveData == null)
            throw new IllegalArgumentException("cveData");
        if (cveData.getCveItems() == null || cveData.getCveItems().isEmpty())
            throw new IllegalArgumentException("cveData.getItems()");

        for (CveItem cveItem : cveData.getCveItems()) {
            Cve cve = cveItem.getCve();
            Advisory advisory = new Advisory();

            if (cve.getCveDataMeta() == null) {
                log.warn("invalid meta data");
                //       continue;
            } else {
                advisory.setCveId(cve.getCveDataMeta().getID());
            }

            if (cve.getProblemType() != null) {
                String problem = "";
                for (ProblemTypeData ptd : cve.getProblemType().getProblemTypeData()) {
                    for (ProblemTypeDataDescription dd : ptd.getDescription()) {
                        problem += dd.getValue() + ",";
                    }
                }
                advisory.setProblemType(problem);
            }

            if (cve.getDescription() != null && cve.getDescription().getDescriptionData() != null) {

                for(DescriptionData descriptionData : cve.getDescription().getDescriptionData()) {
                   if (descriptionData.getLang().equalsIgnoreCase("en"))
                        advisory.setDescription(descriptionData.getValue());
                }
            }

            Set<Product> advProducts = new HashSet<>();

            if (cve.getAffects() != null && cve.getAffects().getVendor() != null) {
                for (VendorData vendorData : cve.getAffects().getVendor().getVendorData()) {
                    Vendor v = vendorRepository.findOneByName(vendorData.getVendorName());
                    if (v == null) {
                        v = new Vendor();
                        v.setName(vendorData.getVendorName());
                        v = vendorRepository.saveAndFlush(v);
                    }


                    for (ProductData pd : vendorData.getProduct().getProductData()) {
                        for (VersionData vd : pd.getVersion().getVersionData()) {
                            Product product = productRepository.findByNameAndVersion(pd.getProductName(), vd.getVersionValue());
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
            advisoryService.save(advisory);
        }
    }
}
