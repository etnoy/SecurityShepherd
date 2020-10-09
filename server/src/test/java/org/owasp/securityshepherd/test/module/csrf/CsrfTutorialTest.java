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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.csrf.CsrfService;
import org.owasp.securityshepherd.module.csrf.CsrfTutorial;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("CsrfTutorial unit test")
class CsrfTutorialTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private static final String MODULE_NAME = "csrf-tutorial";

  CsrfTutorial csrfTutorial;

  @Mock ModuleService moduleService;

  @Mock CsrfService csrfService;

  @Mock FlagHandler flagHandler;

  final Module mockModule = mock(Module.class);

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    reset(mockModule);
    when(moduleService.create(MODULE_NAME)).thenReturn(Mono.just(mockModule));

    csrfTutorial = new CsrfTutorial(csrfService, moduleService, flagHandler);
  }

  @Test
  void equals_EqualsVerifier_AsExpected() {

    class CsrfTutorialChild extends CsrfTutorial {

      public CsrfTutorialChild(
          CsrfService csrfService, ModuleService moduleService, FlagHandler flagHandler) {
        super(csrfService, moduleService, flagHandler);
      }

      @Override
      public boolean canEqual(Object o) {
        return false;
      }
    }

    EqualsVerifier.forClass(CsrfTutorial.class)
        .withRedefinedSuperclass()
        .withRedefinedSubclass(CsrfTutorialChild.class)
        .withIgnoredAnnotations(NonNull.class)
        .verify();
  }

  @Test
  void attack_InvalidTarget_ReturnsError() {
    final long attackerUserId = 90L;

    final String mockTarget = "abcd123";

    when(csrfService.validatePseudonym(mockTarget, MODULE_NAME)).thenReturn(Mono.just(false));
    when(mockModule.isFlagStatic()).thenReturn(false);
    when(mockModule.getName()).thenReturn(MODULE_NAME);

    StepVerifier.create(csrfTutorial.attack(attackerUserId, mockTarget))
        .assertNext(
            result -> {
              assertThat(result.getPseudonym()).isNull();
              assertThat(result.getFlag()).isNull();
              assertThat(result.getError()).isNotNull();
              assertThat(result.getMessage()).isNull();
            })
        .expectComplete()
        .verify();
  }

  @Test
  void attack_ValidTarget_Activates() {
    final long attackerUserId = 90L;

    final String mockTarget = "abcd123";
    final String mockAttacker = "xyz789";

    when(csrfService.getPseudonym(attackerUserId, MODULE_NAME)).thenReturn(Mono.just(mockAttacker));

    when(csrfService.validatePseudonym(mockTarget, MODULE_NAME)).thenReturn(Mono.just(true));
    when(mockModule.isFlagStatic()).thenReturn(false);
    when(mockModule.getName()).thenReturn(MODULE_NAME);
    when(csrfService.attack(mockTarget, MODULE_NAME)).thenReturn(Mono.empty());

    StepVerifier.create(csrfTutorial.attack(attackerUserId, mockTarget))
        .assertNext(
            result -> {
              assertThat(result.getPseudonym()).isNull();
              assertThat(result.getFlag()).isNull();
              assertThat(result.getError()).isNull();
              assertThat(result.getMessage()).isNotNull();
            })
        .expectComplete()
        .verify();
  }

  @Test
  void attack_TargetsSelf_DoesNotActivate() {
    final long userId = 90L;

    final String pseudonym = "xyz789";

    when(csrfService.getPseudonym(userId, MODULE_NAME)).thenReturn(Mono.just(pseudonym));

    when(csrfService.validatePseudonym(pseudonym, MODULE_NAME)).thenReturn(Mono.just(true));
    when(mockModule.isFlagStatic()).thenReturn(false);
    when(mockModule.getName()).thenReturn(MODULE_NAME);

    StepVerifier.create(csrfTutorial.attack(userId, pseudonym))
        .assertNext(
            result -> {
              assertThat(result.getPseudonym()).isNull();
              assertThat(result.getFlag()).isNull();
              assertThat(result.getError()).isNotNull();
              assertThat(result.getMessage()).isNull();
            })
        .expectComplete()
        .verify();
  }

  @Test
  void getTutorial_Activated_ReturnsFlag() {
    final long mockUserId = 45L;
    final String mockPseudonym = "abcd123";
    final String flag = "flag";

    when(csrfService.getPseudonym(mockUserId, MODULE_NAME)).thenReturn(Mono.just(mockPseudonym));

    when(mockModule.isFlagStatic()).thenReturn(false);
    when(mockModule.getName()).thenReturn(MODULE_NAME);

    when(flagHandler.getDynamicFlag(mockUserId, MODULE_NAME)).thenReturn(Mono.just(flag));
    when(csrfService.validate(mockPseudonym, MODULE_NAME)).thenReturn(Mono.just(true));

    StepVerifier.create(csrfTutorial.getTutorial(mockUserId))
        .assertNext(
            result -> {
              assertThat(result.getPseudonym()).isEqualTo(mockPseudonym);
              assertThat(result.getFlag()).isEqualTo(flag);
            })
        .expectComplete()
        .verify();
  }

  @Test
  void getTutorial_NotActivated_ReturnsActivationLink() {
    final long mockUserId = 45L;
    final String mockPseudonym = "abcd123";

    when(csrfService.getPseudonym(mockUserId, MODULE_NAME)).thenReturn(Mono.just(mockPseudonym));

    when(mockModule.isFlagStatic()).thenReturn(false);

    when(csrfService.validate(mockPseudonym, MODULE_NAME)).thenReturn(Mono.just(false));

    StepVerifier.create(csrfTutorial.getTutorial(mockUserId))
        .assertNext(
            result -> {
              assertThat(result.getPseudonym()).isEqualTo(mockPseudonym);
              assertThat(result.getFlag()).isNull();
            })
        .expectComplete()
        .verify();
  }
}
