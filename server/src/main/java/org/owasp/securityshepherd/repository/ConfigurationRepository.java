package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.owasp.securityshepherd.persistence.model.Configuration;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends ReactiveCrudRepository<Configuration, Integer> {
	@Query("select count(1) from configuration where config_key = :key")
	public boolean existsByKey(@Param("key") final String key);

	@Modifying
	@Query("delete from configuration where config_key = :key")
	public void deleteByKey(@Param("key") final String key);

	@Query("select * from configuration where config_key = :key")
	public Optional<Configuration> findByKey(@Param("key") final String key);
}