package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.Auth;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface AuthRepository extends ReactiveCrudRepository<Auth, Integer> {

  @Query("SELECT * from auth WHERE user = :user")
  public Mono<Auth> findByUserId(@Param("user") final int user);

}
