package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.owasp.securityshepherd.model.Module;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends CrudRepository<Module, Long> {

	@Query("select count(1) from module where name = :name")
	public boolean existsByName(String name);

	@Modifying
	@Query("delete from module where name = :name")
	public void deleteByName(String name);

	@Query("select * from module where name = :name")
	public Optional<Module> findByName(String name);

}