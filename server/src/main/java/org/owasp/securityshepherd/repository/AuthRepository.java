package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.UserAuth;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface AuthRepository extends ReactiveCrudRepository<UserAuth, Integer> {
  @Query("SELECT * from auth WHERE user_id = :user_id")
  public Mono<UserAuth> findByUserId(@Param("user_id") final int userId);
}
