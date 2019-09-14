package org.midnightbsd.advisory.repository;

import org.midnightbsd.advisory.model.Advisory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Lucas Holt
 */
public interface AdvisoryRepository extends JpaRepository<Advisory, Integer> {

    Advisory findOneByCveId(@Param("cveId") String cveId);

    List<Advisory> findByPublishedDateBetween(Date startDate, Date endDate);

    List<Advisory> findByLastModifiedDateBetween(Date startDate, Date endDate);

    @Query(
            value = "SELECT distinct a FROM Advisory a JOIN a.products p JOIN p.vendor v WHERE p.name = :productName ORDER BY a.cveId")
    List<Advisory> findByProductName(@Param("productName") String productName);

    @Query(value = "SELECT distinct a FROM Advisory a INNER JOIN a.products p INNER JOIN p.vendor v WHERE v.name = :vendorName ORDER BY a.cveId")
    List<Advisory> findByVendorName(@Param("vendorName") String vendorName);

    @Query(value = "SELECT distinct a FROM Advisory a JOIN a.products p JOIN p.vendor v WHERE v.name = :vendorName and p.name like :productName ORDER BY a.cveId")
    List<Advisory> findByVendorNameAndProductsIsLike(@Param("vendorName") String vendorName, @Param("productName") String productName);
}
