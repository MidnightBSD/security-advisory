package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Lucas Holt
 */
@Data
public class Description {
    @JsonProperty("description_data")
    private DescriptionData descriptionData;
}
