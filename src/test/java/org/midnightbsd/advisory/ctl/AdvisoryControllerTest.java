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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.midnightbsd.advisory.ctl.api.AdvisoryController;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.services.AdvisoryService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test architecture controller
 *
 * @author Lucas Holt
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvisoryControllerTest {

  private static final String TEST_CVE_ID = "CVE-0000-0000";
  private static final String TEST_PRODUCT_NAME = "httpd";
  private static final String TEST_VENDOR_NAME = "apache";

  private MockMvc mockMvc;

  @Mock private AdvisoryService advisoryService;

  @InjectMocks private AdvisoryController controller;

  private Advisory adv;

  @Before
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    adv = new Advisory();
    adv.setDescription("TEST ARCH");
    adv.setCveId(TEST_CVE_ID);
    adv.setId(1);
    adv.setPublishedDate(Calendar.getInstance().getTime());

    when(advisoryService.get(1)).thenReturn(adv);
    when(advisoryService.getByCveId(TEST_CVE_ID)).thenReturn(adv);

    when(advisoryService.getByProduct(TEST_PRODUCT_NAME))
        .thenReturn(Collections.singletonList(adv));
    when(advisoryService.getByVendor(TEST_VENDOR_NAME)).thenReturn(Collections.singletonList(adv));
    when(advisoryService.getByVendorAndProduct(TEST_VENDOR_NAME, TEST_PRODUCT_NAME))
        .thenReturn(Collections.singletonList(adv));
  }

  @Test
  public void testGet() {
    final ResponseEntity<Advisory> result = controller.get(1);
    assertNotNull(result);
    assertNotNull("Body should have a value", result.getBody());
    assertEquals("CVE-0000-0000", result.getBody().getCveId());
    assertEquals(1, result.getBody().getId());
  }

  @Test
  public void testGetByCve() {
    final ResponseEntity<Advisory> result = controller.get(TEST_CVE_ID);
    assertNotNull(result);
    assertNotNull("there should be a result body", result.getBody());
    assertEquals(TEST_CVE_ID, result.getBody().getCveId());
    assertEquals(1, result.getBody().getId());
  }

  @Test
  public void mvcTestGet() throws Exception {
    mockMvc
        .perform(get("/api/advisory/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
  }

  @Test
  public void mvcTestGetByProductName() throws Exception {
    mockMvc
        .perform(get("/api/advisory/product/" + TEST_PRODUCT_NAME))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
  }

  @Test
  public void mvcTestGetByVendorName() throws Exception {
    mockMvc
        .perform(get("/api/advisory/vendor/" + TEST_VENDOR_NAME))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
  }

  @Test
  public void mvcTestGetByVendorNameAndProductName() throws Exception {
    mockMvc
        .perform(get("/api/advisory/vendor/" + TEST_VENDOR_NAME + "/product/" + TEST_PRODUCT_NAME))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
  }
}
