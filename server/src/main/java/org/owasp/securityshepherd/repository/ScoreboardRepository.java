package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.Scoreboard;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ScoreboardRepository extends ReactiveCrudRepository<Scoreboard, Long> {
  @Query("SELECT * from scoreboard WHERE user_id = :user_id")
  public Flux<Scoreboard> findAllByUserId(@Param("user_id") final long userId);
}
