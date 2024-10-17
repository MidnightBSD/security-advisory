package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

import java.util.List;

@Setter
public class Configuration {
  @JsonProperty("nodes")
  public List<Node> getNodes() {
    return this.nodes;
  }

  List<Node> nodes;
}
