package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.Configuration;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface ConfigurationRepository extends ReactiveCrudRepository<Configuration, Long> {
  @Modifying
  @Query("delete from configuration where config_key = :key")
  public void deleteByKey(@Param("key") final String key);

  @Query("select count(1) from configuration where config_key = :key")
  public Mono<Boolean> existsByKey(@Param("key") final String key);

  @Query("select * from configuration where config_key = :key")
  public Mono<Configuration> findByKey(@Param("key") final String key);
}
