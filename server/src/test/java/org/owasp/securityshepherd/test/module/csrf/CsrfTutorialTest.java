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
}
