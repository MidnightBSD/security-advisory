package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Node{
    @JsonProperty("operator")
    public String getOperator() {
        return this.operator; }
    public void setOperator(String operator) {
        this.operator = operator; }
    String operator;
    @JsonProperty("negate")
    public boolean getNegate() {
        return this.negate; }
    public void setNegate(boolean negate) {
        this.negate = negate; }
    boolean negate;
    @JsonProperty("cpeMatch")
    public List<CpeMatch> getCpeMatch() {
        return this.cpeMatch; }
    public void setCpeMatch(List<CpeMatch> cpeMatch) {
        this.cpeMatch = cpeMatch; }
    List<CpeMatch> cpeMatch;
}