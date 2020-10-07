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
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.csrf.CsrfService;
import org.owasp.securityshepherd.module.csrf.CsrfTutorial;
import org.owasp.securityshepherd.module.flag.FlagTutorial;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorial;
import org.owasp.securityshepherd.module.xss.XssTutorial;
import org.owasp.securityshepherd.scoring.CorrectionService;
import org.owasp.securityshepherd.scoring.ScoreService;
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

  @Mock private FlagTutorial flagTutorial;

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
            flagTutorial,
            submissionService,
            correctionService,
            scoringService);
  }
}
