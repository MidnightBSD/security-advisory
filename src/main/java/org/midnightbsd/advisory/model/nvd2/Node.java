package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

import java.util.List;

@Setter
public class Node {
  /**
   * OR or AND operator
   * @return
   */
  @JsonProperty("operator")
  public String getOperator() {
    return this.operator;
  }

  String operator;

  @JsonProperty("negate")
  public boolean getNegate() {
    return this.negate;
  }

  boolean negate;

  @JsonProperty("cpeMatch")
  public List<CpeMatch> getCpeMatch() {
    return this.cpeMatch;
  }

  List<CpeMatch> cpeMatch;
}
