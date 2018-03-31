package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Lucas Holt
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseMetricV3 {

    private CvssV3 cvssV3;

    private BigDecimal exploitabilityScore;
    private BigDecimal impactScore;
}