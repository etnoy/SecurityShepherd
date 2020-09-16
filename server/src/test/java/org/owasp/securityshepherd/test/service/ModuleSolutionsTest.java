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
import org.owasp.securityshepherd.exception.EmptyModuleShortNameException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleListItem;
import org.owasp.securityshepherd.module.ModuleListItem.ModuleListItemBuilder;
import org.owasp.securityshepherd.scoring.Submission;
import org.owasp.securityshepherd.scoring.SubmissionService;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.ModuleSolutions;
import org.owasp.securityshepherd.test.util.TestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModuleSolutions unit test")
public class ModuleSolutionsTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private ModuleSolutions moduleSolutions;

  @Mock private ModuleService moduleService;

  @Mock private SubmissionService submissionService;

  @Test
  public void
      findOpenModuleByShortNameWithSolutionStatus_EmptyShortName_ReturnsInvalidModuleShortNameException() {
    final long mockUserId = 690L;
    StepVerifier.create(moduleSolutions.findOpenModuleByShortNameWithSolutionStatus(mockUserId, ""))
        .expectErrorMatches(
            throwable ->
                throwable instanceof EmptyModuleShortNameException
                    && throwable.getMessage().equals("Module short name cannot be empty"))
        .verify();
  }

  @Test
  public void
      findOpenModuleByShortNameWithSolutionStatus_InvalidUserid_ReturnsInvalidUserIdException() {
    final String mockModuleShortName = "shortName";
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(
              moduleSolutions.findOpenModuleByShortNameWithSolutionStatus(
                  userId, mockModuleShortName))
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
  public void
      findOpenModuleByShortNameWithSolutionStatus_ModuleIsClosedAndHasSolution_ReturnsEmpty() {
    final long mockModuleId = 782L;
    final String mockModuleShortName = "test-module";

    final long mockUserId = 1000L;
    final Module mockModule = mock(Module.class);
    ;

    when(mockModule.isOpen()).thenReturn(false);

    when(moduleService.findByShortName(mockModuleShortName)).thenReturn(Mono.just(mockModule));

    StepVerifier.create(
            moduleSolutions.findOpenModuleByShortNameWithSolutionStatus(
                mockUserId, mockModuleShortName))
        .expectComplete()
        .verify();

    verify(mockModule, never()).getId();
    verify(mockModule, never()).getName();
    verify(mockModule, never()).getShortName();
    verify(mockModule, never()).getDescription();
    verify(mockModule, times(2)).isOpen();

    verify(submissionService, never()).findAllValidByUserIdAndModuleId(mockUserId, mockModuleId);
    verify(moduleService, times(1)).findByShortName(mockModuleShortName);
  }

  @Test
  public void
      findOpenModuleByShortNameWithSolutionStatus_ModuleIsOpenAndHasSolution_ReturnsModule() {
    final long mockModuleId = 782L;
    final String mockModuleName = "Test Module";
    final String mockModuleShortName = "test-module";
    final String mockModuleDescription = "This is a module";

    final long mockUserId = 1000L;
    final Module mockModule = mock(Module.class);
    ;

    when(mockModule.getId()).thenReturn(mockModuleId);
    when(mockModule.getName()).thenReturn(mockModuleName);
    when(mockModule.getShortName()).thenReturn(mockModuleShortName);
    when(mockModule.getDescription()).thenReturn(mockModuleDescription);
    when(mockModule.isOpen()).thenReturn(true);

    final Submission mockedSubmission = mock(Submission.class);

    when(submissionService.findAllValidByUserIdAndModuleId(mockUserId, mockModuleId))
        .thenReturn(Mono.just(mockedSubmission));

    when(moduleService.findByShortName(mockModuleShortName)).thenReturn(Mono.just(mockModule));

    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();

    final ModuleListItem listItem =
        moduleListItemBuilder
            .id(mockModuleId)
            .name(mockModuleName)
            .shortName(mockModuleShortName)
            .description(mockModuleDescription)
            .isSolved(true)
            .build();

    StepVerifier.create(
            moduleSolutions.findOpenModuleByShortNameWithSolutionStatus(
                mockUserId, mockModuleShortName))
        .expectNext(listItem)
        .expectComplete()
        .verify();

    verify(mockModule, times(2)).getId();
    verify(mockModule, times(1)).getName();
    verify(mockModule, times(1)).getShortName();
    verify(mockModule, times(1)).getDescription();
    verify(mockModule, times(2)).isOpen();

    verify(submissionService, times(1)).findAllValidByUserIdAndModuleId(mockUserId, mockModuleId);
    verify(moduleService, times(1)).findByShortName(mockModuleShortName);
  }

  @Test
  public void
      findOpenModuleByShortNameWithSolutionStatus_ModuleIsOpenAndHasNoSolution_ReturnsModule() {
    final long mockModuleId = 782L;
    final String mockModuleName = "Test Module";
    final String mockModuleShortName = "test-module";
    final String mockModuleDescription = "This is a module";

    final long mockUserId = 1000L;
    final Module mockModule = mock(Module.class);
    ;

    when(mockModule.getId()).thenReturn(mockModuleId);
    when(mockModule.getName()).thenReturn(mockModuleName);
    when(mockModule.getShortName()).thenReturn(mockModuleShortName);
    when(mockModule.getDescription()).thenReturn(mockModuleDescription);
    when(mockModule.isOpen()).thenReturn(true);

    when(submissionService.findAllValidByUserIdAndModuleId(mockUserId, mockModuleId))
        .thenReturn(Mono.empty());

    when(moduleService.findByShortName(mockModuleShortName)).thenReturn(Mono.just(mockModule));

    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();

    final ModuleListItem listItem =
        moduleListItemBuilder
            .id(mockModuleId)
            .name(mockModuleName)
            .shortName(mockModuleShortName)
            .description(mockModuleDescription)
            .isSolved(false)
            .build();

    StepVerifier.create(
            moduleSolutions.findOpenModuleByShortNameWithSolutionStatus(
                mockUserId, mockModuleShortName))
        .expectNext(listItem)
        .expectComplete()
        .verify();

    verify(mockModule, times(2)).getId();
    verify(mockModule, times(1)).getName();
    verify(mockModule, times(1)).getShortName();
    verify(mockModule, times(1)).getDescription();
    verify(mockModule, times(2)).isOpen();

    verify(submissionService, times(1)).findAllValidByUserIdAndModuleId(mockUserId, mockModuleId);
    verify(moduleService, times(1)).findByShortName(mockModuleShortName);
  }

  @Test
  public void
      findOpenModuleByShortNameWithSolutionStatus_NullShortName_ReturnsInvalidModuleShortNameException() {
    final long mockUserId = 398L;
    StepVerifier.create(
            moduleSolutions.findOpenModuleByShortNameWithSolutionStatus(mockUserId, null))
        .expectErrorMatches(
            throwable ->
                throwable instanceof NullPointerException
                    && throwable.getMessage().equals("Module short name cannot be null"))
        .verify();
  }

  @Test
  public void
      findOpenModulesByUserIdWithSolutionStatus_InvalidUserid_ReturnsInvalidUserIdException() {
    for (final long userId : TestUtils.INVALID_IDS) {
      StepVerifier.create(moduleSolutions.findOpenModulesByUserIdWithSolutionStatus(userId))
          .expectError(InvalidUserIdException.class)
          .verify();
    }
  }

  @Test
  public void findOpenModulesByUserIdWithSolutionStatus_NoModulesOrSubmissions_ReturnsEmpty() {
    final long mockUserId = 1000L;

    when(submissionService.findAllValidIdsByUserId(mockUserId)).thenReturn(Mono.empty());

    StepVerifier.create(moduleSolutions.findOpenModulesByUserIdWithSolutionStatus(mockUserId))
        .expectComplete()
        .verify();

    verify(submissionService, times(1)).findAllValidIdsByUserId(mockUserId);
    verify(moduleService, never()).findAllOpen();
  }

  @Test
  public void findOpenModulesByUserIdWithSolutionStatus_ValidSubmissions_ReturnsModules() {
    final long mockModule1Id = 315L;
    final String mockModule1Name = "Module 1";
    final String mockModule1ShortName = "module-1";
    final String mockModule1Description = "This is the first module";

    final long mockModule2Id = 759L;
    final String mockModule2Name = "Module 2";
    final String mockModule2ShortName = "module-2";
    final String mockModule2Description = "This is the second module";

    final long mockModule3Id = 245L;

    final long mockUserId = 1000L;
    final Module mockModule1 = mock(Module.class);
    final Module mockModule2 = mock(Module.class);
    final Module mockModule3 = mock(Module.class);
    final Module mockModule4 = mock(Module.class);

    when(mockModule1.getId()).thenReturn(mockModule1Id);
    when(mockModule1.getName()).thenReturn(mockModule1Name);
    when(mockModule1.getShortName()).thenReturn(mockModule1ShortName);
    when(mockModule1.getDescription()).thenReturn(mockModule1Description);

    when(mockModule2.getId()).thenReturn(mockModule2Id);
    when(mockModule2.getName()).thenReturn(mockModule2Name);
    when(mockModule2.getShortName()).thenReturn(mockModule2ShortName);
    when(mockModule2.getDescription()).thenReturn(mockModule2Description);

    final Mono<List<Long>> mockedValidSolutionsList =
        Mono.just(new ArrayList<Long>(Arrays.asList(mockModule1Id, mockModule2Id, mockModule3Id)));

    when(submissionService.findAllValidIdsByUserId(mockUserId))
        .thenReturn(mockedValidSolutionsList);

    when(moduleService.findAllOpen()).thenReturn(Flux.just(mockModule1, mockModule2));

    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();

    final ModuleListItem listItem1 =
        moduleListItemBuilder
            .id(mockModule1Id)
            .name(mockModule1Name)
            .shortName(mockModule1ShortName)
            .description(mockModule1Description)
            .isSolved(true)
            .build();

    final ModuleListItem listItem2 =
        moduleListItemBuilder
            .id(mockModule2Id)
            .name(mockModule2Name)
            .shortName(mockModule2ShortName)
            .description(mockModule2Description)
            .isSolved(true)
            .build();

    StepVerifier.create(moduleSolutions.findOpenModulesByUserIdWithSolutionStatus(mockUserId))
        .expectNext(listItem1)
        .expectNext(listItem2)
        .expectComplete()
        .verify();

    verify(mockModule1, times(1)).getId();
    verify(mockModule1, times(1)).getName();
    verify(mockModule1, times(1)).getShortName();
    verify(mockModule1, times(1)).getDescription();
    verify(mockModule1, never()).isOpen();

    verify(mockModule2, times(1)).getId();
    verify(mockModule2, times(1)).getName();
    verify(mockModule2, times(1)).getShortName();
    verify(mockModule2, times(1)).getDescription();
    verify(mockModule2, never()).isOpen();

    verify(mockModule3, never()).getId();
    verify(mockModule3, never()).getName();
    verify(mockModule3, never()).getShortName();
    verify(mockModule3, never()).getDescription();
    verify(mockModule3, never()).isOpen();

    verify(mockModule4, never()).getId();
    verify(mockModule4, never()).getName();
    verify(mockModule4, never()).getShortName();
    verify(mockModule4, never()).getDescription();
    verify(mockModule4, never()).isOpen();

    verify(submissionService, times(1)).findAllValidIdsByUserId(mockUserId);
    verify(moduleService, times(1)).findAllOpen();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    moduleSolutions = new ModuleSolutions(moduleService, submissionService);
  }
}
