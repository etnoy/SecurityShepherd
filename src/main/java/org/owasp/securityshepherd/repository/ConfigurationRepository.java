package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.owasp.securityshepherd.model.Configuration;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends CrudRepository<Configuration, Integer> {
	@Query("select count(1) from configuration where config_key = :key")
	public boolean existsByKey(@Param("key") final String key);

	@Modifying
	@Query("delete from configuration where config_key = :key")
	public void deleteByKey(@Param("key") final String key);

	@Query("select * from configuration where config_key = :key")
	public Optional<Configuration> findByKey(@Param("key") final String key);
}