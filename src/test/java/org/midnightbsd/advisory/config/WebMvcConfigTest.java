package org.midnightbsd.advisory.config;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

class WebMvcConfigTest {

  @Test
  void servesRobotsTxtFromStaticResources() throws Exception {
    try (AnnotationConfigWebApplicationContext context =
        new AnnotationConfigWebApplicationContext()) {
      context.setServletContext(new MockServletContext());
      context.register(TestWebConfig.class);
      context.refresh();

      MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

      mockMvc
          .perform(get("/robots.txt"))
          .andExpect(status().isOk())
          .andExpect(content().string(containsString("Crawl-delay: 2")));
    }
  }

  @Configuration
  @EnableWebMvc
  @Import(WebMvcConfig.class)
  static class TestWebConfig {}
}
