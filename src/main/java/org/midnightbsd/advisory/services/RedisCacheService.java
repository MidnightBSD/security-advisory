package org.midnightbsd.advisory.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Lucas Holt
 */
@Log4j2
@Service
public class RedisCacheService implements CacheService<Object, Object> {
    private final RedisTemplate<Object, Object> client;

    @Autowired
    public RedisCacheService(RedisTemplate<Object, Object> client) {
        this.client = client;
    }

    public Boolean keyExists(@NotNull Object key) {
        return this.client.hasKey(key);
    }

    public Object get(@NotNull Object key) {
        return this.client.opsForValue().get(key);
    }

    public List<String> list() throws ServiceException {
        try {
            final Set<Object> redisKeys = this.client.keys("*");
            return redisKeys.stream().map(Object::toString).collect(Collectors.toList());
        } catch (Exception var2) {
            log.error(var2.getMessage(), var2);
            throw new ServiceException("Cache list could not be loaded");
        }
    }

    public void set(@NotNull Object key, Object value) {
        this.client.opsForValue().set(key, value);
    }

    public void set(@NotNull Object key, Object value, long timeout, TimeUnit unit) {
        this.client.opsForValue().set(key, value, timeout, unit);
    }

    public void delete(@NotNull Object key) throws ServiceException {
        try {
            this.client.delete(key);
        } catch (final Exception var3) {
            log.error(var3.getMessage(), var3);
            throw new ServiceException("Could not delete " + key.toString());
        }
    }

    public void deleteAllFromCurrentDb() throws ServiceException {
        if (this.client == null) {
            log.error("Client is null.");
            throw new ServiceException("Could not delete all key/value pairs from current redis database, null client.");
        } else {
            RedisConnection connection = getConnectionFactory().getConnection();
            try {
                connection.flushDb();
                log.trace("flushed cache for current db %n", ((JedisConnectionFactory) getConnectionFactory()).getDatabase());
            } catch (Exception var4) {
                log.error(var4.getMessage(), var4);
                throw new ServiceException("Unable to delete all key/value pairs from current redis database");
            }
        }
    }

    public void deleteAllFromInstance() throws ServiceException {
        try {
            getConnectionFactory().getConnection().flushAll();
            log.trace("flushed caches from instance %s", ((JedisConnectionFactory) getConnectionFactory()).getHostName());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("Unable to clear all databases in redis instance");
        }
    }

    private RedisConnectionFactory getConnectionFactory() throws ServiceException {
        final RedisConnectionFactory factory = this.client.getConnectionFactory();
        if (factory == null) {
            log.error("Factory is null.");
            throw new ServiceException("Unable to get connection factory");
        }
        return factory;
    }
}
