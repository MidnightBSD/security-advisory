package org.midnightbsd.advisory.repository;

import org.midnightbsd.advisory.model.Advisory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Lucas Holt
 */
@Repository
public interface AdvisoryRepository extends JpaRepository<Advisory, Integer> {
    Advisory findOneByName(@Param("name") String name);

    Advisory findOneByCveId(@Param("cveId") String cveId);
}
