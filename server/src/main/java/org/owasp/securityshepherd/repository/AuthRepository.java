package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.persistence.model.Auth;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends ReactiveCrudRepository<Auth, Integer> {

}