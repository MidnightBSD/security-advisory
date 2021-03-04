package integration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.midnightbsd.advisory.Application;
import org.midnightbsd.advisory.config.AppConfig;
import org.midnightbsd.advisory.config.CacheConfig;
import org.midnightbsd.advisory.config.DataSourceConfig;
import org.midnightbsd.advisory.config.SearchConfig;
import org.midnightbsd.advisory.config.WebMvcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * @author Lucas Holt
 */
@SpringBootTest(classes = {Application.class, AppConfig.class, WebMvcConfig.class, SearchConfig.class, DataSourceConfig.class,
        TestRedisConfig.class, CacheConfig.class} )
//@ActiveProfiles({ "default", "it"})
@RunWith(SpringJUnit4ClassRunner.class)
/*@ContextConfiguration(classes = { AppConfig.class, WebMvcConfig.class, SearchConfig.class, DataSourceConfig.class,
        TestRedisConfig.class, CacheConfig.class}) */
@WebAppConfiguration
public class ITEndpoint {
    @Autowired
    private WebApplicationContext wac;

    @Test
    public void givenWac_whenServletContext_thenItProvidesHomeController() {
        ServletContext servletContext = wac.getServletContext();

        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(wac.getBean("homeController"));
    }
}
