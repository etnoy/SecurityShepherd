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
package org.owasp.securityshepherd.test.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.application.StartupRunner;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.csrf.CsrfService;
import org.owasp.securityshepherd.module.csrf.CsrfTutorial;
import org.owasp.securityshepherd.module.flag.FlagTutorial;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorial;
import org.owasp.securityshepherd.module.xss.XssTutorial;
import org.owasp.securityshepherd.scoring.Correction;
import org.owasp.securityshepherd.scoring.CorrectionService;
import org.owasp.securityshepherd.scoring.ModulePoint;
import org.owasp.securityshepherd.scoring.ScoreService;
import org.owasp.securityshepherd.scoring.Submission;
import org.owasp.securityshepherd.scoring.SubmissionRepository;
import org.owasp.securityshepherd.scoring.SubmissionService;
import org.owasp.securityshepherd.user.UserService;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@DisplayName("StartupRunner unit test")
class StartupRunnerTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private StartupRunner startupRunner;

  @Mock private UserService userService;

  @Mock private ModuleService moduleService;

  @Mock private XssTutorial xssTutorial;

  @Mock private SqlInjectionTutorial sqlInjectionTutorial;

  @Mock private CsrfTutorial csrfTutorial;

  @Mock private CsrfService csrfService;

  @Mock private FlagTutorial dummyModule;

  @Mock private SubmissionService submissionService;

  @Mock private CorrectionService correctionService;

  @Mock private ScoreService scoringService;

  @Mock private SubmissionRepository submissionRepository;

  @Mock private FlagHandler flagHandler;

  @Test
  void run_MockedServices_CallsMocks() {

    final String flag = "itsaflag";

    when(userService.createPasswordUser(
            "Admin", "admin", "$2y$08$WpfUVZLcXNNpmM2VwSWlbe25dae.eEC99AOAVUiU5RaJmfFsE9B5G"))
        .thenReturn(Mono.just(1L));

    when(userService.createPasswordUser(
            "Dummy", "dummy", "$2y$08$WpfUVZLcXNNpmM2VwSWlbe25dae.eEC99AOAVUiU5RaJmfFsE9B5G"))
        .thenReturn(Mono.just(1L));

    when(xssTutorial.initialize()).thenReturn(Mono.just(1L));
    when(sqlInjectionTutorial.initialize()).thenReturn(Mono.just(2L));
    when(csrfTutorial.initialize()).thenReturn(Mono.just(3L));

    when(userService.create("TestUser1")).thenReturn(Mono.just(2L));
    when(userService.create("TestUser2")).thenReturn(Mono.just(3L));
    when(userService.create("TestUser3")).thenReturn(Mono.just(4L));
    when(userService.create("TestUser4")).thenReturn(Mono.just(5L));
    when(userService.create("TestUser5")).thenReturn(Mono.just(6L));
    when(userService.create("TestUser6")).thenReturn(Mono.just(7L));
    when(userService.create("TestUser7")).thenReturn(Mono.just(8L));
    when(userService.create("TestUser8")).thenReturn(Mono.just(9L));

    final Module mockScoreTestModule = mock(Module.class);
    when(moduleService.create("ScoreTestModule", "score-test"))
        .thenReturn(Mono.just(mockScoreTestModule));
    when(mockScoreTestModule.getId()).thenReturn(3L);
    when(moduleService.setStaticFlag(3L, flag)).thenReturn(Mono.just(mockScoreTestModule));

    final ModulePoint mockModulePoint = mock(ModulePoint.class);

    when(scoringService.setModuleScore(3L, 0, 100)).thenReturn(Mono.just(mockModulePoint));
    when(scoringService.setModuleScore(3L, 1, 50)).thenReturn(Mono.just(mockModulePoint));
    when(scoringService.setModuleScore(3L, 2, 40)).thenReturn(Mono.just(mockModulePoint));
    when(scoringService.setModuleScore(3L, 3, 30)).thenReturn(Mono.just(mockModulePoint));
    when(scoringService.setModuleScore(3L, 4, 20)).thenReturn(Mono.just(mockModulePoint));

    final Module mockAnotherModule = mock(Module.class);
    when(moduleService.create("AnotherModule", "another-module"))
        .thenReturn(Mono.just(mockAnotherModule));
    when(mockAnotherModule.getId()).thenReturn(4L);
    when(moduleService.setStaticFlag(4L, flag)).thenReturn(Mono.just(mockAnotherModule));

    when(scoringService.setModuleScore(4L, 0, 50)).thenReturn(Mono.just(mockModulePoint));
    when(scoringService.setModuleScore(4L, 1, 30)).thenReturn(Mono.just(mockModulePoint));
    when(scoringService.setModuleScore(4L, 2, 10)).thenReturn(Mono.just(mockModulePoint));

    final Module mockIrrelevantModule = mock(Module.class);
    when(moduleService.create("IrrelevantModule", "irrelevant-module"))
        .thenReturn(Mono.just(mockIrrelevantModule));
    when(mockIrrelevantModule.getId()).thenReturn(5L);
    when(moduleService.setStaticFlag(5L, flag)).thenReturn(Mono.just(mockIrrelevantModule));

    when(scoringService.setModuleScore(5L, 0, 1)).thenReturn(Mono.just(mockModulePoint));

    final Submission mockSubmission = mock(Submission.class);

    when(submissionService.submit(2L, 3L, "itsaflag")).thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(2L, 4L, "itsaflag")).thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(2L, 5L, "itsaflag")).thenReturn(Mono.just(mockSubmission));

    when(submissionService.submit(3L, 3L, "itsaflag")).thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(3L, 4L, "itsaflag")).thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(3L, 5L, "itsaflag")).thenReturn(Mono.just(mockSubmission));

    when(submissionService.submit(4L, 3L, "itsaflag")).thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(4L, 4L, "itsaflag")).thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(4L, 5L, "itsaflag")).thenReturn(Mono.just(mockSubmission));

    when(submissionService.submit(5L, 3L, "itsanincorrectflag"))
        .thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(5L, 4L, "itsanincorrectflag"))
        .thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(5L, 5L, "itsanincorrectflag"))
        .thenReturn(Mono.just(mockSubmission));

    when(submissionService.submit(6L, 3L, "itsaflag")).thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(6L, 4L, "itsaflag")).thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(6L, 5L, "itsaflag")).thenReturn(Mono.just(mockSubmission));

    when(submissionService.submit(7L, 3L, "itsaflag")).thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(7L, 4L, "itsaflag")).thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(7L, 5L, "itsaflag")).thenReturn(Mono.just(mockSubmission));

    when(submissionService.submit(8L, 3L, "itsaflag")).thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(8L, 4L, "itsaflag")).thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(8L, 5L, "itsaflag")).thenReturn(Mono.just(mockSubmission));

    when(submissionService.submit(9L, 3L, "itsanincorrectflag"))
        .thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(9L, 4L, "itsanincorrectflag"))
        .thenReturn(Mono.just(mockSubmission));
    when(submissionService.submit(9L, 5L, "itsanincorrectflag"))
        .thenReturn(Mono.just(mockSubmission));

    final Correction mockCorrection = mock(Correction.class);
    when(correctionService.submit(4L, -1000, "Penalty for cheating"))
        .thenReturn(Mono.just(mockCorrection));
    when(correctionService.submit(3L, 100, "Thanks for the bribe"))
        .thenReturn(Mono.just(mockCorrection));

    assertDoesNotThrow(() -> startupRunner.run(null));
  }

  @BeforeEach
  private void setUp() throws Exception {
    // Set up the system under test
    startupRunner =
        new StartupRunner(
            userService,
            moduleService,
            xssTutorial,
            sqlInjectionTutorial,
            csrfTutorial,
            dummyModule,
            submissionService,
            correctionService,
            scoringService);
  }
}
