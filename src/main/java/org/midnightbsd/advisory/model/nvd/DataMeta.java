package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Lucas Holt
 */
@Data
public class DataMeta {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("ASSIGNER")
    private String assigner;
}
