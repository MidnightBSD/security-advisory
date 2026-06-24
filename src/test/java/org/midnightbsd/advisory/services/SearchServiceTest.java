package org.midnightbsd.advisory.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.CvssMetrics3;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.midnightbsd.advisory.repository.search.NvdSearchRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

  @Mock private NvdSearchRepository nvdSearchRepository;

  @Mock private AdvisoryRepository advisoryRepository;

  @InjectMocks private SearchService searchService;

  @Test
  void findReturnsEmptyForBlankInput() {
    var result = searchService.find("   ", PageRequest.of(0, 10));

    assertTrue(result.isEmpty());
    verifyNoInteractions(nvdSearchRepository);
  }

  @Test
  void convertCopiesPrivilegesRequiredFromCvssMetric() {
    CvssMetrics3 metric = new CvssMetrics3();
    metric.setPrivilegesRequired("LOW");

    Advisory advisory = new Advisory();
    advisory.setId(1);
    advisory.setCveId("CVE-0000-0001");
    advisory.setCvssMetrics3(Set.of(metric));

    var result = searchService.convert(advisory);

    assertEquals("LOW", result.getCvssMetrics3().getFirst().getPrivilegesRequired());
  }
}
