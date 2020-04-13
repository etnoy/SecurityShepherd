package org.owasp.securityshepherd.it.service;

import java.time.Clock;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.owasp.securityshepherd.exception.ModuleAlreadySolvedException;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.repository.CorrectionRepository;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.repository.UserRepository;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.SubmissionService;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("SubmissionService integration test")
public class SubmissionServiceIT {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  SubmissionService submissionService;

  @Autowired
  UserService userService;

  @Autowired
  ModuleService moduleService;

  @Autowired
  Clock clock;

  @Autowired
  ModuleRepository moduleRepository;

  @Autowired
  SubmissionRepository submissionRepository;

  @Autowired
  CorrectionRepository correctionRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  TestUtils testService;

  @Test
  public void submitFlag_ValidExactFlag_Success() {
    final String flag = "thisisaflag";

    final Mono<Long> userIdMono = userService.create("TestUser");

    final Mono<Long> moduleIdMono = moduleService.create("Test Module", "url").map(Module::getId)
        .flatMap(moduleId -> moduleService.setExactFlag(moduleId, flag)).map(Module::getId);

    StepVerifier
        .create(Mono.zip(userIdMono, moduleIdMono).flatMap(tuple -> submissionService
            .submit(tuple.getT1(), tuple.getT2(), flag).map(Submission::isValid)))
        .expectNext(true).expectComplete().verify();
  }

  @Test
  public void submitFlag_DuplicateValidExactFlag_ReturnModuleAlreadySolvedException() {
    final String flag = "thisisaflag";

    final Mono<Long> userIdMono = userService.create("TestUser");

    final Mono<Long> moduleIdMono = moduleService.create("Test Module", "url").map(Module::getId)
        .flatMap(moduleId -> moduleService.setExactFlag(moduleId, flag)).map(Module::getId);

    StepVerifier
        .create(Mono.zip(userIdMono, moduleIdMono)
            .flatMapMany(tuple -> submissionService.submit(tuple.getT1(), tuple.getT2(), flag)
                .repeat(2).map(Submission::isValid)))
        .expectNext(true).expectError(ModuleAlreadySolvedException.class).verify();
  }

  private void initializeService(Clock injectedClock) {
    submissionService = new SubmissionService(moduleService, submissionRepository,
        correctionRepository, injectedClock);
  }

  @BeforeEach
  private void clear() {
    // Initialize services with the real clock
    initializeService(clock);

    testService.deleteAll().block();
  }
}
