package org.owasp.securityshepherd.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleIdNotFoundException;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.ModuleScore;
import org.owasp.securityshepherd.model.ModuleScore.ModuleScoreBuilder;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.ModuleScoreRepository;
import org.springframework.stereotype.Service;

import com.google.common.primitives.Bytes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
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
      return submissions.collectMap(submission -> submission.get("userId"), submission -> {
        return baseScore + scoreMap.getOrDefault(submission.get("rank"), 0);
      });
    });

  }

}
