package org.midnightbsd.advisory.dto;

import java.util.Date;
import java.util.List;
import org.midnightbsd.advisory.model.Advisory;

public record CpeRangeAdvisoryDto(
    int id,
    String cveId,
    String description,
    Date publishedDate,
    Date lastModifiedDate,
    String severity,
    String problemType,
    List<CpeConfigurationDto> configurations) {

  public static CpeRangeAdvisoryDto from(
      Advisory advisory, List<CpeConfigurationDto> configurations) {
    return new CpeRangeAdvisoryDto(
        advisory.getId(),
        advisory.getCveId(),
        advisory.getDescription(),
        advisory.getPublishedDate(),
        advisory.getLastModifiedDate(),
        advisory.getSeverity(),
        advisory.getProblemType(),
        configurations);
  }
}
