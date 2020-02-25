package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.persistence.model.PasswordAuth;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordAuthRepository extends ReactiveCrudRepository<PasswordAuth, Integer> {

}