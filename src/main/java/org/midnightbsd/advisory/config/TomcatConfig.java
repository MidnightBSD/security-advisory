package org.midnightbsd.advisory.config;

import org.apache.catalina.webresources.StandardRoot;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

  @Bean
  public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatResourceCacheCustomizer() {
    return factory ->
        factory.addContextCustomizers(
            context -> {
              StandardRoot resources = new StandardRoot(context);
              // Default is 10 MB; increase to 100 MB to handle large vendor URL sets
              resources.setCacheMaxSize(100 * 1024);
              context.setResources(resources);
            });
  }
}
