package org.midnightbsd.advisory.model.nvd2;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

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
    public ArrayList<CpeMatch> getCpeMatch() {
        return this.cpeMatch; }
    public void setCpeMatch(ArrayList<CpeMatch> cpeMatch) {
        this.cpeMatch = cpeMatch; }
    ArrayList<CpeMatch> cpeMatch;
}