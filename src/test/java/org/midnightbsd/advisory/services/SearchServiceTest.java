package org.midnightbsd.advisory.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
}
