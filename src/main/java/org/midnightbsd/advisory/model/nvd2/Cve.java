package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
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
    public ArrayList<Description> getDescriptions() {
        return this.descriptions; }
    public void setDescriptions(ArrayList<Description> descriptions) {
        this.descriptions = descriptions; }
    ArrayList<Description> descriptions;
    @JsonProperty("metrics")
    public Metrics getMetrics() {
        return this.metrics; }
    public void setMetrics(Metrics metrics) {
        this.metrics = metrics; }
    Metrics metrics;
    @JsonProperty("weaknesses")
    public ArrayList<Weakness> getWeaknesses() {
        return this.weaknesses; }
    public void setWeaknesses(ArrayList<Weakness> weaknesses) {
        this.weaknesses = weaknesses; }
    ArrayList<Weakness> weaknesses;
    @JsonProperty("configurations")
    public ArrayList<Configuration> getConfigurations() {
        return this.configurations; }
    public void setConfigurations(ArrayList<Configuration> configurations) {
        this.configurations = configurations; }
    ArrayList<Configuration> configurations;
    @JsonProperty("references")
    public ArrayList<Reference> getReferences() {
        return this.references; }
    public void setReferences(ArrayList<Reference> references) {
        this.references = references; }
    ArrayList<Reference> references;
}