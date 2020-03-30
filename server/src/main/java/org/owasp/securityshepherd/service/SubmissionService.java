package org.owasp.securityshepherd.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.model.Submission.SubmissionBuilder;
import org.owasp.securityshepherd.repository.SubmissionDatabaseClient;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public final class SubmissionService {

  private final UserService userService;

  private final ModuleService moduleService;

  private final SubmissionRepository submissionRepository;

  private final Clock clock;

  private final SubmissionDatabaseClient submissionDatabaseClient;
  
  public Mono<Void> deleteAll() {
    return submissionRepository.deleteAll();
  }

  public Flux<Map<String, Integer>> findAllValidByModuleIdSortedBySubmissionTime(
      final int moduleId) {

    return submissionDatabaseClient.findAllValidByModuleIdSortedBySubmissionTime(moduleId);

  }

  public Mono<Submission> submit(final int userId, final int moduleId, final String flag) {

    if (userId <= 0) {

      return Mono.error(new InvalidUserIdException());

    }

    if (moduleId <= 0) {

      return Mono.error(new InvalidModuleIdException());

    }

    Mono.zip(userService.findDisplayNameById(userId), moduleService.findNameById(moduleId),
        (userDisplayName, moduleName) -> "User " + userDisplayName + " submitted to module "
            + moduleName + " with flag " + flag)
        .doOnSuccess(log::debug).subscribe();

    SubmissionBuilder submissionBuilder = Submission.builder();

    submissionBuilder.userId(userId);
    submissionBuilder.moduleId(moduleId);
    submissionBuilder.flag(flag);
    submissionBuilder.time(LocalDateTime.now(clock));

    return moduleService.verifyFlag(userId, moduleId, flag).map(submissionBuilder::isValid)
        .map(SubmissionBuilder::build).flatMap(submissionRepository::save);

  }

  public Flux<Submission> findAllByModuleId(final int moduleId) {

    if (moduleId <= 0) {

      return Flux.error(new InvalidModuleIdException());

    }

    return submissionRepository.findAllByModuleId(moduleId);

  }

}
