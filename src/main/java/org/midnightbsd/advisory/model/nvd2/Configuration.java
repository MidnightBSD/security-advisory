package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Configuration{
    @JsonProperty("nodes")
    public List<Node> getNodes() {
        return this.nodes; }
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes; }
    List<Node> nodes;
}
