package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Lucas Holt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @JsonProperty("product_data")
    private List<ProductData> productData;

    public List<ProductData> getProductData() {
        return productData;
    }

    public void setProductData(final List<ProductData> productData) {
        this.productData = productData;
    }
}
