/**
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

package org.owasp.securityshepherd.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleAlreadySolvedException;
import org.owasp.securityshepherd.model.Correction;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.model.Correction.CorrectionBuilder;
import org.owasp.securityshepherd.model.Submission.SubmissionBuilder;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.repository.CorrectionRepository;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public final class SubmissionService {

  private final ModuleService moduleService;

  private final SubmissionRepository submissionRepository;

  private final CorrectionRepository correctionRepository;

  private final Clock clock;

  public Mono<Submission> submit(final Long userId, final Long moduleId, final String flag) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }

    SubmissionBuilder submissionBuilder = Submission.builder();

    submissionBuilder.userId(userId);
    submissionBuilder.moduleId(moduleId);
    submissionBuilder.flag(flag);
    submissionBuilder.time(LocalDateTime.now(clock));

    return
    // Check if flag is correct
    moduleService.verifyFlag(userId, moduleId, flag)
        // Get isValid field
        .map(submissionBuilder::isValid)
        // Has this module been solved by this user? In that case, throw exception.
        .filterWhen(u -> validSubmissionDoesNotExistByUserIdAndModuleId(userId, moduleId))
        .switchIfEmpty(Mono.error(new ModuleAlreadySolvedException(
            String.format("User %d has already finished module %d", userId, moduleId))))
        // Otherwise, build a submission and save it in db
        .map(SubmissionBuilder::build).flatMap(submissionRepository::save);
  }

  public Mono<Submission> submitValid(final Long userId, final Long moduleId) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }

    SubmissionBuilder submissionBuilder = Submission.builder();

    submissionBuilder.userId(userId);
    submissionBuilder.moduleId(moduleId);
    submissionBuilder.isValid(true);
    submissionBuilder.time(LocalDateTime.now(clock));

    return Mono.just(submissionBuilder)
        .filterWhen(u -> validSubmissionDoesNotExistByUserIdAndModuleId(userId, moduleId))
        .switchIfEmpty(Mono.error(new ModuleAlreadySolvedException(
            String.format("User %d has already finished module %d", userId, moduleId))))
        // Otherwise, build a submission and save it in db
        .map(SubmissionBuilder::build).flatMap(submissionRepository::save);
  }

  public Flux<Submission> findAllValidByUserIdAndModuleId(final long userId, final long moduleId) {
    return submissionRepository.findAllValidByUserIdAndModuleId(userId, moduleId);
  }

  public Mono<List<Long>> findAllSolvedModulesByUserId(final long userId) {
    return submissionRepository.findAllValidByUserId(userId).map(Submission::getModuleId)
        .collectList();
  }

  private Mono<Boolean> validSubmissionDoesNotExistByUserIdAndModuleId(final long userId,
      final long moduleId) {
    return submissionRepository.findAllValidByUserIdAndModuleId(userId, moduleId).next()
        .map(u -> false).defaultIfEmpty(true);
  }

  public Mono<Correction> submitCorrection(final Long userId, final long amount,
      final String description) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    final CorrectionBuilder correctionBuilder = Correction.builder();

    correctionBuilder.userId(userId);
    correctionBuilder.amount(amount);
    correctionBuilder.description(description);
    correctionBuilder.time(LocalDateTime.now(clock));

    return correctionRepository.save(correctionBuilder.build());
  }

  public Flux<Submission> findAllByModuleId(final long moduleId) {
    if (moduleId <= 0) {
      return Flux.error(new InvalidModuleIdException());
    }

    return submissionRepository.findAllByModuleId(moduleId);
  }


  public Flux<Submission> findAllValidByUserId(final long userId) {
    if (userId <= 0) {
      return Flux.error(new InvalidUserIdException());
    }

    return submissionRepository.findAllValidByUserId(userId);
  }
}
