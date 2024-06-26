/*
 * Copyright (c) 2017-2021 Lucas Holt
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.midnightbsd.advisory.ctl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.ctl.api.AdvisoryController;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.services.AdvisoryService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test advisory controller
 *
 * @author Lucas Holt
 */
@ExtendWith(MockitoExtension.class)
class AdvisoryControllerTest {

  private static final String TEST_CVE_ID = "CVE-0000-0000";
  private static final String TEST_PRODUCT_NAME = "httpd";
  private static final String TEST_VENDOR_NAME = "apache";

  private MockMvc mockMvc;

  @Mock private AdvisoryService advisoryService;

  @InjectMocks private AdvisoryController controller;

  private Advisory adv;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();

    adv = new Advisory();
    adv.setDescription("TEST ARCH");
    adv.setCveId(TEST_CVE_ID);
    adv.setId(1);
    adv.setPublishedDate(Calendar.getInstance().getTime());

  }

  @Test
  void testGet() {
    when(advisoryService.get(1)).thenReturn(adv);
    final ResponseEntity<Advisory> result = controller.get(1);
    assertNotNull(result);
    assertNotNull(result.getBody());
    assertEquals("CVE-0000-0000", result.getBody().getCveId());
    assertEquals(1, result.getBody().getId());
  }

  @Test
  void testGetByCve() {
    when(advisoryService.getByCveId(TEST_CVE_ID)).thenReturn(adv);
    final ResponseEntity<Advisory> result = controller.get(TEST_CVE_ID);
    assertNotNull(result);
    assertNotNull(result.getBody());
    assertEquals(TEST_CVE_ID, result.getBody().getCveId());
    assertEquals(1, result.getBody().getId());
  }


  @Test
  void mvcTestList() throws Exception {
    Page<Advisory> page = new PageImpl<>(Collections.singletonList(adv));
    when(advisoryService.get(any())).thenReturn(page);
    mockMvc
            .perform(get("/api/advisory?pageSize=5&pageNo=1"))
            .andExpect(status().isOk());
  }

  @Test
  void mvcTestGet() throws Exception {
    when(advisoryService.get(1)).thenReturn(adv);
    mockMvc
        .perform(get("/api/advisory/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
  }

  @Test
  void mvcTestGetByProductName() throws Exception {
    when(advisoryService.getByProduct(TEST_PRODUCT_NAME))
            .thenReturn(Collections.singletonList(adv));
    mockMvc
        .perform(get("/api/advisory/product/" + TEST_PRODUCT_NAME))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
  }

  @Test
  void mvcTestGetByVendorName() throws Exception {
    when(advisoryService.getByVendor(TEST_VENDOR_NAME)).thenReturn(Collections.singletonList(adv));
    mockMvc
        .perform(get("/api/advisory/vendor/" + TEST_VENDOR_NAME))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
  }

  @Test
  void mvcTestGetByVendorNameAndProductName() throws Exception {
    when(advisoryService.getByVendorAndProduct(TEST_VENDOR_NAME, TEST_PRODUCT_NAME, null))
            .thenReturn(Collections.singletonList(adv));
    mockMvc
        .perform(get("/api/advisory/vendor/" + TEST_VENDOR_NAME + "/product/" + TEST_PRODUCT_NAME))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
  }

  @Test
  void mvcTestGetByVendorNameAndProductNameWithDate() throws Exception {
    when(advisoryService.getByVendorAndProduct(anyString(), anyString(), any(Date.class)))
            .thenReturn(Collections.singletonList(adv));
    mockMvc
            .perform(get("/api/advisory/vendor/" + TEST_VENDOR_NAME + "/product/" + TEST_PRODUCT_NAME + "?startDate=2006-02-28"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
  }
}
