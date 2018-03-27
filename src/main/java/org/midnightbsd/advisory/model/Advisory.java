package org.midnightbsd.advisory.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * @author Lucas Holt
 */
@Entity
@Table(name = "advisory")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Advisory {
    @Id
    @SequenceGenerator(name = "advisory_id_seq",
            sequenceName = "advisory_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "advisory_id_seq")
    @Column(name = "id", updatable = false)
    private int id;

    @Column(name = "cve_id", nullable = false)
    private String cveId;

    @Column(name = "description", columnDefinition = "TEXT", length = 65616)
    private String description;

    @Column(name = "published_date")
    @Temporal(value = TemporalType.DATE)
    private Date publishedDate;

    @Column(name = "last_modified_date")
    @Temporal(value = TemporalType.DATE)
    private Date lastModifiedDate;

    @Column(name = "severity", nullable = false)
    private String severity;

    @Column(name="problem_type", length = 100)
    private String problemType;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "advisory_package_fixed_map", joinColumns = @JoinColumn(name = "advisory_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "package_fixed_id", referencedColumnName = "id"))
    private Set<PackageFixed> fixedPackages;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "advisory_product_map", joinColumns = @JoinColumn(name = "advisory_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    private Set<Product> products;

}