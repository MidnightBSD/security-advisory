package org.midnightbsd.advisory.ctl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.midnightbsd.advisory.ctl.api.AdvisoryController;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Lucas Holt
 */
@RunWith(MockitoJUnitRunner.class)
public class HomeControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private HomeController controller;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void mvcTestGet() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
