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
package org.owasp.securityshepherd.test.module.csrf;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.authentication.ControllerAuthentication;
import org.owasp.securityshepherd.module.csrf.CsrfTutorial;
import org.owasp.securityshepherd.module.csrf.CsrfTutorialController;
import org.owasp.securityshepherd.module.csrf.CsrfTutorialResult;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("CsrfTutorialController unit test")
class CsrfTutorialControllerTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  CsrfTutorialController csrfTutorialController;

  @Mock CsrfTutorial csrfTutorial;

  @Mock ControllerAuthentication controllerAuthentication;

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    csrfTutorialController = new CsrfTutorialController(csrfTutorial, controllerAuthentication);
  }

  @Test
  void tutorial_TutorialCreated_ReturnsTutorial() {
    final Long mockUserId = 85L;

    when(controllerAuthentication.getUserId()).thenReturn(Mono.just(mockUserId));

    final CsrfTutorialResult mockCsrfTutorialResult = mock(CsrfTutorialResult.class);

    when(csrfTutorial.getTutorial(mockUserId)).thenReturn(Mono.just(mockCsrfTutorialResult));

    StepVerifier.create(csrfTutorialController.tutorial())
        .expectNext(mockCsrfTutorialResult)
        .expectComplete()
        .verify();
  }

  @Test
  void activate_TutorialCreated_ReturnsTutorial() {
    final Long mockUserId = 85L;
    final String mockPseudonym = "abcd123";

    when(controllerAuthentication.getUserId()).thenReturn(Mono.just(mockUserId));

    final CsrfTutorialResult mockCsrfTutorialResult = mock(CsrfTutorialResult.class);

    when(csrfTutorial.attack(mockUserId, mockPseudonym))
        .thenReturn(Mono.just(mockCsrfTutorialResult));

    StepVerifier.create(csrfTutorialController.attack(mockPseudonym))
        .expectNext(mockCsrfTutorialResult)
        .expectComplete()
        .verify();
  }
}
