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

package org.owasp.securityshepherd.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.owasp.securityshepherd.module.ModuleService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(prefix = "application.runner", value = "enabled", havingValue = "true",
    matchIfMissing = true)
@Component
@RequiredArgsConstructor
@Slf4j
public class StartupRunner implements ApplicationRunner {

  private final UserService userService;

  private final ModuleService moduleService;

  private final XssTutorial xssTutorial;

  private final SqlInjectionTutorial sqlInjectionTutorial;

  private final SubmissionService submissionService;

  private final CorrectionService correctionService;

  private final ScoreService scoringService;

  @Autowired
  private Clock clock;

  @Override
  public void run(ApplicationArguments args) {
    log.info("Running StartupRunner");
    // Create a default admin account
    userService.createPasswordUser("Admin", "admin",
        "$2y$08$WpfUVZLcXNNpmM2VwSWlbe25dae.eEC99AOAVUiU5RaJmfFsE9B5G").block();

    xssTutorial.initialize().block();
    sqlInjectionTutorial.initialize().block();

    // We'll use this exact flag
    final String flag = "itsaflag";

    // And this will be an incorrect flag
    final String wrongFlag = "itsanincorrectflag";

    // Create six users and store their ids
    List<Long> userIds = new ArrayList<>();
    userIds.add(userService.create("TestUser1").block());
    userIds.add(userService.create("TestUser2").block());
    userIds.add(userService.create("TestUser3").block());
    userIds.add(userService.create("TestUser4").block());
    userIds.add(userService.create("TestUser5").block());
    userIds.add(userService.create("TestUser6").block());
    userIds.add(userService.create("TestUser7").block());
    userIds.add(userService.create("TestUser8").block());

    // Create a module to submit to
    final long moduleId = moduleService.create("ScoreTestModule", "score-test").block().getId();

    // Set that module to have an exact flag
    moduleService.setExactFlag(moduleId, flag).block();

    // Set scoring levels for module1
    scoringService.setModuleScore(moduleId, 0, 100).block();

    scoringService.setModuleScore(moduleId, 1, 50).block();
    scoringService.setModuleScore(moduleId, 2, 40).block();
    scoringService.setModuleScore(moduleId, 3, 30).block();
    scoringService.setModuleScore(moduleId, 4, 20).block();

    // Create some other modules we aren't interested in
    final long moduleId2 = moduleService.create("AnotherModule", "another-module").block().getId();
    moduleService.setExactFlag(moduleId2, flag).block();

    // Set scoring levels for module2
    scoringService.setModuleScore(moduleId2, 0, 50).block();
    scoringService.setModuleScore(moduleId2, 1, 30).block();
    scoringService.setModuleScore(moduleId2, 2, 10).block();

    final long moduleId3 =
        moduleService.create("IrrelevantModule", "irrelevant-module").block().getId();
    moduleService.setExactFlag(moduleId3, flag).block();

    // You only get 1 point for this module
    scoringService.setModuleScore(moduleId3, 0, 1).block();

    // Create a fixed clock from which we will base our offset submission times
    final Clock startTime = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    // Create a list of times at which the above six users will submit their solutions
    List<Integer> timeOffsets = Arrays.asList(3, 4, 1, 2, 3, 1, 0, 5);

    // The duration between times should be 1 day
    final List<Clock> clocks = timeOffsets.stream().map(Duration::ofDays)
        .map(duration -> Clock.offset(startTime, duration)).collect(Collectors.toList());

    final List<String> flags =
        Arrays.asList(flag, flag, flag, wrongFlag, flag, flag, flag, wrongFlag);

    // Iterate over the user ids and clocks at the same time
    Iterator<Long> userIdIterator = userIds.iterator();
    Iterator<Clock> clockIterator = clocks.iterator();
    Iterator<String> flagIterator = flags.iterator();

    while (userIdIterator.hasNext() && clockIterator.hasNext() && flagIterator.hasNext()) {
      // Recreate the submission service every time with a new clock
      submissionService.setClock(clockIterator.next());

      final Long currentUserId = userIdIterator.next();
      final String currentFlag = flagIterator.next();

      // Submit a new flag
      submissionService.submit(currentUserId, moduleId, currentFlag).block();
      submissionService.submit(currentUserId, moduleId2, currentFlag).block();
      submissionService.submit(currentUserId, moduleId3, currentFlag).block();
    }

    final Clock correctionClock =
        Clock.fixed(Instant.parse("2000-01-04T10:00:00.00Z"), ZoneId.of("Z"));
    submissionService.setClock(correctionClock);
    correctionService.submit(userIds.get(2), -1000, "Penalty for cheating").block();
    submissionService.setClock(Clock.offset(correctionClock, Duration.ofHours(10)));
    correctionService.submit(userIds.get(1), 100, "Thanks for the bribe").block();
    submissionService.setClock(clock);
  }

}
