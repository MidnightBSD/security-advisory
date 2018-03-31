package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Lucas Holt
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseMetricV2 {

    private CvssV2 cvssV2;

    private String severity;
    private BigDecimal exploitabilityScore;
    private BigDecimal impactScore;

    private Boolean obtainAllPrivilege;
    private Boolean obtainUserPrivilege;
    private Boolean obtainOtherPrivilege;
    private Boolean userInteractionRequired;

}