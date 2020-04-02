package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.Correction;
import org.owasp.securityshepherd.model.Score;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CorrectionRepository extends ReactiveCrudRepository<Correction, Integer> {
  @Query("SELECT * from correction WHERE user_id = :user_id")
  public Flux<Score> findAllByUserId(@Param("user_id") final int userId);
}
