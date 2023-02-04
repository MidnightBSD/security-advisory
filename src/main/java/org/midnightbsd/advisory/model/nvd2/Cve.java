package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Date;

public class Cve{
    @JsonProperty("id")
    public String getId() {
        return this.id; }
    public void setId(String id) {
        this.id = id; }
    String id;
    @JsonProperty("sourceIdentifier")
    public String getSourceIdentifier() {
        return this.sourceIdentifier; }
    public void setSourceIdentifier(String sourceIdentifier) {
        this.sourceIdentifier = sourceIdentifier; }
    String sourceIdentifier;
    @JsonProperty("published")
    public Date getPublished() {
        return this.published; }
    public void setPublished(Date published) {
        this.published = published; }
    Date published;
    @JsonProperty("lastModified")
    public Date getLastModified() {
        return this.lastModified; }
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified; }
    Date lastModified;
    @JsonProperty("vulnStatus")
    public String getVulnStatus() {
        return this.vulnStatus; }
    public void setVulnStatus(String vulnStatus) {
        this.vulnStatus = vulnStatus; }
    String vulnStatus;
    @JsonProperty("descriptions")
    public List<Description> getDescriptions() {
        return this.descriptions; }
    public void setDescriptions(List<Description> descriptions) {
        this.descriptions = descriptions; }
    List<Description> descriptions;
    @JsonProperty("metrics")
    public Metrics getMetrics() {
        return this.metrics; }
    public void setMetrics(Metrics metrics) {
        this.metrics = metrics; }
    Metrics metrics;
    @JsonProperty("weaknesses")
    public List<Weakness> getWeaknesses() {
        return this.weaknesses; }
    public void setWeaknesses(List<Weakness> weaknesses) {
        this.weaknesses = weaknesses; }
    List<Weakness> weaknesses;
    @JsonProperty("configurations")
    public List<Configuration> getConfigurations() {
        return this.configurations; }
    public void setConfigurations(List<Configuration> configurations) {
        this.configurations = configurations; }
    List<Configuration> configurations;
    @JsonProperty("references")
    public List<Reference> getReferences() {
        return this.references; }
    public void setReferences(List<Reference> references) {
        this.references = references; }
    List<Reference> references;
}