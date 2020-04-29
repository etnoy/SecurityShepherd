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

package org.owasp.securityshepherd.test.module.sql;

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
import org.owasp.securityshepherd.exception.NotAuthenticatedException;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleController;
import org.owasp.securityshepherd.module.ModuleListItem;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.ModuleSolutions;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorial;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorialController;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorialRow;
import org.owasp.securityshepherd.security.ControllerAuthentication;
import org.owasp.securityshepherd.service.SubmissionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModuleController unit test")
public class SqlInjectionTutorialControllerTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private SqlInjectionTutorialController sqlInjectionTutorialController;

  @Mock
  private ControllerAuthentication controllerAuthentication;

  @Mock
  private SqlInjectionTutorial sqlInjectionTutorial;

  @Test
  public void search_Autenticated_CallsModule() {
    final long mockUserId = 709L;

    final SqlInjectionTutorialRow sqlInjectionTutorialRow1 = mock(SqlInjectionTutorialRow.class);
    final SqlInjectionTutorialRow sqlInjectionTutorialRow2 = mock(SqlInjectionTutorialRow.class);
    final SqlInjectionTutorialRow sqlInjectionTutorialRow3 = mock(SqlInjectionTutorialRow.class);

    final String query = "sql";
    when(controllerAuthentication.getUserId()).thenReturn(Mono.just(mockUserId));

    when(sqlInjectionTutorial.submitQuery(mockUserId, query)).thenReturn(
        Flux.just(sqlInjectionTutorialRow1, sqlInjectionTutorialRow2, sqlInjectionTutorialRow3));

    StepVerifier.create(sqlInjectionTutorialController.search(query))
        .expectNext(sqlInjectionTutorialRow1).expectNext(sqlInjectionTutorialRow2)
        .expectNext(sqlInjectionTutorialRow3).expectComplete().verify();
    verify(controllerAuthentication, times(1)).getUserId();
    verify(sqlInjectionTutorial, times(1)).submitQuery(mockUserId, "sql");
  }

  @Test
  public void search_NotAutenticated_CallsModule() {
    final String query = "sql";
    when(controllerAuthentication.getUserId())
        .thenReturn(Mono.error(new NotAuthenticatedException()));
    StepVerifier.create(sqlInjectionTutorialController.search(query))
        .expectError(NotAuthenticatedException.class).verify();
    verify(controllerAuthentication, times(1)).getUserId();
  }

  @BeforeEach
  private void setUp() throws Exception {
    // Set up the system under test
    sqlInjectionTutorialController =
        new SqlInjectionTutorialController(sqlInjectionTutorial, controllerAuthentication);
  }

}
