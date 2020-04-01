package org.owasp.securityshepherd.service;

import java.util.Map;
import org.owasp.securityshepherd.model.ModulePoints;
import org.owasp.securityshepherd.model.ModulePoints.ModulePointsBuilder;
import org.owasp.securityshepherd.repository.ModulePointsRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public final class ScoringService {

  private final SubmissionService submissionService;

  private final ModulePointsRepository moduleScoreRepository;

  public Mono<Map<Integer, Integer>> computeScoreForModule(final int moduleId) {
    // Get the scoring rules for this module
    final Mono<Map<Integer, Integer>> moduleRankPointsMap = moduleScoreRepository
        .findAllByModuleId(moduleId).collectMap(ModulePoints::getRank, ModulePoints::getPoints);

    return moduleRankPointsMap.flatMap(scoreMap -> {
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

  public Mono<ModulePoints> setModuleScore(final int moduleId, final int rank, final int score) {
    ModulePointsBuilder builder = ModulePoints.builder().moduleId(moduleId).rank(rank).points(score);

    return moduleScoreRepository.save(builder.build());
  }
}
