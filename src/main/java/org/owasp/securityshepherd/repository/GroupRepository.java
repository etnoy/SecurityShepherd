package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.owasp.securityshepherd.model.Group;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group, String> {

	@Query("select case when count(g)> 0 then true else false end from Group g where g.name = :name")
	public boolean existsByName(String name);

	@Query("delete from Groups where name = :name")
	public void deleteByName(String name);

	@Query("select * from Groups where name = :name")
	public Optional<Group> findByName(String name);

}