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
package org.owasp.securityshepherd.test.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.owasp.securityshepherd.crypto.KeyService;
import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.EmptyModuleNameException;
import org.owasp.securityshepherd.exception.EmptyModuleShortNameException;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleRepository;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.SubmittableModule;
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
  void create_DuplicateName_ReturnsDuplicateModuleNameException() {
    final String name = "TestModule";
    final String shortName = "test-module";

    final Module mockModule = mock(Module.class);

    when(moduleRepository.findByName(name)).thenReturn(Mono.just(mockModule));

    StepVerifier.create(moduleService.create(name, shortName))
        .expectErrorMatches(
            throwable ->
                throwable instanceof DuplicateModuleNameException
                    && throwable.getMessage().equals("Module name TestModule already exists"))
        .verify();

    verify(moduleRepository, times(1)).findByName(name);
  }

  @Test
  void create_EmptyName_ReturnsIllegalArgumentException() {
    StepVerifier.create(moduleService.create("", "shortName"))
        .expectErrorMatches(
            throwable ->
                throwable instanceof EmptyModuleNameException
                    && throwable.getMessage().equals("Module name cannot be empty"))
        .verify();
  }

  @Test
  void create_NullName_ReturnsNullPointerException() {
    StepVerifier.create(moduleService.create(null, "shortName"))
        .expectError(NullPointerException.class)
        .verify();
  }

  @Test
  void create_NameAndShortnameAndDescription_Succeeds() {
    final String name = "TestModule";
    final String shortName = "test-module";
    final String description = "This is a module";

    final long mockModuleId = 390;

    final byte[] randomBytes = {120, 56, 111};
    when(keyService.generateRandomBytes(16)).thenReturn(randomBytes);

    when(moduleRepository.save(any(Module.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, Module.class).withId(mockModuleId)));

    when(moduleRepository.findByName(name)).thenReturn(Mono.empty());

    StepVerifier.create(moduleService.create(name, shortName, description))
        .assertNext(
            module -> {
              assertThat(module.getName()).isEqualTo(name);
              assertThat(module.getShortName()).isEqualTo(shortName);
              assertThat(module.getDescription()).isEqualTo(description);
            })
        .expectComplete()
        .verify();

    ArgumentCaptor<Module> argument = ArgumentCaptor.forClass(Module.class);

    verify(moduleRepository, times(1)).findByName(name);
    verify(moduleRepository, times(1)).save(argument.capture());
    verify(moduleRepository, times(1)).save(any(Module.class));
    assertThat(argument.getValue().getName()).isEqualTo(name);
  }

  @Test
  void create_NameAndShortnameSucceeds() {
    final String name = "TestModule";
    final String shortName = "test-module";

    final long mockModuleId = 390;

    final byte[] randomBytes = {120, 56, 111};
    when(keyService.generateRandomBytes(16)).thenReturn(randomBytes);

    when(moduleRepository.save(any(Module.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, Module.class).withId(mockModuleId)));

    when(moduleRepository.findByName(name)).thenReturn(Mono.empty());

    StepVerifier.create(moduleService.create(name, shortName))
        .assertNext(
            module -> {
              assertThat(module.getName()).isEqualTo(name);
              assertThat(module.getShortName()).isEqualTo(shortName);
              assertThat(module.getDescription()).isNull();
            })
        .expectComplete()
        .verify();

    ArgumentCaptor<Module> argument = ArgumentCaptor.forClass(Module.class);

    verify(moduleRepository, times(1)).findByName(name);
    verify(moduleRepository, times(1)).save(argument.capture());
    verify(moduleRepository, times(1)).save(any(Module.class));
    assertThat(argument.getValue().getName()).isEqualTo(name);
  }

  @Test
  void create_ValidSubmittableModule_Succeeds() {
    final String name = "TestModule";
    final String shortName = "test-module";
    final String description = "description";

    final long mockModuleId = 390;

    final SubmittableModule mockSubmittableModule = mock(SubmittableModule.class);

    when(moduleRepository.save(any(Module.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, Module.class).withId(mockModuleId)));

    when(moduleRepository.findByName(name)).thenReturn(Mono.empty());

    when(mockSubmittableModule.getName()).thenReturn(name);
    when(mockSubmittableModule.getShortName()).thenReturn(shortName);
    when(mockSubmittableModule.getDescription()).thenReturn(description);

    final byte[] randomBytes = {120, 56, 111};
    when(keyService.generateRandomBytes(16)).thenReturn(randomBytes);

    StepVerifier.create(moduleService.create(mockSubmittableModule))
        .assertNext(
            module -> {
              assertThat(module.getName()).isEqualTo(name);
              assertThat(module.getShortName()).isEqualTo(shortName);
              assertThat(module.getDescription()).isEqualTo(description);
            })
        .expectComplete()
        .verify();

    ArgumentCaptor<Module> argument = ArgumentCaptor.forClass(Module.class);

    verify(moduleRepository, times(1)).findByName(name);
    verify(moduleRepository, times(1)).save(argument.capture());
    verify(moduleRepository, times(1)).save(any(Module.class));
    assertThat(argument.getValue().getName()).isEqualTo(name);
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
    for (final long moduleId : TestUtils.INVALID_IDS) {
      StepVerifier.create(moduleService.findById(moduleId))
          .expectError(InvalidModuleIdException.class)
          .verify();
    }
  }

  @Test
  void findById_ModuleIdExists_ReturnsInvalidModuleIdException() {
    final Module mockModule = mock(Module.class);
    final long mockModuleId = 750L;

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));
    StepVerifier.create(moduleService.findById(mockModuleId))
        .expectNext(mockModule)
        .expectComplete()
        .verify();
    verify(moduleRepository, times(1)).findById(mockModuleId);
  }

  @Test
  void findById_NonExistentModuleId_ReturnsEmpty() {
    final long mockModuleId = 286;
    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.empty());
    StepVerifier.create(moduleService.findById(mockModuleId)).expectComplete().verify();
    verify(moduleRepository, times(1)).findById(mockModuleId);
  }

  @Test
  void findByShortName_EmptyShortName_ReturnsInvalidShortNameException() {
    StepVerifier.create(moduleService.findByShortName(""))
        .expectErrorMatches(
            throwable ->
                throwable instanceof EmptyModuleShortNameException
                    && throwable.getMessage().equals("Module short name cannot be empty"))
        .verify();
  }

  @Test
  void findByShortName_ExistingShortName_ReturnsModule() {
    final String mockModuleShortName = "a-name";
    final Module mockModule = mock(Module.class);
    when(moduleRepository.findByShortName(mockModuleShortName)).thenReturn(Mono.just(mockModule));
    StepVerifier.create(moduleService.findByShortName(mockModuleShortName))
        .expectNext(mockModule)
        .expectComplete()
        .verify();
    verify(moduleRepository, times(1)).findByShortName(mockModuleShortName);
  }

  @Test
  void findByShortName_NonExistentShortName_ReturnsEmpty() {
    final String mockModuleShortName = "a-name";
    when(moduleRepository.findByShortName(mockModuleShortName)).thenReturn(Mono.empty());
    StepVerifier.create(moduleService.findByShortName(mockModuleShortName))
        .expectComplete()
        .verify();
    verify(moduleRepository, times(1)).findByShortName(mockModuleShortName);
  }

  @Test
  void findByShortName_NullShortName_ReturnsNullPointerException() {
    StepVerifier.create(moduleService.findByShortName(null))
        .expectErrorMatches(
            throwable ->
                throwable instanceof NullPointerException
                    && throwable.getMessage().equals("Module short name cannot be null"))
        .verify();
  }

  @Test
  void findNameById_ExistingModuleId_ReturnsUserEntity() {
    final Module mockModule = mock(Module.class);
    final String mockModuleName = "MockName";
    final long mockModuleId = 21;

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));
    when(mockModule.getName()).thenReturn(mockModuleName);

    StepVerifier.create(moduleService.findNameById(mockModuleId))
        .expectNext(mockModuleName)
        .expectComplete()
        .verify();

    verify(moduleRepository, times(1)).findById(mockModuleId);
    verify(mockModule, times(1)).getName();
  }

  @Test
  void findNameById_InvalidModuleId_ReturnsInvalidModuleIdException() {
    for (final long invalidId : TestUtils.INVALID_IDS) {
      StepVerifier.create(moduleService.findNameById(invalidId))
          .expectErrorMatches(
              throwable ->
                  throwable instanceof InvalidModuleIdException
                      && throwable
                          .getMessage()
                          .equals("Module id must be a strictly positive integer"))
          .verify();
    }
  }

  @Test
  void findNameById_NonExistentModuleId_ReturnsEmpty() {
    final long nonExistentModuleId = 248;
    when(moduleRepository.findById(nonExistentModuleId)).thenReturn(Mono.empty());
    StepVerifier.create(moduleService.findNameById(nonExistentModuleId)).expectComplete().verify();
    verify(moduleRepository, times(1)).findById(nonExistentModuleId);
  }

  @Test
  void setDynamicFlag_FlagPreviouslySet_ReturnPreviousFlag() {
    final byte[] newFlag = {-118, 17, 4, -35, 17, -3, -94, 0, -72, -17, 65, -127, 12, 82, 9, 29};

    final Module mockModuleWithoutStaticFlag = mock(Module.class);
    final Module mockModuleWithStaticFlag = mock(Module.class);
    final Module mockModuleWithDynamicFlag = mock(Module.class);

    final long mockModuleId = 517;

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
  void setDynamicFlag_NegativeModuleId_ReturnsInvalidModuleIdException() {
    StepVerifier.create(moduleService.setDynamicFlag(-1))
        .expectError(InvalidModuleIdException.class)
        .verify();
    StepVerifier.create(moduleService.setDynamicFlag(-1000))
        .expectError(InvalidModuleIdException.class)
        .verify();
  }

  @Test
  void setDynamicFlag_StaticFlagIsSet_SetsDynamicFlag() {
    final Module mockModuleWithStaticFlag = mock(Module.class);
    final Module mockModuleWithDynamicFlag = mock(Module.class);

    final long mockModuleId = 134;

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
  void setDynamicFlag_ZeroModuleId_ReturnsInvalidModuleIdException() {
    StepVerifier.create(moduleService.setDynamicFlag(0))
        .expectError(InvalidModuleIdException.class)
        .verify();
  }

  @Test
  void setStaticFlag_EmptyStaticFlag_ReturnsInvalidFlagException() {
    StepVerifier.create(moduleService.setStaticFlag(1, ""))
        .expectError(InvalidFlagException.class)
        .verify();
  }

  @Test
  void setStaticFlag_InvalidModuleId_ReturnsInvalidModuleIdException() {
    StepVerifier.create(moduleService.setStaticFlag(0, "flag"))
        .expectError(InvalidModuleIdException.class)
        .verify();
    StepVerifier.create(moduleService.setStaticFlag(-1, "flag"))
        .expectError(InvalidModuleIdException.class)
        .verify();
    StepVerifier.create(moduleService.setStaticFlag(-9999, "flag"))
        .expectError(InvalidModuleIdException.class)
        .verify();
  }

  @Test
  void setStaticFlag_NullStaticFlag_ReturnsNulPointerException() {
    StepVerifier.create(moduleService.setStaticFlag(1, null))
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

    final long mockModuleId = 239;

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

    ArgumentCaptor<Long> findArgument = ArgumentCaptor.forClass(Long.class);
    verify(moduleRepository, times(1)).findById(findArgument.capture());
    assertThat(findArgument.getValue()).isEqualTo(mockModuleId);

    ArgumentCaptor<Module> saveArgument = ArgumentCaptor.forClass(Module.class);
    verify(moduleRepository, times(1)).save(saveArgument.capture());
    assertThat(saveArgument.getValue().getStaticFlag()).isEqualTo(staticFlag);
  }

  @Test
  void setName_EmptyName_ReturnsEmptyModuleNameException() {
    StepVerifier.create(moduleService.setName(847L, ""))
        .expectErrorMatches(
            throwable ->
                throwable instanceof EmptyModuleNameException
                    && throwable.getMessage().equals("Module name cannot be empty"))
        .verify();
  }

  @Test
  void setName_InvalidModuleId_ReturnsInvalidModuleIdException() {
    for (final long moduleId : TestUtils.INVALID_IDS) {
      StepVerifier.create(moduleService.setName(moduleId, "name"))
          .expectErrorMatches(
              throwable ->
                  throwable instanceof InvalidModuleIdException
                      && throwable
                          .getMessage()
                          .equals("Module id must be a strictly positive integer"))
          .verify();
    }
  }

  @Test
  void setName_NullName_ReturnsNullPointerException() {
    StepVerifier.create(moduleService.setName(204L, null))
        .expectErrorMatches(
            throwable ->
                throwable instanceof NullPointerException
                    && throwable.getMessage().equals("Module name cannot be null"))
        .verify();
  }

  @Test
  void setName_ValidName_Succeeds() {
    Module mockModule = mock(Module.class);
    String newName = "newName";

    final long mockModuleId = 30;

    when(moduleRepository.findById(mockModuleId)).thenReturn(Mono.just(mockModule));

    when(mockModule.withName(newName)).thenReturn(mockModule);
    when(moduleRepository.save(any(Module.class))).thenReturn(Mono.just(mockModule));
    when(mockModule.getName()).thenReturn(newName);

    StepVerifier.create(moduleService.setName(mockModuleId, newName))
        .assertNext(module -> assertThat(module.getName()).isEqualTo(newName))
        .expectComplete()
        .verify();

    InOrder order = inOrder(mockModule, moduleRepository);

    order.verify(mockModule, times(1)).withName(newName);
    order.verify(moduleRepository, times(1)).save(mockModule);
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    moduleService = new ModuleService(moduleRepository, keyService);
  }
}
