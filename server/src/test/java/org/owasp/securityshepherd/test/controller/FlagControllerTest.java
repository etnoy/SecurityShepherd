/**
 * This file is part of Security Shepherd.
 *
 * <p>Security Shepherd is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with Security
 * Shepherd. If not, see <http://www.gnu.org/licenses/>.
 */
package org.owasp.securityshepherd.test.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.time.Month;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.authentication.ControllerAuthentication;
import org.owasp.securityshepherd.exception.NotAuthenticatedException;
import org.owasp.securityshepherd.module.FlagController;
import org.owasp.securityshepherd.scoring.Submission;
import org.owasp.securityshepherd.scoring.SubmissionService;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("FlagController unit test")
class FlagControllerTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private FlagController flagController;

  @Mock private ControllerAuthentication controllerAuthentication;

  @Mock private SubmissionService submissionService;

  @BeforeEach
  private void setUp() throws Exception {
    // Set up the system under test
    flagController = new FlagController(controllerAuthentication, submissionService);
  }

  @Test
  void submitFlag_UserNotAuthenticated_ReturnsException() throws Exception {
    final long mockModuleId = 16L;
    final String flag = "validflag";

    when(controllerAuthentication.getUserId())
        .thenReturn(Mono.error(new NotAuthenticatedException()));

    StepVerifier.create(flagController.submitFlag(mockModuleId, flag))
        .expectError(NotAuthenticatedException.class)
        .verify();

    verify(controllerAuthentication, times(1)).getUserId();
  }

  @Test
  void submitFlag_UserAuthenticatedAndValidFlagSubmitted_ReturnsValidSubmission()
      throws Exception {
    final long mockUserId = 417L;
    final long mockModuleId = 16L;
    final String flag = "validflag";

    when(controllerAuthentication.getUserId()).thenReturn(Mono.just(mockUserId));

    final Submission submission =
        Submission.builder()
            .userId(mockUserId)
            .moduleId(mockModuleId)
            .flag(flag)
            .isValid(true)
            .time(LocalDateTime.of(2000, Month.JULY, 1, 2, 3, 4))
            .build();

    when(submissionService.submit(mockUserId, mockModuleId, flag))
        .thenReturn(Mono.just(submission));

    StepVerifier.create(flagController.submitFlag(mockModuleId, flag))
        .expectNext(submission)
        .expectComplete()
        .verify();

    verify(submissionService, times(1)).submit(mockUserId, mockModuleId, flag);
  }
}
