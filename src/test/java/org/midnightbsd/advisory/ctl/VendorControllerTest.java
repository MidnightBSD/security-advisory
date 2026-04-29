package org.midnightbsd.advisory.ctl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.ctl.api.VendorController;
import org.midnightbsd.advisory.dto.VendorDto;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.services.VendorService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class VendorControllerTest {

  private static final String TEST_VENDOR_NAME = "apache";

  private MockMvc mockMvc;

  @Mock private VendorService vendorService;

  @InjectMocks private VendorController controller;

  private Vendor vendor;

  @BeforeEach
  void setup() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();

    vendor = new Vendor();
    vendor.setId(1);
    vendor.setName(TEST_VENDOR_NAME);
  }

  @Test
  void testGetById() {
    when(vendorService.get(1)).thenReturn(vendor);
    ResponseEntity<VendorDto> result = controller.get(1);
    assertNotNull(result);
    assertNotNull(result.getBody());
    assertEquals(1, result.getBody().id());
    assertEquals(TEST_VENDOR_NAME, result.getBody().name());
  }

  @Test
  void testGetByIdNotFound() {
    when(vendorService.get(99)).thenReturn(null);
    ResponseEntity<VendorDto> result = controller.get(99);
    assertEquals(404, result.getStatusCode().value());
  }

  @Test
  void testGetByName() {
    when(vendorService.getByName(TEST_VENDOR_NAME)).thenReturn(vendor);
    ResponseEntity<VendorDto> result = controller.get(TEST_VENDOR_NAME);
    assertNotNull(result);
    assertNotNull(result.getBody());
    assertEquals(TEST_VENDOR_NAME, result.getBody().name());
  }

  @Test
  void testGetByNameNotFound() {
    when(vendorService.getByName("unknown")).thenReturn(null);
    ResponseEntity<VendorDto> result = controller.get("unknown");
    assertEquals(404, result.getStatusCode().value());
  }

  @Test
  void mvcTestList() throws Exception {
    when(vendorService.get(any(org.springframework.data.domain.Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(vendor), PageRequest.of(0, 10), 1));
    mockMvc
        .perform(get("/api/vendor"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"));
  }

  @Test
  void mvcTestGetById() throws Exception {
    when(vendorService.get(1)).thenReturn(vendor);
    mockMvc
        .perform(get("/api/vendor/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"))
        .andExpect(jsonPath("$.name").value(TEST_VENDOR_NAME));
  }

  @Test
  void mvcTestGetByIdNotFound() throws Exception {
    when(vendorService.get(99)).thenReturn(null);
    mockMvc.perform(get("/api/vendor/99")).andExpect(status().isNotFound());
  }

  @Test
  void mvcTestGetByName() throws Exception {
    when(vendorService.getByName(TEST_VENDOR_NAME)).thenReturn(vendor);
    mockMvc
        .perform(get("/api/vendor/name/" + TEST_VENDOR_NAME))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"))
        .andExpect(jsonPath("$.name").value(TEST_VENDOR_NAME));
  }

  @Test
  void mvcTestGetByNameNotFound() throws Exception {
    when(vendorService.getByName("unknown")).thenReturn(null);
    mockMvc.perform(get("/api/vendor/name/unknown")).andExpect(status().isNotFound());
  }
}
