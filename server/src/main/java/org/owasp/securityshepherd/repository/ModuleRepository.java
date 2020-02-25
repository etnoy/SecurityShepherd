package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.owasp.securityshepherd.persistence.model.Module;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends ReactiveCrudRepository<Module, Integer> {

	@Query("select count(1) from module where name = :name")
	public boolean existsByName(@Param("name") final String name);

	@Modifying
	@Query("delete from module where name = :name")
	public void deleteByName(@Param("name") final String name);

	@Query("select * from module where name = :name")
	public Optional<Module> findByName(@Param("name") final String name);

}