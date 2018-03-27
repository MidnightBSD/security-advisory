package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Lucas Holt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Affects {

    @JsonProperty("vendor")
    private Vendor vendor;

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(final Vendor vendor) {
        this.vendor = vendor;
    }


}
