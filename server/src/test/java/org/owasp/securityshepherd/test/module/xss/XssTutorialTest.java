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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.DuplicateModuleShortNameException;
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
public class XssTutorialTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  XssTutorial xssTutorial;

  @Mock ModuleService moduleService;

  @Mock XssService xssService;

  @Mock FlagHandler flagHandler;

  @Test
  public void getDescription_IsNotEmpty() {
    assertThat(xssTutorial.getDescription()).isNotEmpty();
  }

  @Test
  public void getModuleId_ModuleIntialized_ReturnsModuleId() {
    final long mockModuleId = 254L;
    final Module mockModule = mock(Module.class);

    when(moduleService.create(xssTutorial)).thenReturn(Mono.just(mockModule));

    when(mockModule.getId()).thenReturn(mockModuleId);
    when(moduleService.setDynamicFlag(mockModuleId)).thenReturn(Mono.just(mockModule));
    xssTutorial.initialize().block();
    assertThat(xssTutorial.getModuleId()).isEqualTo(mockModuleId);
  }

  @Test
  public void getModuleId_ModuleNotIntialized_ThrowsModuleNotInitializedException() {
    assertThatThrownBy(() -> xssTutorial.getModuleId())
        .isInstanceOf(ModuleNotInitializedException.class)
        .hasMessageContaining("Module must be initialized first");
  }

  @Test
  public void getName_ReturnsXssTutorial() {
    assertThat(xssTutorial.getName()).isEqualTo("XSS Tutorial");
  }

  @Test
  public void getShortName_ReturnsXssTutorial() {
    assertThat(xssTutorial.getShortName()).isEqualTo("xss-tutorial");
  }

  @Test
  public void initialize_DuplicateModuleName_ReturnsException() {
    when(moduleService.create(xssTutorial))
        .thenReturn(Mono.error(new DuplicateModuleNameException()));
    StepVerifier.create(xssTutorial.initialize())
        .expectError(DuplicateModuleNameException.class)
        .verify();
  }

  @Test
  public void initialize_DuplicateModuleShortName_ReturnsException() {
    when(moduleService.create(xssTutorial))
        .thenReturn(Mono.error(new DuplicateModuleShortNameException()));
    StepVerifier.create(xssTutorial.initialize())
        .expectError(DuplicateModuleShortNameException.class)
        .verify();
  }

  @Test
  public void initialize_ValidModuleName_InitializesModule() {
    final long mockModuleId = 125L;

    final Module mockModule = mock(Module.class);

    when(moduleService.create(xssTutorial)).thenReturn(Mono.just(mockModule));

    when(mockModule.getId()).thenReturn(mockModuleId);
    when(moduleService.setDynamicFlag(mockModuleId)).thenReturn(Mono.just(mockModule));

    StepVerifier.create(xssTutorial.initialize())
        .expectNext(mockModuleId)
        .expectComplete()
        .verify();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    xssTutorial = new XssTutorial(xssService, moduleService, flagHandler);
  }

  @Test
  public void submitQuery_MakesAlert_ReturnsFlag() {
    final long mockUserId = 606L;
    final Module mockModule = mock(Module.class);
    final String mockFlag = "mockedflag";
    final String query = "username";
    final long mockModuleId = 823L;

    when(moduleService.create(xssTutorial)).thenReturn(Mono.just(mockModule));

    when(mockModule.getId()).thenReturn(mockModuleId);
    when(moduleService.setDynamicFlag(mockModuleId)).thenReturn(Mono.just(mockModule));
    when(flagHandler.getDynamicFlag(mockUserId, mockModuleId)).thenReturn(Mono.just(mockFlag));

    final String mockTarget =
        "<html><head><title>Alert</title></head><body><p>Result: username</p></body></html>";

    final List<String> mockAlertList = Arrays.asList(new String[] {"xss", "alert"});

    when(xssService.doXss(mockTarget)).thenReturn(mockAlertList);

    xssTutorial.initialize().block();

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
  public void submitQuery_ModuleNotIntialized_ReturnsModuleNotInitializedException() {
    final long mockUserId = 419L;
    final String query = "username";
    StepVerifier.create(xssTutorial.submitQuery(mockUserId, query))
        .expectError(ModuleNotInitializedException.class);
  }

  @Test
  public void submitQuery_NoAlert_ReturnsQuery() {
    final long mockUserId = 606L;
    final Module mockModule = mock(Module.class);
    final String query = "username";
    final long mockModuleId = 823L;

    when(moduleService.create(xssTutorial)).thenReturn(Mono.just(mockModule));

    when(mockModule.getId()).thenReturn(mockModuleId);
    when(moduleService.setDynamicFlag(mockModuleId)).thenReturn(Mono.just(mockModule));

    final String mockTarget =
        "<html><head><title>Alert</title></head><body><p>Result: username</p></body></html>";

    final List<String> mockAlertList = new ArrayList<String>();

    when(xssService.doXss(mockTarget)).thenReturn(mockAlertList);

    xssTutorial.initialize().block();

    StepVerifier.create(xssTutorial.submitQuery(mockUserId, query))
        .assertNext(
            response -> {
              assertThat(response.getResult()).contains("Sorry");
              assertThat(response.getResult()).doesNotContain("Congratulations");
              assertThat(response.getAlert()).isEqualTo(null);
            })
        .expectComplete()
        .verify();
  }
}
