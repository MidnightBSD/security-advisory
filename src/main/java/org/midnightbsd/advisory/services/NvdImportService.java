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
        for (Cve cve : cveData.getItems()) {
            Advisory advisory = new Advisory();

            advisory.setCveId(cve.getDataMeta().getId());
            advisory.setDescription(cve.getDescription().getDescriptionData().getValue());

            Set<Product> advProducts = new HashSet<>();

            for (VendorData vendorData : cve.getAffects().getVendor().getVendorData()) {
                Vendor v = vendorRepository.findOneByName(vendorData.getVendorName());
                if (v == null) {
                    v = new Vendor();
                    v.setName(vendorData.getVendorName());
                    v = vendorRepository.saveAndFlush(v);
                }


                for (ProductData pd : vendorData.getProduct().getProductData()) {
                    for (VersionData vd : pd.getVersion().getVersionData()) {
                        Product product = productRepository.findByNameAndVersion(pd.getProductName(), vd.getVersion_value());
                        if (product == null) {
                            product = new Product();
                            product.setName(pd.getProductName());
                            product.setVersion(vd.getVersion_value());
                            product.setVendor(v);
                            product = productRepository.saveAndFlush(product);
                        }

                        advProducts.add(product);
                    }
                }
            }

            advisory.setProducts(advProducts);
            advisoryService.save(advisory);
        }
    }
}
