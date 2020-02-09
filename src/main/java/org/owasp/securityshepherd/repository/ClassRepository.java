package org.owasp.securityshepherd.repository;

import java.util.Optional;

import org.owasp.securityshepherd.model.ClassEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends CrudRepository<ClassEntity, Long> {

	@Query("select count(1) from class where name = :name")
	public boolean existsByName(String name);

	@Modifying
	@Query("delete from class where name = :name")
	public void deleteByName(String name);

	@Query("select * from class where name = :name")
	public Optional<ClassEntity> findByName(String name);

}