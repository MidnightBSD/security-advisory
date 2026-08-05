package org.midnightbsd.advisory.dto;

import org.midnightbsd.advisory.model.ConfigNodeCpe;

public record CpeRangeDto(
    String criteria,
    Boolean vulnerable,
    String matchCriteriaId,
    String versionStartIncluding,
    String versionStartExcluding,
    String versionEndIncluding,
    String versionEndExcluding) {

  public static CpeRangeDto from(ConfigNodeCpe cpe) {
    return new CpeRangeDto(
        cpe.getCpe23Uri(),
        cpe.getVulnerable(),
        cpe.getMatchCriteriaId(),
        cpe.getVersionStartIncluding(),
        cpe.getVersionStartExcluding(),
        cpe.getVersionEndIncluding(),
        cpe.getVersionEndExcluding());
  }
}
