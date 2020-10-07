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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.owasp.securityshepherd.exception.EmptyModuleNameException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleListItem;
import org.owasp.securityshepherd.module.ModuleListItem.ModuleListItemBuilder;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.ModuleSolutions;
import org.owasp.securityshepherd.scoring.Submission;
import org.owasp.securityshepherd.scoring.SubmissionService;
import org.owasp.securityshepherd.test.util.TestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModuleSolutions unit test")
class ModuleSolutionsTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private ModuleSolutions moduleSolutions;

  @Mock private ModuleService moduleService;

  @Mock private SubmissionService submissionService;

  @Test
  void findOpenModuleByIdWithSolutionStatus_EmptyModuleName_ReturnsInvalidModuleNameException() {
    final long mockUserId = 690L;
    StepVerifier.create(moduleSolutions.findOpenModuleByIdWithSolutionStatus(mockUserId, ""))
        .expectErrorMatches(
            throwable ->
                throwable instanceof EmptyModuleNameException
                    && throwable.getMessage().equals("Module name cannot be empty"))
        .verify();
  }

  @Test
  void findOpenModuleByIdWithSolutionStatus_InvalidUserid_ReturnsInvalidUserIdException() {
    final String mockModuleName = "moduleName";
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(
              moduleSolutions.findOpenModuleByIdWithSolutionStatus(userId, mockModuleName))
          .expectErrorMatches(
              throwable ->
                  throwable instanceof InvalidUserIdException
                      && throwable
                          .getMessage()
                          .equals("User id must be a strictly positive integer"))
          .verify();
    }
  }

  @Test
  void findOpenModuleByIdWithSolutionStatus_ModuleIsClosedAndHasSolution_ReturnsEmpty() {
    final String mockModuleName = "moduleName";

    final long mockUserId = 1000L;
    final Module mockModule = mock(Module.class);

    when(mockModule.isOpen()).thenReturn(false);

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    StepVerifier.create(
            moduleSolutions.findOpenModuleByIdWithSolutionStatus(mockUserId, mockModuleName))
        .expectComplete()
        .verify();

    verify(mockModule, never()).getId();
    verify(mockModule, times(2)).isOpen();

    verify(submissionService, never())
        .findAllValidByUserIdAndModuleName(mockUserId, mockModuleName);
    verify(moduleService, times(1)).findByName(mockModuleName);
  }

  @Test
  void findOpenModuleByIdWithSolutionStatus_ModuleIsOpenAndHasSolution_ReturnsModule() {
    final String mockModuleName = "moduleName";

    final long mockUserId = 1000L;
    final Module mockModule = mock(Module.class);

    when(mockModule.getName()).thenReturn(mockModuleName);
    when(mockModule.isOpen()).thenReturn(true);

    final Submission mockedSubmission = mock(Submission.class);

    when(submissionService.findAllValidByUserIdAndModuleName(mockUserId, mockModuleName))
        .thenReturn(Mono.just(mockedSubmission));

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();

    final ModuleListItem listItem =
        moduleListItemBuilder.name(mockModuleName).isSolved(true).build();

    StepVerifier.create(
            moduleSolutions.findOpenModuleByIdWithSolutionStatus(mockUserId, mockModuleName))
        .expectNext(listItem)
        .expectComplete()
        .verify();

    verify(mockModule, times(2)).isOpen();

    verify(submissionService, times(1))
        .findAllValidByUserIdAndModuleName(mockUserId, mockModuleName);
    verify(moduleService, times(1)).findByName(mockModuleName);
  }

  @Test
  void findOpenModuleByIdWithSolutionStatus_ModuleIsOpenAndHasNoSolution_ReturnsModule() {
    final String mockModuleName = "moduleName";

    final long mockUserId = 1000L;
    final Module mockModule = mock(Module.class);

    when(mockModule.getName()).thenReturn(mockModuleName);
    when(mockModule.isOpen()).thenReturn(true);

    when(submissionService.findAllValidByUserIdAndModuleName(mockUserId, mockModuleName))
        .thenReturn(Mono.empty());

    when(moduleService.findByName(mockModuleName)).thenReturn(Mono.just(mockModule));

    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();

    final ModuleListItem listItem =
        moduleListItemBuilder.name(mockModuleName).isSolved(false).build();

    StepVerifier.create(
            moduleSolutions.findOpenModuleByIdWithSolutionStatus(mockUserId, mockModuleName))
        .expectNext(listItem)
        .expectComplete()
        .verify();

    verify(mockModule, times(2)).getName();
    verify(mockModule, times(2)).isOpen();

    verify(submissionService, times(1))
        .findAllValidByUserIdAndModuleName(mockUserId, mockModuleName);
    verify(moduleService, times(1)).findByName(mockModuleName);
  }

  @Test
  void findOpenModuleByIdWithSolutionStatus_NullModuleName_ReturnsInvalidModuleNameException() {
    final Long mockUserId = 108L;
    StepVerifier.create(moduleSolutions.findOpenModuleByIdWithSolutionStatus(mockUserId, null))
        .expectErrorMatches(
            throwable ->
                throwable instanceof NullPointerException
                    && throwable.getMessage().equals("Module name cannot be null"))
        .verify();
  }

  @Test
  void findOpenModulesByUserIdWithSolutionStatus_InvalidUserid_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(moduleSolutions.findOpenModulesByUserIdWithSolutionStatus(userId))
          .expectError(InvalidUserIdException.class)
          .verify();
    }
  }

  @Test
  void findOpenModulesByUserIdWithSolutionStatus_NoModulesOrSubmissions_ReturnsEmpty() {
    final long mockUserId = 1000L;

    when(submissionService.findAllValidModuleNamesByUserId(mockUserId)).thenReturn(Mono.empty());

    StepVerifier.create(moduleSolutions.findOpenModulesByUserIdWithSolutionStatus(mockUserId))
        .expectComplete()
        .verify();

    verify(submissionService, times(1)).findAllValidModuleNamesByUserId(mockUserId);
    verify(moduleService, never()).findAllOpen();
  }

  @Test
  void findOpenModulesByUserIdWithSolutionStatus_ValidSubmissions_ReturnsModules() {
    final String mockModule1Name = "id1";

    final String mockModule2Name = "id2";

    final String mockModule3Id = "id3";

    final long mockUserId = 1000L;
    final Module mockModule1 = mock(Module.class);
    final Module mockModule2 = mock(Module.class);
    final Module mockModule3 = mock(Module.class);
    final Module mockModule4 = mock(Module.class);

    when(mockModule1.getName()).thenReturn(mockModule1Name);
    when(mockModule2.getName()).thenReturn(mockModule2Name);

    final Mono<List<String>> mockedValidSolutionsList =
        Mono.just(
            new ArrayList<String>(Arrays.asList(mockModule1Name, mockModule2Name, mockModule3Id)));

    when(submissionService.findAllValidModuleNamesByUserId(mockUserId))
        .thenReturn(mockedValidSolutionsList);

    when(moduleService.findAllOpen()).thenReturn(Flux.just(mockModule1, mockModule2));

    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();

    final ModuleListItem listItem1 =
        moduleListItemBuilder.name(mockModule1Name).isSolved(true).build();

    final ModuleListItem listItem2 =
        moduleListItemBuilder.name(mockModule2Name).isSolved(true).build();

    StepVerifier.create(moduleSolutions.findOpenModulesByUserIdWithSolutionStatus(mockUserId))
        .expectNext(listItem1)
        .expectNext(listItem2)
        .expectComplete()
        .verify();

    verify(mockModule1, times(1)).getName();

    verify(mockModule1, never()).isOpen();

    verify(mockModule2, times(1)).getName();

    verify(mockModule2, never()).isOpen();

    verify(mockModule3, never()).getName();

    verify(mockModule3, never()).isOpen();

    verify(mockModule4, never()).getName();

    verify(mockModule4, never()).isOpen();

    verify(submissionService, times(1)).findAllValidModuleNamesByUserId(mockUserId);
    verify(moduleService, times(1)).findAllOpen();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    moduleSolutions = new ModuleSolutions(moduleService, submissionService);
  }
}
