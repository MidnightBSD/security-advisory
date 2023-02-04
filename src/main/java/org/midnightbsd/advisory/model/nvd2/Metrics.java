package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Metrics{
    @JsonProperty("cvssMetricV31")
    public List<CvssMetricV31> getCvssMetricV31() {
        return this.cvssMetricV31; }
    public void setCvssMetricV31(List<CvssMetricV31> cvssMetricV31) {
        this.cvssMetricV31 = cvssMetricV31; }
    List<CvssMetricV31> cvssMetricV31;
    @JsonProperty("cvssMetricV2")
    public List<CvssMetricV2> getCvssMetricV2() {
        return this.cvssMetricV2; }
    public void setCvssMetricV2(List<CvssMetricV2> cvssMetricV2) {
        this.cvssMetricV2 = cvssMetricV2; }
    List<CvssMetricV2> cvssMetricV2;
}