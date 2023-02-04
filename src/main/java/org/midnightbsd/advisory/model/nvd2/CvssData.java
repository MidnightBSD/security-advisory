package org.midnightbsd.advisory.model.nvd2;


import com.fasterxml.jackson.annotation.JsonProperty;

public class CvssData{
    @JsonProperty("version")
    public String getVersion() {
        return this.version; }
    public void setVersion(String version) {
        this.version = version; }
    String version;
    @JsonProperty("vectorString")
    public String getVectorString() {
        return this.vectorString; }
    public void setVectorString(String vectorString) {
        this.vectorString = vectorString; }
    String vectorString;
    @JsonProperty("attackVector")
    public String getAttackVector() {
        return this.attackVector; }
    public void setAttackVector(String attackVector) {
        this.attackVector = attackVector; }
    String attackVector;
    @JsonProperty("attackComplexity")
    public String getAttackComplexity() {
        return this.attackComplexity; }
    public void setAttackComplexity(String attackComplexity) {
        this.attackComplexity = attackComplexity; }
    String attackComplexity;
    @JsonProperty("privilegesRequired")
    public String getPrivilegesRequired() {
        return this.privilegesRequired; }
    public void setPrivilegesRequired(String privilegesRequired) {
        this.privilegesRequired = privilegesRequired; }
    String privilegesRequired;
    @JsonProperty("userInteraction")
    public String getUserInteraction() {
        return this.userInteraction; }
    public void setUserInteraction(String userInteraction) {
        this.userInteraction = userInteraction; }
    String userInteraction;
    @JsonProperty("scope")
    public String getScope() {
        return this.scope; }
    public void setScope(String scope) {
        this.scope = scope; }
    String scope;
    @JsonProperty("confidentialityImpact")
    public String getConfidentialityImpact() {
        return this.confidentialityImpact; }
    public void setConfidentialityImpact(String confidentialityImpact) {
        this.confidentialityImpact = confidentialityImpact; }
    String confidentialityImpact;
    @JsonProperty("integrityImpact")
    public String getIntegrityImpact() {
        return this.integrityImpact; }
    public void setIntegrityImpact(String integrityImpact) {
        this.integrityImpact = integrityImpact; }
    String integrityImpact;
    @JsonProperty("availabilityImpact")
    public String getAvailabilityImpact() {
        return this.availabilityImpact; }
    public void setAvailabilityImpact(String availabilityImpact) {
        this.availabilityImpact = availabilityImpact; }
    String availabilityImpact;
    @JsonProperty("baseScore")
    public double getBaseScore() {
        return this.baseScore; }
    public void setBaseScore(double baseScore) {
        this.baseScore = baseScore; }
    double baseScore;
    @JsonProperty("baseSeverity")
    public String getBaseSeverity() {
        return this.baseSeverity; }
    public void setBaseSeverity(String baseSeverity) {
        this.baseSeverity = baseSeverity; }
    String baseSeverity;
    @JsonProperty("accessVector")
    public String getAccessVector() {
        return this.accessVector; }
    public void setAccessVector(String accessVector) {
        this.accessVector = accessVector; }
    String accessVector;
    @JsonProperty("accessComplexity")
    public String getAccessComplexity() {
        return this.accessComplexity; }
    public void setAccessComplexity(String accessComplexity) {
        this.accessComplexity = accessComplexity; }
    String accessComplexity;
    @JsonProperty("authentication")
    public String getAuthentication() {
        return this.authentication; }
    public void setAuthentication(String authentication) {
        this.authentication = authentication; }
    String authentication;
}
