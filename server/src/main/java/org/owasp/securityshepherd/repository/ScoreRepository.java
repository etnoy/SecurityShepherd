package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.Score;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ScoreRepository extends ReactiveCrudRepository<Score, Integer> {
  @Query("SELECT * from score WHERE module_id = :module")
  public Flux<Score> findAllByModuleId(@Param("module") final int moduleId);
}
