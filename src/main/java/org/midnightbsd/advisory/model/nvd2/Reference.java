package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Reference{
    @JsonProperty("url")
    public String getUrl() {
        return this.url; }
    public void setUrl(String url) {
        this.url = url; }
    String url;
    @JsonProperty("source")
    public String getSource() {
        return this.source; }
    public void setSource(String source) {
        this.source = source; }
    String source;
    @JsonProperty("tags")
    public List<String> getTags() {
        return this.tags; }
    public void setTags(List<String> tags) {
        this.tags = tags; }
    List<String> tags;
}