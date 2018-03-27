package org.midnightbsd.advisory.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Lucas Holt
 */
@Entity
@Table(name = "package_fixed")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class PackageFixed implements Serializable {

    @Id
    @SequenceGenerator(name = "package_fixed_id_seq",
            sequenceName = "package_fixed_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "package_fixed_id_seq")
    @Column(name = "id", updatable = false)
    private int id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="version", nullable = false)
    private String version;
}