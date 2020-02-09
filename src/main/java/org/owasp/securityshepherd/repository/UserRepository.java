package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

}