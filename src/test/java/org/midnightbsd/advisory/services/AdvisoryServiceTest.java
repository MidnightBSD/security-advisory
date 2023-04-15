/*
 * Copyright (c) 2017-2023 Lucas Holt
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

import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.Product;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.midnightbsd.advisory.repository.ProductRepository;
import org.midnightbsd.advisory.repository.VendorRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** @author Lucas Holt */
@ExtendWith(MockitoExtension.class)
class AdvisoryServiceTest {

  @Mock private AdvisoryRepository advisoryRepository;

  @Mock private VendorRepository vendorRepository;

  @Mock private ProductRepository productRepository;

  @InjectMocks private AdvisoryService advisoryService;

  Advisory adv;

  @BeforeEach
  public void setup() {
    adv = new Advisory();
    adv.setId(1);
    adv.setCveId("CVE-0000-0000");
    adv.setDescription("Foo");
    adv.setPublishedDate(Calendar.getInstance().getTime());
  }

  @Test
  void testGetName() {
    when(advisoryRepository.findOneByCveId("CVE-0000-0000")).thenReturn(adv);
    Advisory adv = advisoryService.getByCveId("CVE-0000-0000");
    assertNotNull(adv);
    assertEquals(1, adv.getId());
    assertEquals("CVE-0000-0000", adv.getCveId());
    assertEquals("Foo", adv.getDescription());

    verify(advisoryRepository, times(1)).findOneByCveId(anyString());
  }

  @Test
  void testGet() {
    when(advisoryRepository.findById(1)).thenReturn(Optional.of(adv));
    Advisory adv = advisoryService.get(1);
    assertNotNull(adv);
    assertEquals(1, adv.getId());
    assertEquals("CVE-0000-0000", adv.getCveId());
    assertEquals("Foo", adv.getDescription());

    verify(advisoryRepository, times(1)).findById(1);
  }

  @Test
  void testList() {
    when(advisoryRepository.findAll()).thenReturn(Collections.singletonList(adv));
    List<Advisory> items = advisoryService.list();
    assertNotNull(items);
    assertTrue(items.size() > 0);
    verify(advisoryRepository, times(1)).findAll();
  }

  @Test
  void testGetByVendorAndProductAndVersion() {
    when(advisoryRepository.findByProductNameAndVendor(anyString(), anyString(), anyString())).thenReturn(Collections.singletonList(adv));
    List<Advisory> items = advisoryService.getByVendorAndProductAndVersion("vendor", "product", "version", null);
    assertNotNull(items);
    assertTrue(items.size() > 0);
    verify(advisoryRepository, times(1)).findByProductNameAndVendor(anyString(), anyString(), anyString());
  }

  @Test
  void testGetByVendorAndProductAndVersionWithDate() {
    var vendor = new Vendor();
    vendor.setName("vendor");
    when(vendorRepository.findOneByName(anyString())).thenReturn(vendor);
    when(productRepository.findByNameAndVendor(anyString(), any(Vendor.class))).thenReturn(Collections.singletonList(new Product()));
    when(advisoryRepository.findByVersionPublishedDateIsAfterProductsIn(anyString(), any(), anyList())).thenReturn(Collections.singletonList(adv));
    List<Advisory> items =
        advisoryService.getByVendorAndProductAndVersion(
            "vendor", "product", "version", Calendar.getInstance().getTime());

    verify(vendorRepository, times(1)).findOneByName(anyString());
    verify(productRepository, times(1)).findByNameAndVendor(anyString(), any(Vendor.class));
    verify(advisoryRepository, times(1)).findByVersionPublishedDateIsAfterProductsIn(anyString(), any(), anyList());

    assertNotNull(items);
    assertTrue(items.size() > 0);
  }

  @Test
  void testGetByVendorAndProductWithDate() {
    var vendor = new Vendor();
    vendor.setName("vendor");
    when(vendorRepository.findOneByName(anyString())).thenReturn(vendor);
    when(productRepository.findByNameAndVendor(anyString(), any(Vendor.class))).thenReturn(Collections.singletonList(new Product()));
    when(advisoryRepository.findByPublishedDateIsAfterProductsIn(any(), anyList())).thenReturn(Collections.singletonList(adv));
    List<Advisory> items =
            advisoryService.getByVendorAndProduct(
                    "vendor", "product", Calendar.getInstance().getTime());

    verify(vendorRepository, times(1)).findOneByName(anyString());
    verify(productRepository, times(1)).findByNameAndVendor(anyString(), any(Vendor.class));
    verify(advisoryRepository, times(1)).findByPublishedDateIsAfterProductsIn(any(), anyList());

    assertNotNull(items);
    assertTrue(items.size() > 0);
  }

  @Test
  void testGetByVendorAndProduct() {
    var vendor = new Vendor();
    vendor.setName("vendor");
    when(vendorRepository.findOneByName(anyString())).thenReturn(vendor);
    when(productRepository.findByNameAndVendor(anyString(), any(Vendor.class))).thenReturn(Collections.singletonList(new Product()));
    when(advisoryRepository.findByProductsIn(anyList())).thenReturn(Collections.singletonList(adv));
    List<Advisory> items =
            advisoryService.getByVendorAndProduct(
                    "vendor", "product", null);

    verify(vendorRepository, times(1)).findOneByName(anyString());
    verify(productRepository, times(1)).findByNameAndVendor(anyString(), any(Vendor.class));
    verify(advisoryRepository, times(1)).findByProductsIn(anyList());

    assertNotNull(items);
    assertTrue(items.size() > 0);
  }

  @Test
  void testGetByProduct() {
    var vendor = new Vendor();
    vendor.setName("vendor");
    when(advisoryRepository.findByProductName(anyString())).thenReturn(Collections.singletonList(adv));
    List<Advisory> items =
            advisoryService.getByProduct("product");

    verify(advisoryRepository, times(1)).findByProductName(anyString());

    assertNotNull(items);
    assertTrue(items.size() > 0);
  }
}