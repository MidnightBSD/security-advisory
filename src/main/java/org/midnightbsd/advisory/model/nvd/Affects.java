package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Lucas Holt
 */
@Data
public class Affects {

     @JsonProperty("vendor")
     private Vendor vendor;
}
