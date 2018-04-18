package org.midnightbsd.advisory.repository;

import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Lucas Holt
 */
public interface VendorRepository extends JpaRepository<Vendor, Integer> {
    Vendor findOneByName(@Param("name") String name);
}
