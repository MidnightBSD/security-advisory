package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Lucas Holt
 */
@Data
public class CveData {

    @JsonProperty("CVE_data_type")
    private String type;

    @JsonProperty("CVE_data_format")
    private String format;

    @JsonProperty("CVE_data_version")
    private String version;

    @JsonProperty("CVE_data_numberOfCVEs")
    private String numberOfCVE;

    @JsonProperty("CVE_data_timestamp")
    private Data timestamp;

    @JsonProperty("CVE_Items")
    private List<Cve> items;
}
