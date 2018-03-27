package org.midnightbsd.advisory.repository;

import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.PackageFixed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Lucas Holt
 */
@Repository
public interface PackageFixedRepository extends JpaRepository<PackageFixed, Integer> {
    List<PackageFixed> findAllByOrderByVersionAsc();

    PackageFixed findOneByName(@Param("name") String name);

    PackageFixed findByNameAndVersion(@Param("name") String name, @Param("version") String version);
}
