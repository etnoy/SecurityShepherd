package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.owasp.securityshepherd.model.Module;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ModuleRepository extends CrudRepository<Module, Long> {

	@Query("select count(1) from modules where name = :name")
	public boolean existsByName(String name);

	@Modifying
	@Query("delete from modules where name = :name")
	public void deleteByName(String name);

	@Query("select * from modules where name = :name")
	public Optional<Module> findByName(String name);

}