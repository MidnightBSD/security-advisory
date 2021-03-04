package integration;

import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

@TestConfiguration
public class TestRedisConfig {


@Bean
       public RestHighLevelClient client() {
           ClientConfiguration clientConfiguration
               = ClientConfiguration.builder()
                   .connectedTo("localhost:9200")
                   .build();

           return RestClients.create(clientConfiguration).rest();
       }
               /*
    @Bean
    public DataSource dataSource() {
    return DataSourceBuilder.create().build();
    }            */

       @Bean
       public ElasticsearchOperations elasticsearchTemplate() {
           return new ElasticsearchRestTemplate(client());
       }

    @Bean
       @SuppressWarnings("unchecked")
       public RedisSerializer<Object> defaultRedisSerializer()
       {
           return Mockito.mock(RedisSerializer.class);
       }


       @Bean
       public RedisConnectionFactory connectionFactory()
       {
           RedisConnectionFactory factory = Mockito.mock(RedisConnectionFactory.class);
           RedisConnection connection = Mockito.mock(RedisConnection.class);
           Mockito.when(factory.getConnection()).thenReturn(connection);

           return factory;
       }
}
