package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.owasp.securityshepherd.model.Group;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group, Long> {

	@Query("select count(1) from groups where name = :name")
	public boolean existsByName(String name);

	@Modifying
	@Query("delete from Groups where name = :name")
	public void deleteByName(String name);

	@Query("select * from Groups where name = :name")
	public Optional<Group> findByName(String name);

}