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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateClassNameException;
import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorial;
import org.owasp.securityshepherd.repository.PasswordAuthRepository;
import org.owasp.securityshepherd.service.ClassService;
import org.owasp.securityshepherd.service.KeyService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.owasp.securityshepherd.user.PasswordAuth;
import org.owasp.securityshepherd.user.User;
import org.owasp.securityshepherd.user.UserAuth;
import org.owasp.securityshepherd.user.UserAuthRepository;
import org.owasp.securityshepherd.user.UserRepository;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("SqlInjectionTutorial unit test")
public class SqlInjectionTutorialTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  SqlInjectionTutorial sqlInjectionTutorial;

  @Mock
  ModuleService moduleService;

  @Mock
  FlagHandler flagHandler;

  @Test
  public void initialize_DuplicateModuleName_ReturnsException() {
    when(moduleService.create("Sql Injection Tutorial", SqlInjectionTutorial.SHORT_NAME,
        "Tutorial for making sql injections"))
            .thenReturn(Mono.error(new DuplicateModuleNameException()));

    StepVerifier.create(sqlInjectionTutorial.initialize())
        .expectError(DuplicateModuleNameException.class).verify();
  }

  @Test
  public void initialize_MockedServices_InitializesModule() {
    final long mockModuleId = 572L;

    final Module mockModule = mock(Module.class);

    when(moduleService.create("Sql Injection Tutorial", SqlInjectionTutorial.SHORT_NAME,
        "Tutorial for making sql injections")).thenReturn(Mono.just(mockModule));

    when(mockModule.getId()).thenReturn(mockModuleId);
    when(moduleService.setDynamicFlag(mockModuleId)).thenReturn(Mono.just(mockModule));

    StepVerifier.create(sqlInjectionTutorial.initialize()).expectNext(mockModuleId).expectComplete()
        .verify();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    sqlInjectionTutorial = new SqlInjectionTutorial(moduleService, flagHandler);
  }
}
