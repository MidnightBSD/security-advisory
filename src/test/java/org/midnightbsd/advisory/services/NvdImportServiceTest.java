package org.midnightbsd.advisory.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.Product;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.model.nvd2.Configuration;
import org.midnightbsd.advisory.model.nvd2.CpeMatch;
import org.midnightbsd.advisory.model.nvd2.Cve;
import org.midnightbsd.advisory.model.nvd2.Node;
import org.midnightbsd.advisory.model.nvd2.Vulnerability;
import org.midnightbsd.advisory.repository.ConfigNodeCpeRepository;
import org.midnightbsd.advisory.repository.ConfigNodeRepository;
import org.midnightbsd.advisory.repository.CvssMetrics3Repository;
import org.midnightbsd.advisory.repository.ProductRepository;
import org.midnightbsd.advisory.repository.VendorRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NvdImportServiceTest {

  @Mock private AdvisoryService advisoryService;

  @Mock private VendorRepository vendorRepository;

  @Mock private ProductRepository productRepository;

  @Mock private ConfigNodeRepository configNodeRepository;

  @Mock private ConfigNodeCpeRepository configNodeCpeRepository;

  @Mock private CvssMetrics3Repository cvssMetrics3Repository;

  @Mock private SearchService searchService;

  @InjectMocks private NvdImportService nvdImportService;

  @Test
  void existingAdvisoryImportMergesNewProductsAndIndexesOnlyThroughAdvisoryServiceSave()
      throws Exception {
    Vendor vendor = new Vendor();
    vendor.setName("vendor");

    Product existingProduct = new Product();
    existingProduct.setName("product");
    existingProduct.setVersion("1.0");
    existingProduct.setVendor(vendor);

    Product newProduct = new Product();
    newProduct.setName("product");
    newProduct.setVersion("2.0");
    newProduct.setVendor(vendor);

    Advisory advisory = new Advisory();
    advisory.setId(1);
    advisory.setCveId("CVE-1999-0181");
    advisory.setDescription("old");
    advisory.setPublishedDate(new Date(1L));
    advisory.setLastModifiedDate(new Date(1L));
    advisory.setSeverity("LOW");
    advisory.setProducts(new HashSet<>(Set.of(existingProduct)));

    Vulnerability vulnerability = new Vulnerability();
    Cve cve = new Cve();
    setField(cve, "id", advisory.getCveId());
    setField(cve, "published", new Date(2L));
    setField(cve, "lastModified", new Date(2L));
    setField(
        cve,
        "configurations",
        List.of(configuration("cpe:2.3:a:vendor:product:2.0:*:*:*:*:*:*:*")));
    setField(vulnerability, "cve", cve);

    when(advisoryService.getByCveId(advisory.getCveId())).thenReturn(advisory);
    when(vendorRepository.findOneByName("vendor")).thenReturn(vendor);
    when(productRepository.findByNameAndVersionAndVendor(eq("product"), eq("2.0"), eq(vendor)))
        .thenReturn(newProduct);

    ArgumentCaptor<Advisory> savedAdvisory = ArgumentCaptor.forClass(Advisory.class);

    nvdImportService.importVulnerability(vulnerability);

    verify(advisoryService).save(savedAdvisory.capture());
    verify(searchService, never()).index(any());
    Assertions.assertEquals(2, savedAdvisory.getValue().getProducts().size());
  }

  private static Configuration configuration(final String cpe23Uri) throws Exception {
    CpeMatch cpeMatch = new CpeMatch();
    setField(cpeMatch, "criteria", cpe23Uri);
    setField(cpeMatch, "vulnerable", true);

    Node node = new Node();
    setField(node, "cpeMatch", List.of(cpeMatch));

    Configuration configuration = new Configuration();
    setField(configuration, "nodes", List.of(node));
    return configuration;
  }

  private static void setField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
