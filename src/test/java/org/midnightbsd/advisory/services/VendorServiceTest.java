package org.midnightbsd.advisory.services;

import java.util.List;
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

    @Test
    void testGetByNameBlankReturnsNull() {
        var result = service.getByName("   ");
        assertNull(result);
        verifyNoInteractions(repository);
    }

    @Test
    void groupsReturnsLettersDigitsAndOther() {
        var result = service.groups();
        assertEquals("A", result.getFirst());
        assertTrue(result.contains("Z"));
        assertTrue(result.contains("0-9"));
        assertTrue(result.contains("other"));
    }

    @Test
    void getByGroupReturnsLetterPrefix() {
        Vendor vendor = new Vendor();
        vendor.setName("apache");
        when(repository.findByNamePrefix("A")).thenReturn(List.of(vendor));

        var result = service.getByGroup("A");

        assertEquals(List.of(vendor), result);
        verify(repository).findByNamePrefix("A");
    }

    @Test
    void getByGroupReturnsDigitGroup() {
        Vendor vendor = new Vendor();
        vendor.setName("3com");
        when(repository.findByNameStartingWithDigit()).thenReturn(List.of(vendor));

        var result = service.getByGroup("0-9");

        assertEquals(List.of(vendor), result);
        verify(repository).findByNameStartingWithDigit();
    }

    @Test
    void getByGroupReturnsSymbolGroup() {
        Vendor vendor = new Vendor();
        vendor.setName("_vendor");
        when(repository.findByNameStartingWithSymbol()).thenReturn(List.of(vendor));

        var result = service.getByGroup("other");

        assertEquals(List.of(vendor), result);
        verify(repository).findByNameStartingWithSymbol();
    }

    @Test
    void getByGroupReturnsEmptyForInvalidGroup() {
        var result = service.getByGroup("abc");

        assertTrue(result.isEmpty());
        verifyNoInteractions(repository);
    }
}
