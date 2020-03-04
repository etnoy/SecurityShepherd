package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.persistence.model.ClassEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface ClassRepository extends ReactiveCrudRepository<ClassEntity, Integer> {

  @Modifying
  @Query("delete from class where name = :name")
  public void deleteByName(@Param("name") final String name);

  @Query("select * from class where name = :name")
  public Mono<ClassEntity> findByName(@Param("name") final String name);

}
