package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CvssMetricV2 {
    @JsonProperty("source")
    public String getSource() {
        return this.source; }
    public void setSource(String source) {
        this.source = source; }
    String source;
    @JsonProperty("type")
    public String getType() {
        return this.type; }
    public void setType(String type) {
        this.type = type; }
    String type;
    @JsonProperty("cvssData")
    public CvssData getCvssData() {
        return this.cvssData; }
    public void setCvssData(CvssData cvssData) {
        this.cvssData = cvssData; }
    CvssData cvssData;
    @JsonProperty("baseSeverity")
    public String getBaseSeverity() {
        return this.baseSeverity; }
    public void setBaseSeverity(String baseSeverity) {
        this.baseSeverity = baseSeverity; }
    String baseSeverity;
    @JsonProperty("exploitabilityScore")
    public double getExploitabilityScore() {
        return this.exploitabilityScore; }
    public void setExploitabilityScore(double exploitabilityScore) {
        this.exploitabilityScore = exploitabilityScore; }
    double exploitabilityScore;
    @JsonProperty("impactScore")
    public double getImpactScore() {
        return this.impactScore; }
    public void setImpactScore(double impactScore) {
        this.impactScore = impactScore; }
    double impactScore;
    @JsonProperty("acInsufInfo")
    public boolean getAcInsufInfo() {
        return this.acInsufInfo; }
    public void setAcInsufInfo(boolean acInsufInfo) {
        this.acInsufInfo = acInsufInfo; }
    boolean acInsufInfo;
    @JsonProperty("obtainAllPrivilege")
    public boolean getObtainAllPrivilege() {
        return this.obtainAllPrivilege; }
    public void setObtainAllPrivilege(boolean obtainAllPrivilege) {
        this.obtainAllPrivilege = obtainAllPrivilege; }
    boolean obtainAllPrivilege;
    @JsonProperty("obtainUserPrivilege")
    public boolean getObtainUserPrivilege() {
        return this.obtainUserPrivilege; }
    public void setObtainUserPrivilege(boolean obtainUserPrivilege) {
        this.obtainUserPrivilege = obtainUserPrivilege; }
    boolean obtainUserPrivilege;
    @JsonProperty("obtainOtherPrivilege")
    public boolean getObtainOtherPrivilege() {
        return this.obtainOtherPrivilege; }
    public void setObtainOtherPrivilege(boolean obtainOtherPrivilege) {
        this.obtainOtherPrivilege = obtainOtherPrivilege; }
    boolean obtainOtherPrivilege;
    @JsonProperty("userInteractionRequired")
    public boolean getUserInteractionRequired() {
        return this.userInteractionRequired; }
    public void setUserInteractionRequired(boolean userInteractionRequired) {
        this.userInteractionRequired = userInteractionRequired; }
    boolean userInteractionRequired;
}
