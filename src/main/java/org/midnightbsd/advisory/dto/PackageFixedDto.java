package org.midnightbsd.advisory.dto;

import org.midnightbsd.advisory.model.PackageFixed;

public record PackageFixedDto(int id, String name, String version) {

  public static PackageFixedDto from(PackageFixed pkg) {
    return new PackageFixedDto(pkg.getId(), pkg.getName(), pkg.getVersion());
  }
}
