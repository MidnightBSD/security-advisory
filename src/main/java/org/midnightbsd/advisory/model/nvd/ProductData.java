package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Lucas Holt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductData {

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("version")
    private Version version;

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(final Version version) {
        this.version = version;
    }
}
