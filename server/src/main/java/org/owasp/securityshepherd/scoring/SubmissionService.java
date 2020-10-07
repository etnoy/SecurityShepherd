/*
 * This file is part of Security Shepherd.
 * 
 * Security Shepherd is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Security Shepherd.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.owasp.securityshepherd.scoring;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleAlreadySolvedException;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.scoring.Submission.SubmissionBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public final class SubmissionService {

  private final SubmissionRepository submissionRepository;

  private final RankedSubmissionRepository rankedSubmissionRepository;

  private final FlagHandler flagHandler;

  private Clock clock;

  public SubmissionService(
      SubmissionRepository submissionRepository,
      RankedSubmissionRepository rankedSubmissionRepository,
      FlagHandler flagHandler) {
    this.submissionRepository = submissionRepository;
    this.rankedSubmissionRepository = rankedSubmissionRepository;
    this.flagHandler = flagHandler;
    resetClock();
  }

  public Flux<Submission> findAllByModuleName(final String moduleName) {
    return submissionRepository.findAllByModuleName(moduleName);
  }

  public Flux<Submission> findAllValidByUserId(final long userId) {
    if (userId <= 0) {
      return Flux.error(new InvalidUserIdException());
    }
    return submissionRepository.findAllValidByUserId(userId);
  }

  public Flux<RankedSubmission> findAllRankedByUserId(final long userId) {
    if (userId <= 0) {
      return Flux.error(new InvalidUserIdException());
    }
    return rankedSubmissionRepository.findAllByUserId(userId);
  }

  public Mono<Submission> findAllValidByUserIdAndModuleName(
      final long userId, final String moduleName) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }
    return submissionRepository.findAllValidByUserIdAndModuleName(userId, moduleName);
  }

  public Mono<List<String>> findAllValidModuleNamesByUserId(final long userId) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }
    return submissionRepository
        .findAllValidByUserId(userId)
        .map(Submission::getModuleName)
        .collectList();
  }

  public void resetClock() {
    this.clock = Clock.systemDefaultZone();
  }

  public void setClock(Clock clock) {
    this.clock = clock;
  }

  public Mono<Submission> submit(final Long userId, final String moduleName, final String flag) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }
    SubmissionBuilder submissionBuilder = Submission.builder();
    submissionBuilder.userId(userId);
    submissionBuilder.moduleName(moduleName);
    submissionBuilder.flag(flag);
    submissionBuilder.time(LocalDateTime.now(clock));
    return
    // Check if flag is correct
    flagHandler
        .verifyFlag(userId, moduleName, flag)
        // Get isValid field
        .map(submissionBuilder::isValid)
        // Has this module been solved by this user? In that case, throw exception.
        .filterWhen(u -> validSubmissionDoesNotExistByUserIdAndModuleName(userId, moduleName))
        .switchIfEmpty(
            Mono.error(
                new ModuleAlreadySolvedException(
                    String.format("User %d has already finished module %s", userId, moduleName))))
        // Otherwise, build a submission and save it in db
        .map(SubmissionBuilder::build)
        .flatMap(submissionRepository::save);
  }

  public Mono<Submission> submitValid(final Long userId, final String moduleName) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    SubmissionBuilder submissionBuilder = Submission.builder();

    submissionBuilder.userId(userId);
    submissionBuilder.moduleName(moduleName);
    submissionBuilder.isValid(true);
    submissionBuilder.time(LocalDateTime.now(clock));

    return Mono.just(submissionBuilder)
        .filterWhen(u -> validSubmissionDoesNotExistByUserIdAndModuleName(userId, moduleName))
        .switchIfEmpty(
            Mono.error(
                new ModuleAlreadySolvedException(
                    String.format("User %d has already finished module %s", userId, moduleName))))
        // Otherwise, build a submission and save it in db
        .map(SubmissionBuilder::build)
        .flatMap(submissionRepository::save);
  }

  private Mono<Boolean> validSubmissionDoesNotExistByUserIdAndModuleName(
      final long userId, final String moduleName) {
    return submissionRepository
        .findAllValidByUserIdAndModuleName(userId, moduleName)
        .map(u -> false)
        .defaultIfEmpty(true);
  }
}
