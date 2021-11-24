package org.midnightbsd.advisory.services;

import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.repository.VendorRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@CacheConfig(cacheNames = "vendor")
@Slf4j
@Service
public class VendorService {
    private final VendorRepository repository;

    public VendorService(final VendorRepository vendorRepository) {
        this.repository = vendorRepository;
    }

    public Page<Vendor> get(final Pageable page) {
        return repository.findAll(page);
    }

    public Vendor get(final int id) {
        final Optional<Vendor> vendor = repository.findById(id);
        return vendor.orElse(null);
    }

    @Cacheable(unless = "#result == null", key = "#name")
    public Vendor getByName(final String name) {
        return repository.findOneByName(name);
    }
}
