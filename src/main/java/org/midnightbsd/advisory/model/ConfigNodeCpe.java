package org.midnightbsd.advisory.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Lucas Holt
 */
@Entity
@Table(name = "config_node_cpe")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigNodeCpe {
    @Id
    @SequenceGenerator(name = "config_node_cpe_id_seq",
            sequenceName = "config_node_cpe_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "config_node_cpe_id_seq")
    @Column(name = "id", updatable = false)
    private int id;

    @Column(name = "vulnerable")
    private Boolean vulnerable;

    @Column(name = "cpe22Uri", length = 100)
    private String cpe22Uri;

    @Column(name = "cpe23Uri", length = 100)
    private String cpe23Uri;

    @ManyToOne
    @JoinColumn(name = "config_node_id")
    private ConfigNode configNode;
}
