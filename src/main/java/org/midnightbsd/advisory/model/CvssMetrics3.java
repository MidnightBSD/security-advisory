package org.midnightbsd.advisory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "cvss_metrics3")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CvssMetrics3 {
    @JsonIgnore private static final long serialVersionUID = -2413883936383873806L;

    @Id
    @SequenceGenerator(
            name = "cvss_metrics3_id_seq",
            sequenceName = "cvss_metrics3_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cvss_metrics3_id_seq")
    @Column(name = "id", updatable = false)
    private int id;

    @Column(name = "source", length = 100)
    private String source;

    @Column(name = "type", length = 200)
    private String type;

    @Column(name = "exploitability_score", length = 10)
    private String exploitabilityScore;

    @Column(name = "impact_score", length = 10)
    private String impactScore;

    @Column(name = "version", length = 10)
    private String version;

    @Column(name = "vector_string", length = 100)
    private String vectorString;

    @Column(name = "attack_vector", length = 100)
    private String attackVector;

    @Column(name = "attack_complexity", length = 100)
    private String attackComplexity;

    @Column(name = "privileges_required", length = 100)
    private String privilegesRequired;

    @Column(name = "user_interaction", length = 100)
    private String userInteraction;

    @Column(name = "scope", length = 100)
    private String scope;

    @Column(name = "confidentiality_impact", length = 100)
    private String confidentialityImpact;

    @Column(name = "integrity_impact", length = 100)
    private String integrityImpact;

    @Column(name = "availability_impact", length = 100)
    private String availabilityImpact;

    @Column(name = "base_score", length = 100)
    private String baseScore;

    @Column(name = "base_severity", length = 100)
    private String baseSeverity;

    @Column(name = "access_vector", length = 100)
    private String accessVector;

    @Column(name = "access_complexity", length = 100)
    private String accessComplexity;

    @Column(name = "authentication", length = 100)
    private String authentication;

    @ManyToOne
    @JoinColumn(name = "advisory_id")
    private Advisory advisory;
}
