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
package org.owasp.securityshepherd.test.module.xss;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.exception.ModuleNotInitializedException;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.xss.XssService;
import org.owasp.securityshepherd.module.xss.XssTutorial;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("XssTutorial unit test")
class XssTutorialTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  XssTutorial xssTutorial;

  @Mock ModuleService moduleService;

  @Mock XssService xssService;

  @Mock FlagHandler flagHandler;

  final Module mockModule = mock(Module.class);

  @Test
  void equals_EqualsVerifier_AsExpected() {
    EqualsVerifier.forClass(XssTutorial.class).withRedefinedSuperclass().verify();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    when(moduleService.create("xss-tutorial")).thenReturn(Mono.just(mockModule));

    xssTutorial = new XssTutorial(xssService, moduleService, flagHandler);
  }

  @Test
  void submitQuery_MakesAlert_ReturnsFlag() {
    final long mockUserId = 606L;
    final String mockFlag = "mockedflag";
    final String query = "username";
    final String mockModuleId = "id";

    when(mockModule.getId()).thenReturn(mockModuleId);
    when(moduleService.setDynamicFlag(mockModuleId)).thenReturn(Mono.just(mockModule));
    when(flagHandler.getDynamicFlag(mockUserId, mockModuleId)).thenReturn(Mono.just(mockFlag));
    when(mockModule.isFlagStatic()).thenReturn(false);

    final String mockTarget =
        "<html><head><title>Alert</title></head><body><p>Result: username</p></body></html>";

    final List<String> mockAlertList = Arrays.asList(new String[] {"xss", "alert"});

    when(xssService.doXss(mockTarget)).thenReturn(mockAlertList);

    StepVerifier.create(xssTutorial.submitQuery(mockUserId, query))
        .assertNext(
            response -> {
              assertThat(response.getResult()).contains(mockFlag);
              assertThat(response.getAlert()).isEqualTo(mockAlertList.get(0));
            })
        .expectComplete()
        .verify();
  }

  @Test
  void submitQuery_ModuleNotIntialized_ReturnsModuleNotInitializedException() {
    final long mockUserId = 419L;
    final String query = "username";
    StepVerifier.create(xssTutorial.submitQuery(mockUserId, query))
        .expectError(ModuleNotInitializedException.class);
  }

  @Test
  void submitQuery_NoAlert_ReturnsQuery() {
    final long mockUserId = 606L;
    final Module mockModule = mock(Module.class);
    final String query = "username";
    final String mockModuleId = "id";

    when(mockModule.getId()).thenReturn(mockModuleId);
    when(moduleService.setDynamicFlag(mockModuleId)).thenReturn(Mono.just(mockModule));

    final String mockTarget =
        "<html><head><title>Alert</title></head><body><p>Result: username</p></body></html>";

    final List<String> mockAlertList = new ArrayList<String>();

    when(xssService.doXss(mockTarget)).thenReturn(mockAlertList);

    StepVerifier.create(xssTutorial.submitQuery(mockUserId, query))
        .assertNext(
            response -> {
              assertThat(response.getResult()).contains("Sorry");
              assertThat(response.getResult()).doesNotContain("Congratulations");
              assertThat(response.getAlert()).isNull();
            })
        .expectComplete()
        .verify();
  }
}
