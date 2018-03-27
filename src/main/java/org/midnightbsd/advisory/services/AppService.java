package org.midnightbsd.advisory.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Lucas Holt
 */
public interface AppService<T> {
    List<T> list();

    Page<T> get(Pageable page);

    T get(int id);
}
