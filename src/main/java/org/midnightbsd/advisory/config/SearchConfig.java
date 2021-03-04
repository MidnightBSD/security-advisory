package org.midnightbsd.advisory.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.Arrays;

/**
 * @author Lucas Holt
 */
@Log4j2
@Configuration
@EnableElasticsearchRepositories(basePackages = {"org.midnightbsd.advisory.repository.search"})
public class SearchConfig {

    @Autowired
    private Environment environment;

    @Value("search.nvd-item-index")
    private String nvdItemIndex;

    @Bean
    public String nvdItemIndex() {
        return String.format("%s-%s",nvdItemIndex,getEnv());
    }

    public String getEnv() {
        if(environment != null) {
            return Arrays.toString(environment.getActiveProfiles());
        }
        return "";
    }
}
