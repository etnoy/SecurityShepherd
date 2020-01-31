package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.owasp.securityshepherd.model.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	@Query("select count(1) from users where name = :name")
	public boolean existsByName(String name);

	@Modifying
	@Query("delete from users where name = :name")
	public void deleteByName(String name);

	@Query("select * from users where name = :name")
	public Optional<User> findByName(String name);

}