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
package org.owasp.securityshepherd.test.module.xss;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.authentication.ControllerAuthentication;
import org.owasp.securityshepherd.exception.NotAuthenticatedException;
import org.owasp.securityshepherd.module.xss.XssTutorial;
import org.owasp.securityshepherd.module.xss.XssTutorialController;
import org.owasp.securityshepherd.module.xss.XssTutorialResponse;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("XssTutorialController unit test")
class XssTutorialControllerTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private XssTutorialController xssTutorialController;

  @Mock private ControllerAuthentication controllerAuthentication;

  @Mock private XssTutorial xssTutorial;

  @Test
  void search_Autenticated_CallsModule() {
    final long mockUserId = 527L;

    final XssTutorialResponse xssTutorialRow = mock(XssTutorialResponse.class);

    final String query = "xss";
    when(controllerAuthentication.getUserId()).thenReturn(Mono.just(mockUserId));

    when(xssTutorial.submitQuery(mockUserId, query)).thenReturn(Mono.just(xssTutorialRow));

    StepVerifier.create(xssTutorialController.search(query))
        .expectNext(xssTutorialRow)
        .expectComplete()
        .verify();
    verify(controllerAuthentication, times(1)).getUserId();
    verify(xssTutorial, times(1)).submitQuery(mockUserId, query);
  }

  @Test
  void search_NotAutenticated_CallsModule() {
    final String query = "xss";
    when(controllerAuthentication.getUserId())
        .thenReturn(Mono.error(new NotAuthenticatedException()));
    StepVerifier.create(xssTutorialController.search(query))
        .expectError(NotAuthenticatedException.class)
        .verify();
    verify(controllerAuthentication, times(1)).getUserId();
  }

  @BeforeEach
  private void setUp() throws Exception {
    // Set up the system under test
    xssTutorialController = new XssTutorialController(xssTutorial, controllerAuthentication);
  }
}
