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
package org.midnightbsd.advisory.services;


import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/** @author Lucas Holt */
@Slf4j
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
      throw new ServiceException(
          "Could not delete all key/value pairs from current redis database, null client.");
    } else {
      RedisConnection connection = getConnectionFactory().getConnection();
      try {
        connection.flushDb();
        log.trace(
            "flushed cache for current db %n",
            ((JedisConnectionFactory) getConnectionFactory()).getDatabase());
      } catch (Exception var4) {
        log.error(var4.getMessage(), var4);
        throw new ServiceException(
            "Unable to delete all key/value pairs from current redis database");
      }
    }
  }

  public void deleteAllFromInstance() throws ServiceException {
    try {
      getConnectionFactory().getConnection().flushAll();
      log.trace(
          "flushed caches from instance %s",
          ((JedisConnectionFactory) getConnectionFactory()).getHostName());
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
