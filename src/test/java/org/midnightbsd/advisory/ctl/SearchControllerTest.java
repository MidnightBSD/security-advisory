package org.midnightbsd.advisory.ctl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.ctl.api.SearchController;
import org.midnightbsd.advisory.model.search.NvdItem;
import org.midnightbsd.advisory.services.SearchService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

  private MockMvc mockMvc;

  @Mock private SearchService searchService;

  @InjectMocks private SearchController controller;

  private NvdItem nvdItem;

  @BeforeEach
  void setup() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();

    nvdItem = new NvdItem();
    nvdItem.setId(1);
    nvdItem.setCveId("CVE-0000-0001");
    nvdItem.setDescription("Test vulnerability");
  }

  @Test
  void mvcTestFindReturnsResults() throws Exception {
    when(searchService.find(eq("apache"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(nvdItem)));
    mockMvc
        .perform(get("/api/search?term=apache"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"));
  }

  @Test
  void mvcTestFindReturnsEmptyPage() throws Exception {
    when(searchService.find(eq("notfound"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));
    mockMvc
        .perform(get("/api/search?term=notfound"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"));
  }

  @Test
  void mvcTestFindWithPagination() throws Exception {
    when(searchService.find(eq("curl"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(nvdItem)));
    mockMvc
        .perform(get("/api/search?term=curl&page=0&size=10"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"));
  }
}
