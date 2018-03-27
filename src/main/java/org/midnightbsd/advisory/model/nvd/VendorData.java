package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Lucas Holt
 */
@Data
public class VendorData {

    @JsonProperty("vendor_name")
    private String vendorName;

    @JsonProperty("product")
    private Product product;

    
}
