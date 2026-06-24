package org.midnightbsd.advisory.services;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.repository.VendorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Service
public class VendorService {
    private static final List<String> VENDOR_GROUPS = vendorGroups();

    private final VendorRepository repository;

    public VendorService(final VendorRepository vendorRepository) {
        this.repository = vendorRepository;
    }

    public List<Vendor> list() {
        return repository.findAll(Sort.by("name"));
    }

    public List<String> groups() {
        return VENDOR_GROUPS;
    }

    public List<Vendor> getByGroup(final String group) {
        if (!StringUtils.hasText(group)) return List.of();

        final String cleanedGroup = group.trim();
        if ("0-9".equals(cleanedGroup)) {
            return repository.findByNameStartingWithDigit();
        }
        if ("other".equalsIgnoreCase(cleanedGroup)) {
            return repository.findByNameStartingWithSymbol();
        }
        if (cleanedGroup.length() == 1 && Character.isLetter(cleanedGroup.charAt(0))) {
            return repository.findByNamePrefix(cleanedGroup);
        }
        return List.of();
    }

    public Page<Vendor> get(final Pageable page) {
        return repository.findAll(page);
    }

    public Vendor get(final int id) {
        final Optional<Vendor> vendor = repository.findById(id);
        return vendor.orElse(null);
    }

    public Vendor getByName(final String name) {
        if (!StringUtils.hasText(name)) return null;

        var nameTrimmed = name.trim();
        return repository.findOneByName(nameTrimmed);
    }

    private static List<String> vendorGroups() {
        final List<String> groups = new ArrayList<>();
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            groups.add(Character.toString(letter));
        }
        groups.add("0-9");
        groups.add("other");
        return List.copyOf(groups);
    }
}
