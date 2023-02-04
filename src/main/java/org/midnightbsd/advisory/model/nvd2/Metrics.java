package org.midnightbsd.advisory.model.nvd2;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Metrics{
    @JsonProperty("cvssMetricV31")
    public ArrayList<CvssMetricV31> getCvssMetricV31() {
        return this.cvssMetricV31; }
    public void setCvssMetricV31(ArrayList<CvssMetricV31> cvssMetricV31) {
        this.cvssMetricV31 = cvssMetricV31; }
    ArrayList<CvssMetricV31> cvssMetricV31;
    @JsonProperty("cvssMetricV2")
    public ArrayList<CvssMetricV2> getCvssMetricV2() {
        return this.cvssMetricV2; }
    public void setCvssMetricV2(ArrayList<CvssMetricV2> cvssMetricV2) {
        this.cvssMetricV2 = cvssMetricV2; }
    ArrayList<CvssMetricV2> cvssMetricV2;
}