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

import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.authentication.ControllerAuthentication;
import org.owasp.securityshepherd.exception.NotAuthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.ReactorContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListener;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("ControllerAuthentication unit test")
public class ControllerAuthenticationTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private ControllerAuthentication controllerAuthentication;

  @Mock private Authentication authentication;

  private TestExecutionListener reactorContextTestExecutionListener =
      new ReactorContextTestExecutionListener();

  @BeforeEach
  private void authenticate() throws Exception {
    // Set up the system under test
    controllerAuthentication = new ControllerAuthentication();
    TestSecurityContextHolder.setAuthentication(authentication);
    reactorContextTestExecutionListener.beforeTestMethod(null);
  }

  @Test
  public void getUserId_UserAuthenticated_ReturnsUserId() throws Exception {
    final long mockUserId = 633L;
    when(authentication.getPrincipal()).thenReturn(mockUserId);
    StepVerifier.create(controllerAuthentication.getUserId())
        .expectNext(mockUserId)
        .expectComplete()
        .verify();
  }

  @Test
  public void getUserId_UserNotAuthenticated_ReturnsNotAuthenticatedException() throws Exception {
    StepVerifier.create(controllerAuthentication.getUserId())
        .expectError(NotAuthenticatedException.class)
        .verify();
  }
}
