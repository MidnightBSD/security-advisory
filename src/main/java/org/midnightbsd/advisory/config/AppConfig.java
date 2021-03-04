package org.midnightbsd.advisory.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Lucas Holt
 */
@EnableFeignClients
@EnableScheduling
@EnableAsync
@EnableTransactionManagement
@EnableJpaRepositories
@Configuration
public class AppConfig {
}
