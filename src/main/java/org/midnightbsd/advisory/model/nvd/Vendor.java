package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Lucas Holt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vendor {

    @JsonProperty("vendor_data")
    private List<VendorData> vendorData;

    public List<VendorData> getVendorData() {
        return vendorData;
    }

    public void setVendorData(final List<VendorData> vendorData) {
        this.vendorData = vendorData;
    }
}
