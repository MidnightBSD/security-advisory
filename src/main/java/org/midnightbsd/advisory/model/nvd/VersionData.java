package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Lucas Holt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionData {

    @JsonProperty("version_value")
    private String versionValue;

    public String getVersionValue() {
        return versionValue;
    }

    public void setVersionValue(final String version_value) {
        this.versionValue = version_value;
    }
}
