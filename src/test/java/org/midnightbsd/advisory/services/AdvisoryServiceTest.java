package org.midnightbsd.advisory.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Lucas Holt
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvisoryServiceTest {

    @Mock
    private AdvisoryRepository advisoryRepository;

    @InjectMocks
    private AdvisoryService advisoryService;

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
