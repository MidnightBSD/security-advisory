package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author Lucas Holt
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CveDataPage {

    private int resultsPerPage;

    private long startIndex;

    private long totalResults;

    private CveData result;
}
