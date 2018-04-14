package org.midnightbsd.advisory.repository;

import org.midnightbsd.advisory.model.ConfigNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Lucas Holt
 */
@Repository
public interface ConfigNodeRepository extends JpaRepository<ConfigNode, Integer> {
}
