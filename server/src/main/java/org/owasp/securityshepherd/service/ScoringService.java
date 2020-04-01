package org.owasp.securityshepherd.service;

import java.util.Map;
import org.owasp.securityshepherd.model.ModulePoint;
import org.owasp.securityshepherd.model.ModuleScore;
import org.owasp.securityshepherd.model.ModulePoint.ModulePointBuilder;
import org.owasp.securityshepherd.model.ModuleScore.ModuleScoreBuilder;
import org.owasp.securityshepherd.repository.ModulePointRepository;
import org.owasp.securityshepherd.repository.ModuleScoreRepository;
import org.owasp.securityshepherd.repository.SubmissionDatabaseClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public final class ScoringService {

  private final ModulePointRepository modulePointRepository;

  private final SubmissionDatabaseClient submissionDatabaseClient;
  
  private final ModuleScoreRepository moduleScoreRepository;
  
  public Mono<Void> deleteAll() {
    return modulePointRepository.deleteAll();
  }

  public Mono<ModulePoint> setModuleScore(final int moduleId, final int rank, final int score) {
    ModulePointBuilder builder = ModulePoint.builder().moduleId(moduleId).rank(rank).points(score);

    return modulePointRepository.save(builder.build());
  }
  
  public Flux<ModuleScore> computeScoreForModule(final int moduleId) {
    // Get the scoring rules for this module
    final Mono<Map<Integer, Integer>> moduleRankPointMap = modulePointRepository
        .findAllByModuleId(moduleId).collectMap(ModulePoint::getRank, ModulePoint::getPoints);

    ModuleScoreBuilder moduleScoreBuilder = ModuleScore.builder().moduleId(moduleId);

    return moduleRankPointMap.flatMapMany(pointMap -> {
      // The base score for this module is the 0th entry in the list
      final int baseScore = pointMap.get(0);
      // Get all valid submissions related to this module, ranked by submission time
      return submissionDatabaseClient.findAllValidByModuleIdSortedBySubmissionTime(moduleId)
          .map(submission -> {
            moduleScoreBuilder.userId(submission.get("userId"));
            moduleScoreBuilder.rank(submission.get("rank"));
            moduleScoreBuilder.score(baseScore + pointMap.getOrDefault(submission.get("rank"), 0));
            return moduleScoreBuilder.build();
          }).flatMap(moduleScoreRepository::save);
    });
  }
}
