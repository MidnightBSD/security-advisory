package org.midnightbsd.advisory.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.repository.VendorRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class VendorServiceTest {

    @Mock
    VendorRepository repository;

    @InjectMocks
    VendorService service;

    @Test
    void testGet() {
        when(repository.findById(1)).thenReturn(Optional.of(new Vendor()));
        var result = service.get(1);
        assertNotNull(result);
    }

    @Test
    void testGetByName() {
        when(repository.findOneByName("Vendor")).thenReturn(new Vendor());
        var result = service.getByName("Vendor");
        assertNotNull(result);
    }
}
