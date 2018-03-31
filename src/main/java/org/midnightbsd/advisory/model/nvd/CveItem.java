package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Lucas Holt
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CveItem {

    @JsonProperty("cve")
    private Cve cve;

    // configurations

    private Impact impact;

    @JsonProperty("publishedDate")
    private String publishedDate;

    @JsonProperty("lastModifiedDate")
    private String lastModifiedDate;
}
