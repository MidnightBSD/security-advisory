package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Weakness{
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
    @JsonProperty("description")
    public List<Description> getDescription() {
        return this.description; }
    public void setDescription(List<Description> description) {
        this.description = description; }
    List<Description> description;
}

