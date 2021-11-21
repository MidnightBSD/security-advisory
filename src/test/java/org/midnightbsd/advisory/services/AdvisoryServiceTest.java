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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/** @author Lucas Holt */
@RunWith(MockitoJUnitRunner.class)
public class AdvisoryServiceTest {

  @Mock private AdvisoryRepository advisoryRepository;

  @InjectMocks private AdvisoryService advisoryService;

  @Before
  public void setup() {
    Advisory adv = new Advisory();
    adv.setId(1);
    adv.setCveId("CVE-0000-0000");
    adv.setDescription("Foo");
    adv.setPublishedDate(Calendar.getInstance().getTime());

    when(advisoryRepository.findOneByCveId("CVE-0000-0000")).thenReturn(adv);
    when(advisoryRepository.findById(1)).thenReturn(Optional.of(adv));
    when(advisoryRepository.findAll()).thenReturn(Collections.singletonList(adv));
  }

  @Test
  public void testGetName() {
    Advisory adv = advisoryService.getByCveId("CVE-0000-0000");
    assertNotNull(adv);
    assertEquals(1, adv.getId());
    assertEquals("CVE-0000-0000", adv.getCveId());
    assertEquals("Foo", adv.getDescription());

    verify(advisoryRepository, times(1)).findOneByCveId(anyString());
  }

  @Test
  public void testGet() {
    Advisory adv = advisoryService.get(1);
    assertNotNull(adv);
    assertEquals(1, adv.getId());
    assertEquals("CVE-0000-0000", adv.getCveId());
    assertEquals("Foo", adv.getDescription());

    verify(advisoryRepository, times(1)).findById(1);
  }

  @Test
  public void testList() {
    List<Advisory> items = advisoryService.list();
    assertNotNull(items);
    assertTrue(items.size() > 0);
    verify(advisoryRepository, times(1)).findAll();
  }
}
