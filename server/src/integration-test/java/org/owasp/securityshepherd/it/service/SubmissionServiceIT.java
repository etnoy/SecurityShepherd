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
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleRepository;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.scoring.CorrectionRepository;
import org.owasp.securityshepherd.scoring.Submission;
import org.owasp.securityshepherd.scoring.SubmissionRepository;
import org.owasp.securityshepherd.scoring.SubmissionService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.owasp.securityshepherd.user.UserRepository;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"application.runner.enabled=false"})
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("SubmissionService integration test")
public class SubmissionServiceIT {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  @Autowired
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

  @Autowired
  FlagHandler flagComponent;

  @Test
  public void submitFlag_ValidExactFlag_Success() {
    final String flag = "thisisaflag";

    final Mono<Long> userIdMono = userService.create("TestUser");

    final Mono<Long> moduleIdMono =
        moduleService.create("Test Module", "short-name").map(Module::getId)
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

    final Mono<Long> moduleIdMono =
        moduleService.create("Test Module", "short-name").map(Module::getId)
            .flatMap(moduleId -> moduleService.setExactFlag(moduleId, flag)).map(Module::getId);

    StepVerifier
        .create(Mono.zip(userIdMono, moduleIdMono)
            .flatMapMany(tuple -> submissionService.submit(tuple.getT1(), tuple.getT2(), flag)
                .repeat(2).map(Submission::isValid)))
        .expectNext(true).expectError(ModuleAlreadySolvedException.class).verify();
  }

  @BeforeEach
  private void clear() {
    // Initialize services with the real clock
    testService.deleteAll().block();
  }
}
