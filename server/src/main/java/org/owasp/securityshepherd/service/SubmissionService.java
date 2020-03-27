package org.owasp.securityshepherd.service;

import java.sql.Timestamp;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateClassNameException;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.model.Auth;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.Auth.AuthBuilder;
import org.owasp.securityshepherd.model.PasswordAuth.PasswordAuthBuilder;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.model.Submission.SubmissionBuilder;
import org.owasp.securityshepherd.model.User.UserBuilder;
import org.owasp.securityshepherd.repository.AuthRepository;
import org.owasp.securityshepherd.repository.PasswordAuthRepository;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.repository.UserRepository;
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

  private final ClassService classService;

  private final ModuleService moduleService;

  private final SubmissionRepository submissionRepository;

  public Mono<Boolean> submit(final int userId, final int moduleId, final String flag) {

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
    submissionBuilder.time(new Timestamp(System.currentTimeMillis()));

    Mono<Boolean> isValid = moduleService.verifyFlag(userId, moduleId, flag);

    isValid.map(valid -> submissionBuilder.isValid(valid)).map(SubmissionBuilder::build)
        .flatMap(submissionRepository::save);

    return isValid;

  }

}
