package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Lucas Holt
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CvssV3 {
    private String version;
    private String vectorString;
    private String attackVector;
    private String attackComplexity;
    private String privilegesRequired;
    private String userInteraction;
    private String scope;
    private String confidentialityImpact;
    private String integrityImpact;

    private String availabilityImpact;

    private BigDecimal baseScore;
    private String baseSeverity;
}