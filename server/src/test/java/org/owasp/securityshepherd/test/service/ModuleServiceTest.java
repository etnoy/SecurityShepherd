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
package org.owasp.securityshepherd.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.crypto.KeyService;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleRepository;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.test.util.TestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModuleService unit test")
class ModuleServiceTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private ModuleService moduleService;

  @Mock private ModuleRepository moduleRepository;

  @Mock private KeyService keyService;

  @Test
  void count_NoArgument_ReturnsCount() {
    final long mockedModuleCount = 75L;

    when(moduleRepository.count()).thenReturn(Mono.just(mockedModuleCount));

    StepVerifier.create(moduleService.count())
        .expectNext(mockedModuleCount)
        .expectComplete()
        .verify();
    verify(moduleRepository, times(1)).count();
  }

  @Test
  void create_ValidModuleId_Succeeds() {
    final String moduleId = "test-module";

    final byte[] randomBytes = {120, 56, 111};
    when(keyService.generateRandomBytes(16)).thenReturn(randomBytes);

    when(moduleRepository.save(any(Module.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, Module.class)));

    StepVerifier.create(moduleService.create(moduleId)).expectComplete().verify();

    ArgumentCaptor<Module> argument = ArgumentCaptor.forClass(Module.class);

    verify(moduleRepository, times(1)).save(argument.capture());
    verify(moduleRepository, times(1)).save(any(Module.class));
    assertThat(argument.getValue().getId()).isEqualTo(moduleId);
  }

  @Test
  void findAll_ModulesExist_ReturnsModules() {
    final Module mockModule1 = mock(Module.class);
    final Module mockModule2 = mock(Module.class);
    final Module mockModule3 = mock(Module.class);

    when(moduleRepository.findAll()).thenReturn(Flux.just(mockModule1, mockModule2, mockModule3));

    StepVerifier.create(moduleService.findAll())
        .expectNext(mockModule1)
        .expectNext(mockModule2)
        .expectNext(mockModule3)
        .expectComplete()
        .verify();

    verify(moduleRepository, times(1)).findAll();
  }

  @Test
  void findAll_NoModulesExist_ReturnsEmpty() {
    when(moduleRepository.findAll()).thenReturn(Flux.empty());
    StepVerifier.create(moduleService.findAll()).expectComplete().verify();
    verify(moduleRepository, times(1)).findAll();
  }

  @Test
  void findAllOpen_NoModulesExist_ReturnsEmpty() {
    when(moduleRepository.findAllOpen()).thenReturn(Flux.empty());
    StepVerifier.create(moduleService.findAllOpen()).expectComplete().verify();
    verify(moduleRepository, times(1)).findAllOpen();
  }

  @Test
  void findAllOpen_OpenModulesExist_ReturnsOpenModules() {
    final Module mockModule1 = mock(Module.class);
    final Module mockModule2 = mock(Module.class);
    final Module mockModule3 = mock(Module.class);

    when(moduleRepository.findAllOpen())
        .thenReturn(Flux.just(mockModule1, mockModule2, mockModule3));

    StepVerifier.create(moduleService.findAllOpen())
        .expectNext(mockModule1)
        .expectNext(mockModule2)
        .expectNext(mockModule3)
        .expectComplete()
        .verify();

    verify(moduleRepository, times(1)).findAllOpen();
  }

  @Test
  void findById_InvalidModuleId_ReturnsInvalidModuleIdException() {
    for (final String moduleId : TestUtils.INVALID_ID_STRINGS) {
      StepVerifier.create(moduleService.findById(moduleId))
          .expectError(InvalidModuleIdException.class)
          .verify();
    }
  }

  @Test
  void findById_ModuleIdExists_ReturnsInvalidModuleIdException() {
    final Module mockModule = mock(Module.class);
    final String mockModuleId = "id";

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));
    StepVerifier.create(moduleService.findById(mockModuleId))
        .expectNext(mockModule)
        .expectComplete()
        .verify();
    verify(moduleRepository, times(1)).findById(mockModuleId);
  }

  @Test
  void findById_NonExistentModuleId_ReturnsEmpty() {
    final String mockModuleId = "id";
    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.empty());
    StepVerifier.create(moduleService.findById(mockModuleId)).expectComplete().verify();
    verify(moduleRepository, times(1)).findById(mockModuleId);
  }

  @Test
  void setDynamicFlag_FlagPreviouslySet_ReturnPreviousFlag() {
    final byte[] newFlag = {-118, 17, 4, -35, 17, -3, -94, 0, -72, -17, 65, -127, 12, 82, 9, 29};

    final Module mockModuleWithStaticFlag = mock(Module.class);
    final Module mockModuleWithDynamicFlag = mock(Module.class);

    final String mockModuleId = "id";

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModuleWithStaticFlag));

    when(mockModuleWithStaticFlag.withFlagStatic(false)).thenReturn(mockModuleWithDynamicFlag);

    when(mockModuleWithDynamicFlag.getKey()).thenReturn(newFlag);

    when(moduleRepository.save(mockModuleWithDynamicFlag))
        .thenReturn(Mono.just(mockModuleWithDynamicFlag));

    StepVerifier.create(moduleService.setDynamicFlag(mockModuleId))
        .assertNext(
            module -> {
              assertThat(module.getKey()).isEqualTo(newFlag);
            })
        .expectComplete()
        .verify();

    verify(moduleRepository, times(1)).save(any(Module.class));
    verify(keyService, never()).generateRandomString(any(Integer.class));

    ArgumentCaptor<Module> argument = ArgumentCaptor.forClass(Module.class);
    verify(moduleRepository, times(1)).save(argument.capture());
    assertThat(argument.getValue().getKey()).isEqualTo(newFlag);
  }

  @Test
  void setDynamicFlag_StaticFlagIsSet_SetsDynamicFlag() {
    final Module mockModuleWithStaticFlag = mock(Module.class);
    final Module mockModuleWithDynamicFlag = mock(Module.class);

    final String mockModuleId = "id";

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModuleWithStaticFlag));

    when(mockModuleWithStaticFlag.withFlagStatic(false)).thenReturn(mockModuleWithDynamicFlag);

    when(mockModuleWithDynamicFlag.isFlagStatic()).thenReturn(false);

    when(moduleRepository.save(mockModuleWithDynamicFlag))
        .thenReturn(Mono.just(mockModuleWithDynamicFlag));

    StepVerifier.create(moduleService.setDynamicFlag(mockModuleId))
        .assertNext(
            module -> {
              assertThat(module.isFlagStatic()).isFalse();
            })
        .expectComplete()
        .verify();

    verify(mockModuleWithStaticFlag, times(1)).withFlagStatic(false);
    verify(moduleRepository, times(1)).save(any(Module.class));
  }

  @Test
  void setStaticFlag_EmptyStaticFlag_ReturnsInvalidFlagException() {
    StepVerifier.create(moduleService.setStaticFlag("id", ""))
        .expectError(InvalidFlagException.class)
        .verify();
  }

  @Test
  void setStaticFlag_NullStaticFlag_ReturnsNulPointerException() {
    StepVerifier.create(moduleService.setStaticFlag("id", null))
        .expectErrorMatches(
            throwable ->
                throwable instanceof NullPointerException
                    && throwable.getMessage().equals("Flag cannot be null"))
        .verify();
  }

  @Test
  void setStaticFlag_ValidStaticFlag_SetsFlagToStatic() {
    final String staticFlag = "setStaticFlag_ValidStaticFlag_SetsFlagToStatic";

    final Module mockModule = mock(Module.class);
    final Module mockModuleWithStaticFlag = mock(Module.class);
    final Module mockModuleWithStaticFlagEnabled = mock(Module.class);

    final String mockModuleId = "id";

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));
    when(mockModule.withFlagStatic(true)).thenReturn(mockModuleWithStaticFlag);
    when(mockModuleWithStaticFlag.withStaticFlag(staticFlag))
        .thenReturn(mockModuleWithStaticFlagEnabled);

    when(mockModuleWithStaticFlagEnabled.isFlagStatic()).thenReturn(true);
    when(mockModuleWithStaticFlagEnabled.getStaticFlag()).thenReturn(staticFlag);

    when(moduleRepository.save(mockModuleWithStaticFlagEnabled))
        .thenReturn(Mono.just(mockModuleWithStaticFlagEnabled));

    StepVerifier.create(moduleService.setStaticFlag(mockModuleId, staticFlag))
        .assertNext(
            module -> {
              assertThat(module.isFlagStatic()).isTrue();
              assertThat(module.getStaticFlag()).isEqualTo(staticFlag);
            })
        .expectComplete()
        .verify();

    ArgumentCaptor<String> findArgument = ArgumentCaptor.forClass(String.class);
    verify(moduleRepository, times(1)).findById(findArgument.capture());
    assertThat(findArgument.getValue()).isEqualTo(mockModuleId);

    ArgumentCaptor<Module> saveArgument = ArgumentCaptor.forClass(Module.class);
    verify(moduleRepository, times(1)).save(saveArgument.capture());
    assertThat(saveArgument.getValue().getStaticFlag()).isEqualTo(staticFlag);
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    moduleService = new ModuleService(moduleRepository, keyService);
  }
}
