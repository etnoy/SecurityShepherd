package org.owasp.securityshepherd.service;

import org.owasp.securityshepherd.model.ModulePoint;
import org.owasp.securityshepherd.model.ModulePoint.ModulePointBuilder;
import org.owasp.securityshepherd.model.Scoreboard;
import org.owasp.securityshepherd.repository.CorrectionRepository;
import org.owasp.securityshepherd.repository.ModulePointRepository;
import org.owasp.securityshepherd.repository.ScoreboardRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public final class ScoreService {

  private final ModulePointRepository modulePointRepository;

  private final CorrectionRepository correctionRepository;

  private final ScoreboardRepository scoreboardRepository;
  
  public Mono<Void> deleteAll() {
    return correctionRepository.deleteAll().then(modulePointRepository.deleteAll());
  }

  public Mono<ModulePoint> setModuleScore(final int moduleId, final int rank, final int score) {
    ModulePointBuilder builder = ModulePoint.builder().moduleId(moduleId).rank(rank).points(score);
    return modulePointRepository.save(builder.build());
  }

  public Flux<Scoreboard> getScoreboard() {
    return scoreboardRepository.findAll();
  }
}
