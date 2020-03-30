package org.owasp.securityshepherd.service;

import java.util.Map;
import org.owasp.securityshepherd.model.ModuleScore;
import org.owasp.securityshepherd.model.ModuleScore.ModuleScoreBuilder;
import org.owasp.securityshepherd.repository.ModuleScoreRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public final class ScoringService {

  private final SubmissionService submissionService;

  private final ModuleScoreRepository moduleScoreRepository;

  public Mono<ModuleScore> setScore(final int moduleId, final int rank, final int score) {
    ModuleScoreBuilder builder = ModuleScore.builder().moduleId(moduleId).rank(rank).score(score);

    return moduleScoreRepository.save(builder.build());
  }

  public Mono<Map<Integer, Integer>> computeScoreForModule(final int moduleId) {
    final Mono<Map<Integer, Integer>> moduleRankScoreMap = moduleScoreRepository
        .findAllByModuleId(moduleId).collectMap(ModuleScore::getRank, ModuleScore::getScore);

    Flux<Map<String, Integer>> submissions =
        submissionService.findAllValidByModuleIdSortedBySubmissionTime(moduleId);

    return moduleRankScoreMap.flatMap(scoreMap -> {
      final int baseScore = scoreMap.get(0);
      return submissions.collectMap(submission -> submission.get("userId"),
          submission -> baseScore + scoreMap.getOrDefault(submission.get("rank"), 0));
    });
  }
}
