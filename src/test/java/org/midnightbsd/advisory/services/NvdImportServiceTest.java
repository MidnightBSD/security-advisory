package org.midnightbsd.advisory.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.nvd2.Cve;
import org.midnightbsd.advisory.model.nvd2.Vulnerability;
import org.midnightbsd.advisory.repository.ConfigNodeCpeRepository;
import org.midnightbsd.advisory.repository.ConfigNodeRepository;
import org.midnightbsd.advisory.repository.CvssMetrics3Repository;
import org.midnightbsd.advisory.repository.ProductRepository;
import org.midnightbsd.advisory.repository.VendorRepository;
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
  void existingAdvisoryImportIndexesOnlyThroughAdvisoryServiceSave() throws Exception {
    Advisory advisory = new Advisory();
    advisory.setId(1);
    advisory.setCveId("CVE-1999-0181");
    advisory.setDescription("old");
    advisory.setPublishedDate(new Date(1L));
    advisory.setLastModifiedDate(new Date(1L));
    advisory.setSeverity("LOW");

    Vulnerability vulnerability = new Vulnerability();
    Cve cve = new Cve();
    setField(cve, "id", advisory.getCveId());
    setField(cve, "published", new Date(2L));
    setField(cve, "lastModified", new Date(2L));
    setField(vulnerability, "cve", cve);

    when(advisoryService.getByCveId(advisory.getCveId())).thenReturn(advisory);

    nvdImportService.importVulnerability(vulnerability);

    verify(advisoryService).save(advisory);
    verify(searchService, never()).index(any());
  }

  private static void setField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
