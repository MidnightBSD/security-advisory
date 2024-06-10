package org.midnightbsd.advisory.ctl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.ctl.api.AdvisoryController;
import org.midnightbsd.advisory.ctl.api.CpeController;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.services.AdvisoryService;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CpeControllerTest {

    private static final String TEST_CVE_ID = "CVE-0000-0000";


    private MockMvc mockMvc;

    @Mock
    private AdvisoryService advisoryService;

    @InjectMocks
    private CpeController controller;

    private Advisory adv;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        adv = new Advisory();
        adv.setDescription("TEST ARCH");
        adv.setCveId(TEST_CVE_ID);
        adv.setId(1);
        adv.setPublishedDate(Calendar.getInstance().getTime());
    }

    // MidnightBSD cpe identifiers used to not include the other field at the end of the identifier.
    @Test
    void mvcTestGetCpe2() throws Exception {
        when(advisoryService.getByVendorAndProduct(anyString(), anyString(), ArgumentMatchers.isNull()))
                .thenReturn(Collections.singletonList(adv));
        mockMvc
                .perform(get("/api/cpe/partial-match?cpe=cpe:2.3:a:apache:mod_dav_svn:1.14.3:*:*:*:*:midnightbsd3:x64"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
    }

    @Test
    void mvcTestGetCpe() throws Exception {
    when(advisoryService.getByVendorAndProduct(anyString(), anyString(), ArgumentMatchers.isNull()))
        .thenReturn(Collections.singletonList(adv));
        mockMvc
                .perform(get("/api/cpe/partial-match?cpe=cpe:2.3:a:eric_allman:sendmail:5.58:*:*:*:*:*:*:*"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
    }

    @Test
    void mvcTestGetByCpeWithDate() throws Exception {
        when(advisoryService.getByVendorAndProduct(anyString(), anyString(), any(Date.class)))
                .thenReturn(Collections.singletonList(adv));
        mockMvc
                .perform(get("/api/cpe/partial-match?cpe=cpe:2.3:a:eric_allman:sendmail:5.58:*:*:*:*:*:*:*&startDate=2006-02-28"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
    }
}
