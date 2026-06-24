package org.midnightbsd.advisory.ctl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.services.AdvisoryService;
import org.midnightbsd.advisory.services.SearchService;
import org.midnightbsd.advisory.services.VendorService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

  @Mock private VendorService vendorService;

  @Mock private AdvisoryService advisoryService;

  @Mock private SearchService searchService;

  @InjectMocks private HomeController homeController;

  @Test
  void homeShowsVendorGroupsWithoutLoadingAllVendors() {
    List<String> groups = List.of("A", "B", "0-9", "other");
    Advisory advisory = new Advisory();
    advisory.setCveId("CVE-2026-0001");
    when(advisoryService.latest(10)).thenReturn(List.of(advisory));
    when(vendorService.groups()).thenReturn(groups);
    ExtendedModelMap model = new ExtendedModelMap();

    String view = homeController.home(model);

    assertEquals("index", view);
    assertSame(groups, model.get("vendorGroups"));
    assertEquals(List.of(advisory), model.get("recentAdvisories"));
    verify(vendorService, never()).list();
  }

  @Test
  void vendorsShowsSelectedVendorGroup() {
    List<String> groups = List.of("A", "B", "0-9", "other");
    Vendor vendor = new Vendor();
    vendor.setName("apache");
    when(vendorService.groups()).thenReturn(groups);
    when(vendorService.getByGroup("A")).thenReturn(List.of(vendor));
    ExtendedModelMap model = new ExtendedModelMap();

    String view = homeController.vendors("A", model);

    assertEquals("vendors", view);
    assertSame(groups, model.get("vendorGroups"));
    assertEquals("A", model.get("selectedGroup"));
    assertEquals(List.of(vendor), model.get("vendors"));
  }

  @Test
  void cveShowsFullAdvisory() {
    Advisory advisory = new Advisory();
    advisory.setCveId("CVE-2026-0001");
    when(advisoryService.getByCveId("CVE-2026-0001")).thenReturn(advisory);
    ExtendedModelMap model = new ExtendedModelMap();

    String view = homeController.cve("CVE-2026-0001", model);

    assertEquals("cve", view);
    assertEquals("CVE-2026-0001", model.get("cveId"));
    assertSame(advisory, model.get("advisory"));
  }
}
