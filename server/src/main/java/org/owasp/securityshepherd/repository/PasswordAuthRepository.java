package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.UserAuth;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface PasswordAuthRepository extends ReactiveCrudRepository<PasswordAuth, Long> {
  @Query("SELECT * from password_auth WHERE login_name = :login_name")
  public Mono<PasswordAuth> findByLoginName(@Param("login_name") final String loginName);

  @Query("SELECT * from password_auth WHERE user_id = :user_id")
  public Mono<PasswordAuth> findByUserId(@Param("user_id") final long userId);

  @Modifying
  @Query("delete from password_auth WHERE user_id = :user_id")
  public Mono<UserAuth> deleteByUserId(@Param("user_id") final long userId);
}
