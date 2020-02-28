package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.persistence.model.PasswordAuth;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface PasswordAuthRepository extends ReactiveCrudRepository<PasswordAuth, Integer> {

	@Query("SELECT * from password_auth WHERE user = :user")
	public Mono<PasswordAuth> findByUserId(@Param("user") final int user);
	
	@Query("SELECT * from password_auth WHERE login_name = :login_name")
	public Mono<PasswordAuth> findByLoginName(@Param("login_name") final String loginName);

}