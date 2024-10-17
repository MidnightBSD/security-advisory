package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

import java.util.List;
import java.util.Date;

@Setter
public class Cve {
  @JsonProperty("id")
  public String getId() {
    return this.id;
  }

    String id;

  @JsonProperty("sourceIdentifier")
  public String getSourceIdentifier() {
    return this.sourceIdentifier;
  }

    String sourceIdentifier;

  @JsonProperty("published")
  public Date getPublished() {
    return this.published;
  }

    Date published;

  @JsonProperty("lastModified")
  public Date getLastModified() {
    return this.lastModified;
  }

    Date lastModified;

  @JsonProperty("vulnStatus")
  public String getVulnStatus() {
    return this.vulnStatus;
  }

    String vulnStatus;

  @JsonProperty("descriptions")
  public List<Description> getDescriptions() {
    return this.descriptions;
  }

    List<Description> descriptions;

  @JsonProperty("metrics")
  public Metrics getMetrics() {
    return this.metrics;
  }

    Metrics metrics;

  @JsonProperty("weaknesses")
  public List<Weakness> getWeaknesses() {
    return this.weaknesses;
  }

    List<Weakness> weaknesses;

  @JsonProperty("configurations")
  public List<Configuration> getConfigurations() {
    return this.configurations;
  }

    List<Configuration> configurations;

  @JsonProperty("references")
  public List<Reference> getReferences() {
    return this.references;
  }

    List<Reference> references;
}
