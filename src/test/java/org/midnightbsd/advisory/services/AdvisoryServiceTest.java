package org.midnightbsd.advisory.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.Architecture;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.midnightbsd.advisory.repository.ArchitectureRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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

        when(advisoryRepository.findOneByName("test")).thenReturn(adv);
        when(advisoryRepository.findOne(1)).thenReturn(adv);
        when(advisoryRepository.findAll()).thenReturn(Collections.singletonList(adv));
    }

    @Test
    public void testGetName() {
        Advisory arch = advisoryService.getByCveId("test");
        assertNotNull(arch);
        assertEquals(1, arch.getId());
        assertEquals("CVE-0000-0000", arch.getCveId());
        assertEquals("Foo", arch.getDescription());

        verify(advisoryRepository, times(1)).findOneByName(anyString());
    }

    @Test
    public void testGet() {
        Advisory adv = advisoryService.get(1);
        assertNotNull(adv);
        assertEquals(1, adv.getId());
        assertEquals("CVE-0000-0000", adv.getCveId());
        assertEquals("Foo", adv.getDescription());

        verify(advisoryRepository, times(1)).findOne(1);
    }

    @Test
    public void testList() {
        List<Advisory> items = advisoryService.list();
        assertNotNull(items);
        assertTrue(items.size() > 0);
        verify(advisoryRepository, times(1)).findAll();
    }
}
