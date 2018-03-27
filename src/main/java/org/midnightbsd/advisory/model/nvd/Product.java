package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Lucas Holt
 */
@Data
public class Product {

    @JsonProperty("product_data")
    private List<ProductData> productData;
}
