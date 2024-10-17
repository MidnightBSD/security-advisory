package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

@Setter
public class CpeMatch {
  @JsonProperty("vulnerable")
  public boolean getVulnerable() {
    return this.vulnerable;
  }

  boolean vulnerable;

  @JsonProperty("criteria")
  public String getCriteria() {
    return this.criteria;
  }

  String criteria;

  @JsonProperty("versionEndExcluding")
  public String getVersionEndExcluding() {
    return this.versionEndExcluding;
  }

  String versionEndExcluding;

  @JsonProperty("versionStartIncluding")
  public String getVersionStartIncluding() {
    return this.versionStartIncluding;
  }

  String versionStartIncluding;

  @JsonProperty("versionEndIncluding")
  public String getVersionEndIncluding() {
    return this.versionEndIncluding;
  }

  String versionEndIncluding;

  @JsonProperty("versionStartExcluding")
  public String getVersionStartExcluding() {
    return this.versionStartExcluding;
  }

  String versionStartExcluding;

  @JsonProperty("matchCriteriaId")
  public String getMatchCriteriaId() {
    return this.matchCriteriaId;
  }

  String matchCriteriaId;
}
