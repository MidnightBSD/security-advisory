package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Description2{
    @JsonProperty("lang")
    public String getLang() {
        return this.lang; }
    public void setLang(String lang) {
        this.lang = lang; }
    String lang;
    @JsonProperty("value")
    public String getValue() {
        return this.value; }
    public void setValue(String value) {
        this.value = value; }
    String value;
}