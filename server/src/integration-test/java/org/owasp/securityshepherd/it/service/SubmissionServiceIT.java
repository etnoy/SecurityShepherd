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
package org.owasp.securityshepherd.it.service;

import java.time.Clock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.owasp.securityshepherd.exception.ModuleAlreadySolvedException;
import org.owasp.securityshepherd.module.FlagHandler;
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
class SubmissionServiceIT {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  @Autowired SubmissionService submissionService;

  @Autowired UserService userService;

  @Autowired ModuleService moduleService;

  @Autowired Clock clock;

  @Autowired ModuleRepository moduleRepository;

  @Autowired SubmissionRepository submissionRepository;

  @Autowired CorrectionRepository correctionRepository;

  @Autowired UserRepository userRepository;

  @Autowired TestUtils testService;

  @Autowired FlagHandler flagComponent;

  @BeforeEach
  private void clear() {
    testService.deleteAll().block();
  }

  @Test
  void submitFlag_DuplicateValidStaticFlag_ReturnModuleAlreadySolvedException() {
    final String flag = "thisisaflag";
    final String moduleName = "test-module";

    final Mono<Long> userIdMono = userService.create("TestUser");

    moduleService.create(moduleName).block();
    moduleService.setStaticFlag(moduleName, flag).block();

    StepVerifier.create(
            userIdMono.flatMapMany(
                userId ->
                    submissionService
                        .submit(userId, moduleName, flag)
                        .repeat(2)
                        .map(Submission::isValid)))
        .expectNext(true)
        .expectError(ModuleAlreadySolvedException.class)
        .verify();
  }

  @Test
  void submitFlag_ValidStaticFlag_Success() {
    final String flag = "thisisaflag";
    final String moduleName = "test-module";

    final Mono<Long> userIdMono = userService.create("TestUser");

    moduleService.create(moduleName).block();

    moduleService.setStaticFlag(moduleName, flag).block();

    StepVerifier.create(
            userIdMono.flatMap(
                userId ->
                    submissionService.submit(userId, moduleName, flag).map(Submission::isValid)))
        .expectNext(true)
        .expectComplete()
        .verify();
  }
}
