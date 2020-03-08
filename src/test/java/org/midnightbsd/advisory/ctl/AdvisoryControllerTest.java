package org.midnightbsd.advisory.ctl;

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
        adv.setCveId("CVE-0000-0000");
        adv.setId(1);
        adv.setPublishedDate(Calendar.getInstance().getTime());

        when(advisoryService.list()).thenReturn(Collections.singletonList(adv));
        when(advisoryService.get(1)).thenReturn(adv);
    }

    @Test
    public void testList() {
        Pageable page = PageRequest.of(0,10);
        final ResponseEntity<Page<Advisory>> result = controller.list(page);
        assertNotNull(result);
        assertEquals(1, result.getBody().getTotalPages());
    }

    @Test
    public void testGet() {
        final ResponseEntity<Advisory> result = controller.get(1);
        assertNotNull(result);
        assertEquals("NAME", result.getBody().getCveId());
        assertEquals(1, result.getBody().getId());
    }

    @Test
    public void testGetByName() {
        final ResponseEntity<Advisory> result = controller.get("NAME");
        assertNotNull(result);
        assertEquals("NAME", result.getBody().getCveId());
        assertEquals(1, result.getBody().getId());
    }

    @Test
    public void mvcTestList() throws Exception {
        mockMvc.perform(get("/api/advisory"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
    }

    @Test
    public void mvcTestGet() throws Exception {
        mockMvc.perform(get("/api/advisory/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
    }

    @Test
    public void mvcTestGetByName() throws Exception {
        mockMvc.perform(get("/api/advisory/NAME"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json;charset=UTF-8"));
    }
}
