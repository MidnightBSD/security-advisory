package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author Lucas Holt
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeCpe {

    private Boolean vulnerable;

    /**
     * example "cpe:/a:microsoft:sharepoint_enterprise_server:2016"
     */
    private String cpe22Uri;

    /**
     * example "cpe:2.3:a:microsoft:sharepoint_enterprise_server:2016:*:*:*:*:*:*:*"
     */
    private String cpe23Uri;
}
