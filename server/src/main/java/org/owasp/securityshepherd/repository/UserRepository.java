package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Integer> {
  @Query("SELECT * from user WHERE display_name = :display_name")
  public Mono<User> findByDisplayName(@Param("display_name") final String displayName);
}
