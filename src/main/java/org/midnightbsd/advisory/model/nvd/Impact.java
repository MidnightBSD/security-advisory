package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author Lucas Holt
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Impact {

    BaseMetricV3 baseMetricV3;

    BaseMetricV2 baseMetricV2;
}
