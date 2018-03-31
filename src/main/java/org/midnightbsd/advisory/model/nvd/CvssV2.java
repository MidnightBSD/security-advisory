package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Lucas Holt
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CvssV2 {
    private String version;
    private String vectorString;
    private String accessComplexity;
    private String authentication;
    private String confidentialityImpact;
    private String integrityImpact;
    private String availabilityImpact;
    private BigDecimal baseScore;
}