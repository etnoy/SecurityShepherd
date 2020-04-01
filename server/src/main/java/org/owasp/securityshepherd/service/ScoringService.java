package org.owasp.securityshepherd.service;

import java.util.Map;
import org.owasp.securityshepherd.model.ModulePoint;
import org.owasp.securityshepherd.model.ModulePoint.ModulePointBuilder;
import org.owasp.securityshepherd.repository.ModulePointRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public final class ScoringService {

  private final SubmissionService submissionService;

  private final ModulePointRepository moduleScoreRepository;

  public Mono<Map<Integer, Integer>> computeScoreForModule(final int moduleId) {
    // Get the scoring rules for this module
    final Mono<Map<Integer, Integer>> moduleRankPointMap = moduleScoreRepository
        .findAllByModuleId(moduleId).collectMap(ModulePoint::getRank, ModulePoint::getPoints);

    return moduleRankPointMap.flatMap(scoreMap -> {
      // The base score for this module is the 0th entry in the list
      final int baseScore = scoreMap.get(0);
      // Get all valid submissions related to this module, ranked by submission time
      return submissionService.findAllValidByModuleIdSortedBySubmissionTime(moduleId)
          // Organize these submissions into a map
          .collectMap(
              // with userid as key
              submission -> submission.get("userId"),
              // and scoring as value. Score is the base score plus any bonus points
              submission -> baseScore + scoreMap.getOrDefault(submission.get("rank"), 0));
    });
  }
  
  public Mono<Void> deleteAll() {
    return moduleScoreRepository.deleteAll();
  }

  public Mono<ModulePoint> setModuleScore(final int moduleId, final int rank, final int score) {
    ModulePointBuilder builder = ModulePoint.builder().moduleId(moduleId).rank(rank).points(score);

    return moduleScoreRepository.save(builder.build());
  }
}
