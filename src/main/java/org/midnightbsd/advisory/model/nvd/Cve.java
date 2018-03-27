package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Lucas Holt
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cve {

    @JsonProperty("data_type")
    private String dataType;

    @JsonProperty("data_format")
    private String dataFormat;

    @JsonProperty("data_version")
    private String dataVersion;

    @JsonProperty("CVE_data_meta")
    private CveDataMeta cveDataMeta;

    @JsonProperty("affects")
    private Affects affects;

    @JsonProperty("problemtype")
    private ProblemType problemType;

    @JsonProperty("references")
    private References references;

    @JsonProperty("description")
    private Description description;
}
