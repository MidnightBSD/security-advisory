package org.midnightbsd.advisory.model.nvd2;


import com.fasterxml.jackson.annotation.JsonProperty;

public class CvssMetricV31{
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
}