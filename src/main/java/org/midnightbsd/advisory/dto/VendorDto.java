package org.midnightbsd.advisory.dto;

import org.midnightbsd.advisory.model.Vendor;

public record VendorDto(int id, String name) {

  public static VendorDto from(Vendor vendor) {
    return new VendorDto(vendor.getId(), vendor.getName());
  }
}
