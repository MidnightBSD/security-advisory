package org.midnightbsd.advisory.repository;

import org.midnightbsd.advisory.model.PackageFixed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Lucas Holt
 */
public interface PackageFixedRepository extends JpaRepository<PackageFixed, Integer> {
    List<PackageFixed> findAllByOrderByVersionAsc();

    PackageFixed findOneByName(@Param("name") String name);

    PackageFixed findByNameAndVersion(@Param("name") String name, @Param("version") String version);
}
