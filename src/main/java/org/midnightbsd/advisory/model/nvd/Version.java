package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Lucas Holt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Version {

    @JsonProperty("version_data")
    List<VersionData> versionData;

    public List<VersionData> getVersionData() {
        return versionData;
    }

    public void setVersionData(final List<VersionData> versionData) {
        this.versionData = versionData;
    }
}
