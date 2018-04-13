package org.midnightbsd.advisory.model.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * @author Lucas Holt
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {

    private String operator;

    private List<NodeCpe> cpe;

    /**
     * this can be null if OR operator is used.
     * Used when product in cpe list is 
     */
    private List<Node> children;
}