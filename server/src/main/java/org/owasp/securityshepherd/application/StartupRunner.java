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
package org.owasp.securityshepherd.application;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.csrf.CsrfTutorial;
import org.owasp.securityshepherd.module.flag.FlagTutorial;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorial;
import org.owasp.securityshepherd.module.xss.XssTutorial;
import org.owasp.securityshepherd.scoring.CorrectionService;
import org.owasp.securityshepherd.scoring.ScoreService;
import org.owasp.securityshepherd.scoring.SubmissionService;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(
    prefix = "application.runner",
    value = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@Component
@RequiredArgsConstructor
@Slf4j
public class StartupRunner implements ApplicationRunner {

  private final UserService userService;

  private final ModuleService moduleService;

  private final XssTutorial xssTutorial;

  private final SqlInjectionTutorial sqlInjectionTutorial;

  private final CsrfTutorial csrfTutorial;

  private final FlagTutorial flagTutorial;

  private final SubmissionService submissionService;

  private final CorrectionService correctionService;

  private final ScoreService scoringService;

  @Autowired private Clock clock;

  @Override
  public void run(ApplicationArguments args) {
    log.info("Running StartupRunner");
    // Create a default admin account
    userService
        .createPasswordUser(
            "Admin", "admin", "$2y$08$WpfUVZLcXNNpmM2VwSWlbe25dae.eEC99AOAVUiU5RaJmfFsE9B5G")
        .block();
    
    Mono.when(csrfTutorial.init(),
    flagTutorial.init(),
    xssTutorial.init(),sqlInjectionTutorial.init()).block();

  }
}
