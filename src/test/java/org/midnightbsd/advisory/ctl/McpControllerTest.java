package org.midnightbsd.advisory.ctl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.ctl.api.McpController;
import org.midnightbsd.advisory.dto.AdvisoryDto;
import org.midnightbsd.advisory.dto.AdvisorySummaryDto;
import org.midnightbsd.advisory.dto.PackageQuery;
import org.midnightbsd.advisory.dto.PackageVulnerability;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.services.McpService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class McpControllerTest {

  private static final String TEST_CVE_ID = "CVE-0000-0000";

  private MockMvc mockMvc;

  @Mock private McpService mcpService;

  private ExecutorService executor;
  private McpController controller;

  private Advisory adv;

  @BeforeEach
  void setup() {
    executor = Executors.newSingleThreadExecutor();
    controller = new McpController(mcpService, executor);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    adv = new Advisory();
    adv.setDescription("test");
    adv.setCveId(TEST_CVE_ID);
    adv.setId(1);
    adv.setSeverity("HIGH");
    adv.setPublishedDate(Calendar.getInstance().getTime());
  }

  @AfterEach
  void teardown() {
    executor.shutdownNow();
  }

  @Test
  void toolsManifestIsListed() throws Exception {
    mockMvc
        .perform(get("/api/mcp/tools"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"))
        .andExpect(jsonPath("$[0].name").exists());
  }

  @Test
  void getCveReturnsAdvisory() throws Exception {
    when(mcpService.getCve(TEST_CVE_ID)).thenReturn(AdvisoryDto.from(adv));
    mockMvc
        .perform(get("/api/mcp/cve/" + TEST_CVE_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cveId").value(TEST_CVE_ID));
  }

  @Test
  void getCveReturnsNotFound() throws Exception {
    when(mcpService.getCve("CVE-9999-9999")).thenReturn(null);
    mockMvc.perform(get("/api/mcp/cve/CVE-9999-9999")).andExpect(status().isNotFound());
  }

  @Test
  void productAdvisoriesReturnsSummaries() throws Exception {
    when(mcpService.productSummaries("sendmail"))
        .thenReturn(List.of(AdvisorySummaryDto.from(adv)));
    mockMvc
        .perform(get("/api/mcp/product/sendmail"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].cveId").value(TEST_CVE_ID));
  }

  @Test
  void productVersionCheckReportsVulnerable() throws Exception {
    final PackageVulnerability vuln =
        PackageVulnerability.of(
            new PackageQuery("sendmail", "5.58", null), List.of(AdvisorySummaryDto.from(adv)));
    when(mcpService.checkPackage(any(PackageQuery.class))).thenReturn(vuln);
    mockMvc
        .perform(get("/api/mcp/product/sendmail/version/5.58"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.vulnerable").value(true))
        .andExpect(jsonPath("$.advisoryCount").value(1));
  }

  @Test
  void checkRejectsEmptyBody() throws Exception {
    mockMvc
        .perform(
            post("/api/mcp/check").contentType("application/json").content("[]"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void streamCheckCreatesSessionAndProcessesPackages() throws Exception {
    final PackageVulnerability vuln =
        PackageVulnerability.of(
            new PackageQuery("sendmail", "5.58", null), Collections.emptyList());
    when(mcpService.createScanSession(anyInt(), any())).thenReturn("sess-1");
    when(mcpService.checkPackage(any(PackageQuery.class))).thenReturn(vuln);

    mockMvc
        .perform(
            post("/api/mcp/check/stream")
                .contentType("application/json")
                .content("[{\"name\":\"sendmail\",\"version\":\"5.58\"}]"))
        .andExpect(request().asyncStarted());

    // session is created synchronously on the request thread before the emitter is returned
    verify(mcpService).createScanSession(eq(1), any());
    // the per-package work runs on the streaming executor
    verify(mcpService, timeout(2000)).checkPackage(any(PackageQuery.class));
    verify(mcpService, timeout(2000)).completeScanSession(eq("sess-1"), eq(1), anyInt());
  }
}
