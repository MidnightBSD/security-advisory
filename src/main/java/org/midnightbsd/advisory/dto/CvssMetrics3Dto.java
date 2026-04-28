package org.midnightbsd.advisory.dto;

import org.midnightbsd.advisory.model.CvssMetrics3;

public record CvssMetrics3Dto(
    int id,
    String source,
    String type,
    String exploitabilityScore,
    String impactScore,
    String version,
    String vectorString,
    String attackVector,
    String attackComplexity,
    String privilegesRequired,
    String userInteraction,
    String scope,
    String confidentialityImpact,
    String integrityImpact,
    String availabilityImpact,
    String baseScore,
    String baseSeverity,
    String accessVector,
    String accessComplexity,
    String authentication) {

  public static CvssMetrics3Dto from(CvssMetrics3 m) {
    return new CvssMetrics3Dto(
        m.getId(),
        m.getSource(),
        m.getType(),
        m.getExploitabilityScore(),
        m.getImpactScore(),
        m.getVersion(),
        m.getVectorString(),
        m.getAttackVector(),
        m.getAttackComplexity(),
        m.getPrivilegesRequired(),
        m.getUserInteraction(),
        m.getScope(),
        m.getConfidentialityImpact(),
        m.getIntegrityImpact(),
        m.getAvailabilityImpact(),
        m.getBaseScore(),
        m.getBaseSeverity(),
        m.getAccessVector(),
        m.getAccessComplexity(),
        m.getAuthentication());
  }
}
