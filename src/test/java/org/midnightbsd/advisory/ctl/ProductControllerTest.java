package org.midnightbsd.advisory.ctl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.midnightbsd.advisory.ctl.api.ProductController;
import org.midnightbsd.advisory.dto.ProductDto;
import org.midnightbsd.advisory.model.Product;
import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.repository.ProductRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

  private static final String TEST_PRODUCT_NAME = "curl";
  private static final String TEST_VERSION = "0.15.1b";
  private static final String TEST_VENDOR_NAME = "haxx";

  private MockMvc mockMvc;

  @Mock private ProductRepository productRepository;

  @InjectMocks private ProductController controller;

  private Product product;

  @BeforeEach
  void setup() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();

    Vendor vendor = new Vendor();
    vendor.setId(1);
    vendor.setName(TEST_VENDOR_NAME);

    product = new Product();
    product.setId(1);
    product.setName(TEST_PRODUCT_NAME);
    product.setVersion(TEST_VERSION);
    product.setVendor(vendor);
  }

  @Test
  void testGetById() {
    when(productRepository.findById(1)).thenReturn(Optional.of(product));
    ResponseEntity<List<ProductDto>> result = controller.get("1");
    assertNotNull(result);
    assertNotNull(result.getBody());
    assertEquals(1, result.getBody().size());
    assertEquals(TEST_PRODUCT_NAME, result.getBody().get(0).name());
    assertEquals(TEST_VERSION, result.getBody().get(0).version());
    assertEquals(TEST_VENDOR_NAME, result.getBody().get(0).vendorName());
  }

  @Test
  void testGetByIdNotFound() {
    when(productRepository.findById(99)).thenReturn(Optional.empty());
    ResponseEntity<List<ProductDto>> result = controller.get("99");
    assertEquals(404, result.getStatusCode().value());
  }

  @Test
  void testGetByVersion() {
    when(productRepository.findByVersion(TEST_VERSION)).thenReturn(List.of(product));
    ResponseEntity<List<ProductDto>> result = controller.get(TEST_VERSION);
    assertNotNull(result);
    assertNotNull(result.getBody());
    assertEquals(1, result.getBody().size());
    assertEquals(TEST_PRODUCT_NAME, result.getBody().get(0).name());
    assertEquals(TEST_VERSION, result.getBody().get(0).version());
  }

  @Test
  void testGetByVersionNotFound() {
    when(productRepository.findByVersion("9.9.9z")).thenReturn(Collections.emptyList());
    ResponseEntity<List<ProductDto>> result = controller.get("9.9.9z");
    assertEquals(404, result.getStatusCode().value());
  }

  @Test
  void testGetByNameAndVersion() {
    when(productRepository.findByNameAndVersion(TEST_PRODUCT_NAME, TEST_VERSION))
        .thenReturn(product);
    ResponseEntity<ProductDto> result = controller.get(TEST_PRODUCT_NAME, TEST_VERSION);
    assertNotNull(result);
    assertNotNull(result.getBody());
    assertEquals(TEST_PRODUCT_NAME, result.getBody().name());
    assertEquals(TEST_VERSION, result.getBody().version());
  }

  @Test
  void testGetByNameAndVersionNotFound() {
    when(productRepository.findByNameAndVersion(TEST_PRODUCT_NAME, "9.9.9z")).thenReturn(null);
    ResponseEntity<ProductDto> result = controller.get(TEST_PRODUCT_NAME, "9.9.9z");
    assertEquals(404, result.getStatusCode().value());
  }

  @Test
  void mvcTestList() throws Exception {
    when(productRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(product)));
    mockMvc
        .perform(get("/api/product"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"));
  }

  @Test
  void mvcTestGetById() throws Exception {
    when(productRepository.findById(1)).thenReturn(Optional.of(product));
    mockMvc
        .perform(get("/api/product/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"))
        .andExpect(jsonPath("$[0].name").value(TEST_PRODUCT_NAME))
        .andExpect(jsonPath("$[0].version").value(TEST_VERSION));
  }

  @Test
  void mvcTestGetByVersion() throws Exception {
    when(productRepository.findByVersion(TEST_VERSION)).thenReturn(List.of(product));
    mockMvc
        .perform(get("/api/product/" + TEST_VERSION))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"))
        .andExpect(jsonPath("$[0].name").value(TEST_PRODUCT_NAME))
        .andExpect(jsonPath("$[0].version").value(TEST_VERSION))
        .andExpect(jsonPath("$[0].vendorName").value(TEST_VENDOR_NAME));
  }

  @Test
  void mvcTestGetByVersionNotFound() throws Exception {
    when(productRepository.findByVersion("9.9.9z")).thenReturn(Collections.emptyList());
    mockMvc.perform(get("/api/product/9.9.9z")).andExpect(status().isNotFound());
  }

  @Test
  void mvcTestGetByNameAndVersion() throws Exception {
    when(productRepository.findByNameAndVersion(TEST_PRODUCT_NAME, TEST_VERSION))
        .thenReturn(product);
    mockMvc
        .perform(get("/api/product/name/" + TEST_PRODUCT_NAME + "/version/" + TEST_VERSION))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"))
        .andExpect(jsonPath("$.name").value(TEST_PRODUCT_NAME))
        .andExpect(jsonPath("$.version").value(TEST_VERSION));
  }
}
