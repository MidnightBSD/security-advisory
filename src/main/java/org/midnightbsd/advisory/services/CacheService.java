package org.midnightbsd.advisory.services;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Lucas Holt
 */
public interface CacheService<K, V> {
    Boolean keyExists(@NotNull K var1);

    V get(@NotNull K var1);

    List<String> list() throws ServiceException;

    void set(@NotNull K var1, V var2);

    void set(@NotNull K var1, V var2, long var3, TimeUnit var5);

    void delete(@NotNull K var1) throws ServiceException;

    void deleteAllFromCurrentDb() throws ServiceException;

    void deleteAllFromInstance() throws ServiceException;
}
