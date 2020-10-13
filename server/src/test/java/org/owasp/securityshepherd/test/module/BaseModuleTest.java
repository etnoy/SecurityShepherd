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
package org.owasp.securityshepherd.test.module;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.module.BaseModule;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("BaseModule unit test")
class BaseModuleTest {

  @Mock ModuleService moduleService;

  @Mock FlagHandler flagHandler;

  private class TestModule extends BaseModule {

    protected TestModule(
        String moduleName,
        ModuleService moduleService,
        FlagHandler flagHandler,
        String staticFlag) {
      super(moduleName, moduleService, flagHandler, staticFlag);
    }
  }

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  @Test
  void constructor_DynamicFlag_ReturnsNewBaseModule() {
    final Module mockModule = mock(Module.class);
    final String mockModuleName = "test-module";

    when(moduleService.create(mockModuleName)).thenReturn(Mono.just(mockModule));

    TestModule dynamicFlagModule = new TestModule(mockModuleName, moduleService, flagHandler, null);

    StepVerifier.create(dynamicFlagModule.getInit()).expectComplete().verify();

    assertThat(dynamicFlagModule.getModuleService()).isEqualTo(moduleService);
    assertThat(dynamicFlagModule.getFlagHandler()).isEqualTo(flagHandler);
  }

  @Test
  void constructor_StaticFlag_ReturnsNewBaseModule() {
    final Module mockModule = mock(Module.class);
    final String mockModuleName = "test-module";
    final String mockStaticFlag = "flag";

    when(moduleService.create(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(moduleService.setStaticFlag(mockModuleName, mockStaticFlag))
        .thenReturn(Mono.just(mockModule));

    TestModule staticFlagModule =
        new TestModule(mockModuleName, moduleService, flagHandler, mockStaticFlag);

    StepVerifier.create(staticFlagModule.getInit()).expectComplete().verify();

    assertThat(staticFlagModule.getModuleService()).isEqualTo(moduleService);
    assertThat(staticFlagModule.getFlagHandler()).isEqualTo(flagHandler);

    verify(moduleService).setStaticFlag(mockModuleName, mockStaticFlag);
  }

  @Test
  void getFlag_StaticFlagNoUserId_ReturnsFlag() {
    final Module mockModule = mock(Module.class);
    final String mockModuleName = "test-module";
    final String mockStaticFlag = "flag";

    when(moduleService.create(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(moduleService.setStaticFlag(mockModuleName, mockStaticFlag))
        .thenReturn(Mono.just(mockModule));

    TestModule staticFlagModule =
        new TestModule(mockModuleName, moduleService, flagHandler, mockStaticFlag);

    staticFlagModule.getInit().block();

    when(mockModule.isFlagStatic()).thenReturn(true);
    when(mockModule.getStaticFlag()).thenReturn(mockStaticFlag);

    StepVerifier.create(staticFlagModule.getFlag())
        .expectNext(mockStaticFlag)
        .expectComplete()
        .verify();
  }

  @Test
  void getFlag_StaticFlagWithUserId_ReturnsFlag() {
    final Module mockModule = mock(Module.class);
    final String mockModuleName = "test-module";
    final String mockStaticFlag = "flag";
    final Long mockUserId = 45L;

    when(moduleService.create(mockModuleName)).thenReturn(Mono.just(mockModule));

    when(moduleService.setStaticFlag(mockModuleName, mockStaticFlag))
        .thenReturn(Mono.just(mockModule));

    TestModule staticFlagModule =
        new TestModule(mockModuleName, moduleService, flagHandler, mockStaticFlag);

    staticFlagModule.getInit().block();

    when(mockModule.isFlagStatic()).thenReturn(true);
    when(mockModule.getStaticFlag()).thenReturn(mockStaticFlag);

    StepVerifier.create(staticFlagModule.getFlag(mockUserId))
        .expectNext(mockStaticFlag)
        .expectComplete()
        .verify();
  }

  @Test
  void getFlag_DynamicFlagWithUserId_ReturnsFlag() {
    final Module mockModule = mock(Module.class);
    final String mockModuleName = "test-module";
    final String mockStaticFlag = null;
    final String mockDynamicFlag = "flag{123abc}";

    final Long mockUserId = 45L;

    when(moduleService.create(mockModuleName)).thenReturn(Mono.just(mockModule));

    TestModule dynamicFlagModule =
        new TestModule(mockModuleName, moduleService, flagHandler, mockStaticFlag);

    when(flagHandler.getDynamicFlag(mockUserId, mockModuleName))
        .thenReturn(Mono.just(mockDynamicFlag));

    dynamicFlagModule.getInit().block();

    when(mockModule.isFlagStatic()).thenReturn(false);

    StepVerifier.create(dynamicFlagModule.getFlag(mockUserId))
        .expectNext(mockDynamicFlag)
        .expectComplete()
        .verify();
  }

  @Test
  void getFlag_DynamicFlagWithoutUserId_ReturnsError() {
    final Module mockModule = mock(Module.class);
    final String mockModuleName = "test-module";
    final String mockStaticFlag = null;

    when(moduleService.create(mockModuleName)).thenReturn(Mono.just(mockModule));

    TestModule dynamicFlagModule =
        new TestModule(mockModuleName, moduleService, flagHandler, mockStaticFlag);

    dynamicFlagModule.getInit().block();

    when(mockModule.isFlagStatic()).thenReturn(false);

    StepVerifier.create(dynamicFlagModule.getFlag())
        .expectError(InvalidFlagStateException.class)
        .verify();
  }
}
