package org.midnightbsd.advisory.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.model.nvd2.Root;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CronDailyTest {

  @Mock private NvdFetchService nvdFetchService;

  @Mock private NvdImportService nvdImportService;

  @Mock private AdvisoryRepository advisoryRepository;

  @InjectMocks private CronDaily cronDaily;

  @Test
  void dailyUsesLatestAdvisoryModifiedDateForIncrementalFetch() throws Exception {
    Date checkpoint = new Date(System.currentTimeMillis() - 1000L);
    Root emptyIncrementalResult = new Root();
    emptyIncrementalResult.setTotalResults(0);

    when(advisoryRepository.findLatestLastModifiedDate()).thenReturn(checkpoint);
    when(nvdFetchService.getPage(eq(checkpoint), any(Date.class), eq(0L)))
        .thenReturn(emptyIncrementalResult);

    cronDaily.daily();

    verify(nvdFetchService).getPage(eq(checkpoint), any(Date.class), eq(0L));
    verify(nvdFetchService, never()).getPage(0L);
    verify(nvdImportService, never()).importVulnerability(any());
  }
}
