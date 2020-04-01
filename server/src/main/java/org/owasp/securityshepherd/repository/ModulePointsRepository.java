package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.ModulePoints;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ModulePointsRepository extends ReactiveCrudRepository<ModulePoints, Integer> {
  @Query("SELECT * from module_points WHERE module_id = :module ORDER BY submission_rank")
  public Flux<ModulePoints> findAllByModuleId(@Param("module") final int moduleId);
}
