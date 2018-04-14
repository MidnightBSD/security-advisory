package org.midnightbsd.advisory.repository;

import org.midnightbsd.advisory.model.ConfigNodeCpe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Lucas Holt
 */
@Repository
public interface ConfigNodeCpeRepository extends JpaRepository<ConfigNodeCpe, Integer> {
}
