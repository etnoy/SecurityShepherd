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
package org.owasp.securityshepherd.it.module.xss;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.xss.XssService;
import org.owasp.securityshepherd.module.xss.XssTutorial;
import org.owasp.securityshepherd.module.xss.XssTutorialResponse;
import org.owasp.securityshepherd.scoring.ScoreService;
import org.owasp.securityshepherd.scoring.Submission;
import org.owasp.securityshepherd.scoring.SubmissionService;
import org.owasp.securityshepherd.test.util.TestUtils;
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
@DisplayName("XssTutorial integration test")
class XssTutorialIT {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  XssTutorial xssTutorial;

  @Autowired TestUtils testUtils;

  @Autowired UserService userService;

  @Autowired ModuleService moduleService;

  @Autowired SubmissionService submissionService;

  @Autowired ScoreService scoreService;

  @Autowired XssService xssService;

  @Autowired FlagHandler flagHandler;

  @BeforeEach
  private void setUp() {
    testUtils.deleteAll().block();
    xssTutorial = new XssTutorial(xssService, moduleService, flagHandler);
    xssTutorial.getInit().block();
  }

  @AfterEach
  private void clear() {
    xssTutorial = null;
  }

  private String extractFlagFromResponse(final XssTutorialResponse response) {
    assertThat(response.getResult()).startsWith("Congratulations, flag is");
    return response.getResult().replaceAll("Congratulations, flag is ", "");
  }

  @Test
  void submitQuery_XssQuery_ShowsAlert() {
    final Long userId = userService.create("TestUser1").block();

    final Mono<String> flagMono =
        xssTutorial
            .submitQuery(userId, "<script>alert('xss')</script>")
            .map(this::extractFlagFromResponse);

    // Submit the flag we got from the sql injection and make sure it validates
    StepVerifier.create(
            flagMono
                .flatMap(
                    flag -> submissionService.submit(userId, xssTutorial.getModuleName(), flag))
                .map(Submission::isValid))
        .expectNext(true)
        .expectComplete()
        .verify();
  }

  @Test
  void submitQuery_CorrectAttackQuery_ModifiedFlagIsWrong() {
    final Long userId = userService.create("TestUser1").block();

    final Mono<String> flagMono =
        xssTutorial
            .submitQuery(userId, "<script>alert('xss')</script>")
            .map(this::extractFlagFromResponse);

    // Take the flag we got from the tutorial, modify it, and expect validation to fail
    StepVerifier.create(
            flagMono
                .flatMap(flag -> submissionService.submit(userId, "xss-tutorial", flag + "wrong"))
                .map(Submission::isValid))
        .expectNext(false)
        .expectComplete()
        .verify();
  }

  @Test
  void submitQuery_QueryWithoutXss_NoResults() {
    final Long userId = userService.create("TestUser1").block();

    StepVerifier.create(xssTutorial.submitQuery(userId, "test"))
        .assertNext(
            response -> {
              assertThat(response.getResult()).startsWith("Sorry");
            })
        .expectComplete()
        .verify();
  }
}
