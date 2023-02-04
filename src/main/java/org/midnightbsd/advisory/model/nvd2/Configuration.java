package org.midnightbsd.advisory.model.nvd2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Configuration{
    @JsonProperty("nodes")
    public ArrayList<Node> getNodes() {
        return this.nodes; }
    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes; }
    ArrayList<Node> nodes;
}
