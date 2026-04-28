package org.midnightbsd.advisory.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.midnightbsd.advisory.model.Advisory;

public record AdvisoryDto(
    int id,
    String cveId,
    String description,
    Date publishedDate,
    Date lastModifiedDate,
    String severity,
    String problemType,
    List<CvssMetrics3Dto> cvssMetrics3,
    List<ProductDto> products,
    List<PackageFixedDto> fixedPackages) {

  public static AdvisoryDto from(Advisory advisory) {
    List<CvssMetrics3Dto> metrics =
        advisory.getCvssMetrics3() != null
            ? advisory.getCvssMetrics3().stream().map(CvssMetrics3Dto::from).toList()
            : List.of();

    List<ProductDto> products =
        advisory.getProducts() != null
            ? advisory.getProducts().stream().map(ProductDto::from).toList()
            : List.of();

    List<PackageFixedDto> fixedPackages =
        advisory.getFixedPackages() != null
            ? advisory.getFixedPackages().stream().map(PackageFixedDto::from).toList()
            : List.of();

    return new AdvisoryDto(
        advisory.getId(),
        advisory.getCveId(),
        advisory.getDescription(),
        advisory.getPublishedDate(),
        advisory.getLastModifiedDate(),
        advisory.getSeverity(),
        advisory.getProblemType(),
        metrics,
        products,
        fixedPackages);
  }
}
