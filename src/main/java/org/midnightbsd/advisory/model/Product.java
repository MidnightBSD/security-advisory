package org.midnightbsd.advisory.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Lucas Holt
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Product {
    @Id
    @SequenceGenerator(name = "product_id_seq",
            sequenceName = "product_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "product_id_seq")
    @Column(name = "id", updatable = false)
    private int id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "version", nullable = false, length = 100)
    private String version;


    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;
}
