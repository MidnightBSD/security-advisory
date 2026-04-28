package org.midnightbsd.advisory.dto;

import org.midnightbsd.advisory.model.Product;

public record ProductDto(int id, String name, String version, String vendorName) {

  public static ProductDto from(Product product) {
    String vendorName = product.getVendor() != null ? product.getVendor().getName() : null;
    return new ProductDto(product.getId(), product.getName(), product.getVersion(), vendorName);
  }
}
