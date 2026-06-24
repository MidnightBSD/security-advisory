package org.midnightbsd.advisory.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.dto.AdvisoryDto;
import org.midnightbsd.advisory.dto.AdvisorySummaryDto;
import org.midnightbsd.advisory.dto.PackageQuery;
import org.midnightbsd.advisory.dto.PackageVulnerability;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.search.NvdItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class McpToolsTest {

  private static final String TEST_CVE_ID = "CVE-0000-0000";

  @Mock private McpService mcpService;

  private McpTools tools;
  private Advisory adv;

  @BeforeEach
  void setup() {
    tools = new McpTools(mcpService);

    adv = new Advisory();
    adv.setId(1);
    adv.setCveId(TEST_CVE_ID);
    adv.setDescription("test");
    adv.setSeverity("HIGH");
    adv.setPublishedDate(Calendar.getInstance().getTime());
  }

  @Test
  void getCveDelegatesToService() {
    when(mcpService.getCve(TEST_CVE_ID)).thenReturn(AdvisoryDto.from(adv));
    final AdvisoryDto dto = tools.getCve(TEST_CVE_ID);
    assertEquals(TEST_CVE_ID, dto.cveId());
  }

  @Test
  void getCveReturnsNullWhenUntracked() {
    when(mcpService.getCve("CVE-9999-9999")).thenReturn(null);
    assertNull(tools.getCve("CVE-9999-9999"));
  }

  @Test
  void getCveReturnsNullForBlankInput() {
    assertNull(tools.getCve("   "));
    verifyNoInteractions(mcpService);
  }

  @Test
  void searchAppliesDefaultPagingWhenUnspecified() {
    final Page<NvdItem> page = new PageImpl<>(List.of(new NvdItem()));
    final ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
    when(mcpService.search(eq("openssl"), pageable.capture())).thenReturn(page);

    final List<NvdItem> results = tools.searchAdvisories("openssl", null, null);

    assertEquals(1, results.size());
    assertEquals(0, pageable.getValue().getPageNumber());
    assertEquals(25, pageable.getValue().getPageSize());
  }

  @Test
  void searchClampsOversizedPageSize() {
    when(mcpService.search(eq("openssl"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));
    tools.searchAdvisories("openssl", 2, 9999);
    verify(mcpService).search("openssl", PageRequest.of(2, 200));
  }

  @Test
  void searchReturnsEmptyForBlankInput() {
    assertTrue(tools.searchAdvisories("   ", null, null).isEmpty());
    verifyNoInteractions(mcpService);
  }

  @Test
  void matchCpeWrapsParsingErrorAsIllegalArgument() throws Exception {
    when(mcpService.cpeMatch(eq("bogus"), eq(false), any()))
        .thenThrow(new us.springett.parsers.cpe.exceptions.CpeParsingException("bad"));
    assertThrows(IllegalArgumentException.class, () -> tools.matchCpe("bogus", false));
  }

  @Test
  void matchCpeRejectsBlankInput() {
    assertThrows(IllegalArgumentException.class, () -> tools.matchCpe("   ", false));
    verifyNoInteractions(mcpService);
  }

  @Test
  void checkPackageBuildsQueryFromArguments() {
    final PackageVulnerability vuln =
        PackageVulnerability.of(
            new PackageQuery("sendmail", "5.58", null), List.of(AdvisorySummaryDto.from(adv)));
    final ArgumentCaptor<PackageQuery> query = ArgumentCaptor.forClass(PackageQuery.class);
    when(mcpService.checkPackage(query.capture())).thenReturn(vuln);

    final PackageVulnerability result = tools.checkPackage("sendmail", "5.58", null);

    assertTrue(result.vulnerable());
    assertEquals(1, result.advisoryCount());
    assertEquals("sendmail", query.getValue().name());
    assertEquals("5.58", query.getValue().version());
  }

  @Test
  void checkPackageTrimsArguments() {
    when(mcpService.checkPackage(any(PackageQuery.class)))
        .thenAnswer(i -> PackageVulnerability.of(i.getArgument(0), List.of()));

    tools.checkPackage("  sendmail  ", " 5.58 ", "  nist  ");

    final ArgumentCaptor<PackageQuery> query = ArgumentCaptor.forClass(PackageQuery.class);
    verify(mcpService).checkPackage(query.capture());
    assertEquals("sendmail", query.getValue().name());
    assertEquals("5.58", query.getValue().version());
    assertEquals("nist", query.getValue().vendor());
  }

  @Test
  void checkPackageRejectsBlankName() {
    assertThrows(IllegalArgumentException.class, () -> tools.checkPackage("   ", "1", "v"));
    verifyNoInteractions(mcpService);
  }

  @Test
  void checkPackagesReturnsEmptyForEmptyInput() {
    assertTrue(tools.checkPackages(List.of()).isEmpty());
  }

  @Test
  void checkPackagesChecksEachEntry() {
    final PackageQuery q1 = new PackageQuery("a", "1", null);
    final PackageQuery q2 = new PackageQuery("b", "2", null);
    when(mcpService.checkPackage(any(PackageQuery.class)))
        .thenAnswer(i -> PackageVulnerability.of(i.getArgument(0), List.of()));

    final List<PackageVulnerability> results = tools.checkPackages(List.of(q1, q2));

    assertEquals(2, results.size());
    verify(mcpService).checkPackage(q1);
    verify(mcpService).checkPackage(q2);
  }

  @Test
  void checkPackagesRejectsBlankNames() {
    assertThrows(
        IllegalArgumentException.class,
        () -> tools.checkPackages(List.of(new PackageQuery("   ", "1", "v"))));
    verifyNoInteractions(mcpService);
  }
}
