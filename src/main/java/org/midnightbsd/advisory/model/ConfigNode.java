package org.midnightbsd.advisory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Lucas Holt
 */
@Entity
@Table(name = "config_node")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigNode {
    @Id
    @SequenceGenerator(name = "config_node_id_seq",
            sequenceName = "config_node_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "config_node_id_seq")
    @Column(name = "id", updatable = false)
    private int id;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "operator", length = 10)
    private String operator;

    @ManyToOne
    @JoinColumn(name = "advisory_id")
    private Advisory advisory;

    @JsonIgnore
    @OneToMany(mappedBy = "configNode")
    private Set<ConfigNodeCpe> configNodeCpes;
}
