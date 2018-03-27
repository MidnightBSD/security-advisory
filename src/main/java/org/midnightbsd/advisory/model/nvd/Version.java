package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Lucas Holt
 */
@Data
public class Version {

    @JsonProperty("version_data")
    List<VersionData> versionData;
}
