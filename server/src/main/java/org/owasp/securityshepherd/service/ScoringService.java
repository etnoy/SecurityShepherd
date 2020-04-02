package org.owasp.securityshepherd.service;

import java.util.Map;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.ModulePoint;
import org.owasp.securityshepherd.model.Score;
import org.owasp.securityshepherd.model.ModulePoint.ModulePointBuilder;
import org.owasp.securityshepherd.model.Score.ScoreBuilder;
import org.owasp.securityshepherd.model.Scoreboard;
import org.owasp.securityshepherd.repository.ModulePointRepository;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.ScoreRepository;
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

  private final ScoreRepository scoreRepository;
  
  private final ModuleRepository moduleRepository;

  public Mono<Void> deleteAll() {
    return modulePointRepository.deleteAll();
  }

  public Mono<ModulePoint> setModuleScore(final int moduleId, final int rank, final int score) {
    ModulePointBuilder builder = ModulePoint.builder().moduleId(moduleId).rank(rank).points(score);
    return modulePointRepository.save(builder.build());
  }

  public Flux<Score> computeScoresFromSubmissions() {
    final Flux<Integer> moduleIds = moduleRepository.findAll().map(Module::getId);

    // Get the scoring rules for this module
    final Flux<Map<Integer, Integer>> moduleRankPointMap =
        moduleIds.flatMap(moduleId -> modulePointRepository.findAllByModuleId(moduleId)
            .collectMap(ModulePoint::getRank, ModulePoint::getPoints));

    return Flux.zip(moduleIds, moduleRankPointMap).flatMap(tuple -> {
      // The base score for this module is the 0th entry in the list
      final int baseScore = tuple.getT2().get(0);
      // Get all valid submissions related to this module, ranked by submission time

      final Flux<Score> scores =
          submissionDatabaseClient.findAllValidByModuleIdSortedBySubmissionTime(tuple.getT1())
              // And then extract a score for each users's submission
              .map(rankedSubmissionDto -> {
                // Create a score entity
                final ScoreBuilder builder = Score.builder();
                // ModuleId is extracted from the tuple
                builder.moduleId(tuple.getT1());
                // UserId, rank and time are simply mapped from the Dto
                builder.userId(rankedSubmissionDto.getUserId());
                builder.rank(rankedSubmissionDto.getRank());
                builder.time(rankedSubmissionDto.getTime());
                // Score is calculated as base score + bonus for early completion
                builder.amount(
                    baseScore + tuple.getT2().getOrDefault(rankedSubmissionDto.getRank(), 0));
                // Finish building the database row entity and return it
                return builder.build();
              });

      // Save the scores in the database and return
      return scoreRepository.saveAll(scores);
    });
  }
  
  public Flux<Scoreboard> getScoreboard() {
    return submissionDatabaseClient.getScoreboard();
  }
}
