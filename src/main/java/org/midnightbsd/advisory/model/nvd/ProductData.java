package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Lucas Holt
 */
@Data
public class ProductData {

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("version")
    private Version version;
}
