package org.midnightbsd.advisory.model.search;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@ToString
@EqualsAndHashCode
public class CvssMetric implements Serializable {
    @Serial
    private static final long serialVersionUID = 3432313031363531585L;

    @Getter
    @Setter
    private String source;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private String exploitabilityScore;

    @Getter @Setter
    private String impactScore;

    @Getter @Setter
    private String version;

    @Getter @Setter
    private String vectorString;

    @Getter @Setter
    private String attackVector;

    @Getter @Setter
    private String attackComplexity;

    @Getter @Setter
    private String privilegesRequired;

    @Getter @Setter
    private String userInteraction;

    @Getter @Setter
    private String scope;

    @Getter @Setter
    private String confidentialityImpact;

    @Getter @Setter
    private String integrityImpact;

    @Getter @Setter
    private String availabilityImpact;

    @Getter @Setter
    private String baseScore;

    @Getter @Setter
    private String baseSeverity;

    @Getter @Setter
    private String accessVector;

    @Getter @Setter
    private String accessComplexity;

    @Getter @Setter
    private String authentication;
}
