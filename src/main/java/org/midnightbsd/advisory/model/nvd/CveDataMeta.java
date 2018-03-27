package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Lucas Holt
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CveDataMeta {

    @JsonProperty("ID")
    private String ID;

    @JsonProperty("ASSIGNER")
    private String ASSIGNER;
}
