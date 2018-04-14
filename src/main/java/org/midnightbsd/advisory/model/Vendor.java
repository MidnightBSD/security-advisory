package org.midnightbsd.advisory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Lucas Holt
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vendor")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Vendor implements Serializable {

    @JsonIgnore
    private static final long serialVersionUID = 4504423113963008931L;

    @Id
    @SequenceGenerator(name = "vendor_id_seq",
            sequenceName = "vendor_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "vendor_id_seq")
    @Column(name = "id", updatable = false)
    private int id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "vendor")
    private List<Product> products;

}