package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CpeMatch{
    @JsonProperty("vulnerable")
    public boolean getVulnerable() {
        return this.vulnerable; }
    public void setVulnerable(boolean vulnerable) {
        this.vulnerable = vulnerable; }
    boolean vulnerable;
    @JsonProperty("criteria")
    public String getCriteria() {
        return this.criteria; }
    public void setCriteria(String criteria) {
        this.criteria = criteria; }
    String criteria;
    @JsonProperty("versionEndExcluding")
    public String getVersionEndExcluding() {
        return this.versionEndExcluding; }
    public void setVersionEndExcluding(String versionEndExcluding) {
        this.versionEndExcluding = versionEndExcluding; }
    String versionEndExcluding;
    @JsonProperty("matchCriteriaId")
    public String getMatchCriteriaId() {
        return this.matchCriteriaId; }
    public void setMatchCriteriaId(String matchCriteriaId) {
        this.matchCriteriaId = matchCriteriaId; }
    String matchCriteriaId;
}