package org.owasp.securityshepherd.proxy;

import java.util.Optional;

import org.owasp.securityshepherd.model.User;

public interface UserRepositoryProxy extends RepositoryProxy<User> {

	boolean existsByDisplayName(String displayName);

	boolean existsByLoginName(String loginName);

	Optional<User> findByLoginName(String loginName);

}