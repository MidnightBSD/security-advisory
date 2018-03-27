package org.midnightbsd.advisory.model.search;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Lucas Holt
 */
@ToString
@EqualsAndHashCode
public class Instance implements Serializable, Comparable<Instance> {
    private static final long serialVersionUID = -6753971942731865621L;

    @Getter
    @Setter
    private String vendor;

    @Getter
    @Setter
    private String product;

    @Getter
    @Setter
    private String version;

    @Override
    public int compareTo(final Instance o) {
        return this.vendor.compareTo(o.getVendor()) +
                this.product.compareTo(o.getProduct()) +
                this.version.compareTo(o.getVersion());
    }
}
