package org.midnightbsd.advisory.ctl.api;

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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Mock
    private AdvisoryService advisoryService;

    @InjectMocks
    private AdvisoryController controller;

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

        when(advisoryService.getByProduct(TEST_PRODUCT_NAME)).thenReturn(Collections.singletonList(adv));
        when(advisoryService.getByVendor(TEST_VENDOR_NAME)).thenReturn(Collections.singletonList(adv));
        when(advisoryService.getByVendorAndProduct(TEST_VENDOR_NAME, TEST_PRODUCT_NAME)).thenReturn(Collections.singletonList(adv));
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
        mockMvc.perform(get("/api/advisory/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
    }

    @Test
    public void mvcTestGetByProductName() throws Exception {
        mockMvc.perform(get("/api/advisory/product/" + TEST_PRODUCT_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
    }

    @Test
     public void mvcTestGetByVendorName() throws Exception {
         mockMvc.perform(get("/api/advisory/vendor/" + TEST_VENDOR_NAME))
                 .andExpect(status().isOk())
                 .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
     }

    @Test
       public void mvcTestGetByVendorNameAndProductName() throws Exception {
           mockMvc.perform(get("/api/advisory/vendor/" + TEST_VENDOR_NAME + "/product/" + TEST_PRODUCT_NAME))
                   .andExpect(status().isOk())
                   .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
       }
}
