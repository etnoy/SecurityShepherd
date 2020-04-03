package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.ModulePoint;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ModulePointRepository extends ReactiveCrudRepository<ModulePoint, Long> {
  @Query("SELECT * from module_point WHERE module_id = :module")
  public Flux<ModulePoint> findAllByModuleId(@Param("module") final int moduleId);
}