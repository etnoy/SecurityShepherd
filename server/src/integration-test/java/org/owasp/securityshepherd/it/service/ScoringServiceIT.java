package org.owasp.securityshepherd.it.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Scoreboard;
import org.owasp.securityshepherd.repository.CorrectionRepository;
import org.owasp.securityshepherd.repository.ModulePointRepository;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.service.ConfigurationService;
import org.owasp.securityshepherd.service.CryptoService;
import org.owasp.securityshepherd.service.KeyService;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.ScoreService;
import org.owasp.securityshepherd.service.SubmissionService;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DisplayName("ScoringService integration test")
public class ScoringServiceIT {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  @Autowired
  ModuleService moduleService;

  @Autowired
  UserService userService;

  @Autowired
  SubmissionService submissionService;

  @Autowired
  ScoreService scoringService;

  @Autowired
  CorrectionRepository correctionRepository;

  @Autowired
  ModuleRepository moduleRepository;

  @Autowired
  SubmissionRepository submissionRepository;

  @Autowired
  ModulePointRepository modulePointRepository;

  @Autowired
  Clock clock;

  @Autowired
  TestUtils testService;

  @Autowired
  ConfigurationService configurationService;

  @Autowired
  KeyService keyService;

  @Autowired
  CryptoService cryptoService;

  @Test
  public void computeScoreForModule_SubmittedScores_ReturnsCorrectScoresForUsers()
      throws Exception {
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
    final long moduleId = moduleService.create("ScoreTestModule").block().getId();

    // Set that module to have an exact flag
    moduleService.setExactFlag(moduleId, flag).block();

    // Set scoring levels for module1
    scoringService.setModuleScore(moduleId, 0, 100).block();

    scoringService.setModuleScore(moduleId, 1, 50).block();
    scoringService.setModuleScore(moduleId, 2, 40).block();
    scoringService.setModuleScore(moduleId, 3, 30).block();
    scoringService.setModuleScore(moduleId, 4, 20).block();

    // Create some other modules we aren't interested in
    final long moduleId2 = moduleService.create("AnotherModule").block().getId();
    moduleService.setExactFlag(moduleId2, flag).block();

    // Set scoring levels for module2
    scoringService.setModuleScore(moduleId2, 0, 50).block();
    scoringService.setModuleScore(moduleId2, 1, 30).block();
    scoringService.setModuleScore(moduleId2, 2, 10).block();

    final long moduleId3 = moduleService.create("IrrelevantModule").block().getId();
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
      initializeService(clockIterator.next());

      final Long currentUserId = userIdIterator.next();
      final String currentFlag = flagIterator.next();

      // Submit a new flag
      submissionService.submit(currentUserId, moduleId, currentFlag).block();
      submissionService.submit(currentUserId, moduleId2, currentFlag).block();
      submissionService.submit(currentUserId, moduleId3, currentFlag).block();
    }

    final Clock correctionClock =
        Clock.fixed(Instant.parse("2000-01-04T10:00:00.00Z"), ZoneId.of("Z"));
    initializeService(correctionClock);
    submissionService.submitCorrection(userIds.get(2), -1000, "Penalty for cheating").block();
    initializeService(Clock.offset(correctionClock, Duration.ofHours(10)));
    submissionService.submitCorrection(userIds.get(1), 100, "Thanks for the bribe").block();

    StepVerifier.create(scoringService.getScoreboard())
        .expectNext(Scoreboard.builder().rank(1L).userId(userIds.get(1)).score(251L).build())
        .expectNext(Scoreboard.builder().rank(2L).userId(userIds.get(6)).score(231L).build())
        .expectNext(Scoreboard.builder().rank(3L).userId(userIds.get(5)).score(201L).build())
        .expectNext(Scoreboard.builder().rank(4L).userId(userIds.get(0)).score(171L).build())
        .expectNext(Scoreboard.builder().rank(4L).userId(userIds.get(4)).score(171L).build())
        .expectNext(Scoreboard.builder().rank(6L).userId(userIds.get(3)).score(0L).build())
        .expectNext(Scoreboard.builder().rank(6L).userId(userIds.get(7)).score(0L).build())
        .expectNext(Scoreboard.builder().rank(8L).userId(userIds.get(2)).score(-799L).build())
        .expectComplete().verify();
  }

  private void initializeService(Clock injectedClock) {
    submissionService = new SubmissionService(moduleService, submissionRepository,
        correctionRepository, injectedClock);
  }

  @BeforeEach
  private void clear() {
    testService.deleteAll().block();
  }
}
