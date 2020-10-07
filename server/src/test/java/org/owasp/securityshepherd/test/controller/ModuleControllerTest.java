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
package org.owasp.securityshepherd.test.controller;

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
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleController;
import org.owasp.securityshepherd.module.ModuleListItem;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.ModuleSolutions;
import org.owasp.securityshepherd.scoring.SubmissionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModuleController unit test")
class ModuleControllerTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private ModuleController moduleController;

  @Mock private ControllerAuthentication controllerAuthentication;

  @Mock private SubmissionService submissionService;

  @Mock private ModuleService moduleService;

  @Mock private ModuleSolutions moduleSolutions;

  @Test
  void findAllByUserId_IdExists_ReturnsModule() throws Exception {
    final long mockUserId = 645L;

    final ModuleListItem mockModuleListItem1 = mock(ModuleListItem.class);
    final ModuleListItem mockModuleListItem2 = mock(ModuleListItem.class);
    final ModuleListItem mockModuleListItem3 = mock(ModuleListItem.class);
    final ModuleListItem mockModuleListItem4 = mock(ModuleListItem.class);

    when(controllerAuthentication.getUserId()).thenReturn(Mono.just(mockUserId));

    when(moduleSolutions.findOpenModulesByUserIdWithSolutionStatus(mockUserId))
        .thenReturn(
            Flux.just(
                mockModuleListItem1,
                mockModuleListItem2,
                mockModuleListItem3,
                mockModuleListItem4));

    StepVerifier.create(moduleController.findAllByUserId())
        .expectNext(mockModuleListItem1)
        .expectNext(mockModuleListItem2)
        .expectNext(mockModuleListItem3)
        .expectNext(mockModuleListItem4)
        .expectComplete()
        .verify();
    verify(moduleSolutions, times(1)).findOpenModulesByUserIdWithSolutionStatus(mockUserId);
    verify(controllerAuthentication, times(1)).getUserId();
  }

  @Test
  void findAllByUserId_NotAuthenticated_ReturnsNotAuthenticatedException() throws Exception {
    when(controllerAuthentication.getUserId())
        .thenReturn(Mono.error(new NotAuthenticatedException()));

    StepVerifier.create(moduleController.findAllByUserId())
        .expectError(NotAuthenticatedException.class)
        .verify();
  }

  @Test
  void getModuleById_IdDoesNotExist_ReturnsModule() throws Exception {
    final String mockModuleName = "module-id";
    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.empty());
    StepVerifier.create(moduleController.getModuleById(mockModuleName)).expectComplete().verify();
    verify(moduleService, times(1)).findByName(mockModuleName);
  }

  @Test
  void getModuleById_IdExists_ReturnsModuleListItem() throws Exception {
    final String mockModuleName = "module-id";
    final Module mockedModule = mock(Module.class);
    final ModuleListItem mockModuleListItem = mock(ModuleListItem.class);

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockedModule));
    StepVerifier.create(moduleController.getModuleById(mockModuleName))
        .expectNext(mockModuleListItem)
        .expectComplete()
        .verify();
    verify(moduleService, times(1)).findByName(mockModuleName);
  }

  @Test
  void getModuleByShortName_ShortNameDoesNotExist_ReturnsEmpty() throws Exception {
    final long mockUserId = 94L;
    final String mockShortName = "shortName";
    when(controllerAuthentication.getUserId()).thenReturn(Mono.just(mockUserId));

    when(moduleSolutions.findOpenModuleByIdWithSolutionStatus(mockUserId, mockShortName))
        .thenReturn(Mono.empty());
    StepVerifier.create(moduleController.getModuleById(mockShortName)).expectComplete().verify();
    verify(controllerAuthentication, times(1)).getUserId();
    verify(moduleSolutions, times(1))
        .findOpenModuleByIdWithSolutionStatus(mockUserId, mockShortName);
  }

  @Test
  void getModuleByShortName_ShortNameExists_ReturnsModule() throws Exception {
    final long mockUserId = 94L;
    final String mockShortName = "shortName";
    final ModuleListItem mockModuleListItem = mock(ModuleListItem.class);
    when(controllerAuthentication.getUserId()).thenReturn(Mono.just(mockUserId));

    when(moduleSolutions.findOpenModuleByIdWithSolutionStatus(mockUserId, mockShortName))
        .thenReturn(Mono.just(mockModuleListItem));
    StepVerifier.create(moduleController.getModuleById(mockShortName))
        .expectNext(mockModuleListItem)
        .expectComplete()
        .verify();
    verify(controllerAuthentication, times(1)).getUserId();
    verify(moduleSolutions, times(1))
        .findOpenModuleByIdWithSolutionStatus(mockUserId, mockShortName);
  }

  @BeforeEach
  private void setUp() throws Exception {
    // Set up the system under test
    moduleController = new ModuleController(moduleSolutions, controllerAuthentication);
  }
}
