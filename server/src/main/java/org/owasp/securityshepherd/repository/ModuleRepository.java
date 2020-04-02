package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.Module;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface ModuleRepository extends ReactiveCrudRepository<Module, Long> {
  @Modifying
  @Query("delete from module where name = :name")
  public void deleteByName(@Param("name") final String name);

  @Query("select count(1) from module where name = :name")
  public Mono<Boolean> existsByName(@Param("name") final String name);

  @Query("select * from module where name = :name")
  public Mono<Module> findByName(@Param("name") final String name);
}
