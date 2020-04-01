package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.ModuleScore;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ModuleScoreRepository extends ReactiveCrudRepository<ModuleScore, Integer> {
  @Query("SELECT * from module_score WHERE module_id = :module ORDER BY submission_rank ")
  public Flux<ModuleScore> findAllByModuleId(@Param("module") final int moduleId);
}
