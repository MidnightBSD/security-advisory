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
package org.midnightbsd.advisory.config;


import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/** @author Lucas Holt */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

  public WebMvcConfig() {
    super();
  }

  /**
   * Override default handlers. see
   * http://stackoverflow.com/questions/24837715/spring-boot-with-angularjs-html5mode
   *
   * @param registry
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    super.addResourceHandlers(registry);
    Integer cachePeriod = 60;

    registry
        .addResourceHandler("/robots.txt")
        .addResourceLocations("classpath:/static/robots.txt")
        .setCachePeriod(cachePeriod);

    registry
        .addResourceHandler("/static/**")
        .addResourceLocations("classpath:/static/")
        .setCachePeriod(cachePeriod);

    registry
        .addResourceHandler("/static/styles/**", "/styles/*.css", "/styles/*.map")
        .addResourceLocations("classpath:/static/styles/")
        .setCachePeriod(cachePeriod);

    // enable webjars
    registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
  }

  /** {@inheritDoc} */
  @Override
  public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
    super.configureMessageConverters(converters);
    converters.add(new StringHttpMessageConverter());
    converters.add(new ByteArrayHttpMessageConverter());
  }
}
