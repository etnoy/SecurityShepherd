package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.Scoreboard;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreboardRepository extends ReactiveCrudRepository<Scoreboard, Long> {
}
