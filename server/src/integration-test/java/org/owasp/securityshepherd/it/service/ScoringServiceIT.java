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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.repository.ModulePointRepository;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.ModuleScoreRepository;
import org.owasp.securityshepherd.repository.SubmissionDatabaseClient;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.service.ConfigurationService;
import org.owasp.securityshepherd.service.CryptoService;
import org.owasp.securityshepherd.service.DatabaseService;
import org.owasp.securityshepherd.service.KeyService;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.ScoringService;
import org.owasp.securityshepherd.service.SubmissionService;
import org.owasp.securityshepherd.service.UserService;
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

  @Autowired
  ModuleService moduleService;

  @Autowired
  UserService userService;

  @Autowired
  ModuleScoreRepository moduleScoreRepository;

  @Autowired
  SubmissionService submissionService;

  @Autowired
  ScoringService scoringService;

  @Autowired
  SubmissionDatabaseClient submissionDatabaseClient;

  @Autowired
  ModuleRepository moduleRepository;

  @Autowired
  SubmissionRepository submissionRepository;

  @Autowired
  ModulePointRepository modulePointRepository;

  @Autowired
  Clock clock;

  @Autowired
  DatabaseService databaseService;

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
    List<Integer> userIds = new ArrayList<>();
    userIds.add(userService.create("TestUser1").block());
    userIds.add(userService.create("TestUser2").block());
    userIds.add(userService.create("TestUser3").block());
    userIds.add(userService.create("TestUser4").block());
    userIds.add(userService.create("TestUser5").block());
    userIds.add(userService.create("TestUser6").block());
    userIds.add(userService.create("TestUser7").block());
    userIds.add(userService.create("TestUser8").block());

    // Create a module to submit to
    final int moduleId = moduleService.create("ScoreTestModule").block().getId();

    // Set that module to have an exact flag
    moduleService.setExactFlag(moduleId, flag).block();

    // Set scoring levels for module
    scoringService.setModuleScore(moduleId, 0, 100).block();

    scoringService.setModuleScore(moduleId, 1, 50).block();
    scoringService.setModuleScore(moduleId, 2, 40).block();
    scoringService.setModuleScore(moduleId, 3, 30).block();
    scoringService.setModuleScore(moduleId, 4, 20).block();

    // Create some other modules we aren't interested in
    final int moduleId2 = moduleService.create("AnotherModule").block().getId();
    moduleService.setExactFlag(moduleId2, flag).block();

    // Set scoring levels for module
    scoringService.setModuleScore(moduleId2, 0, 9999).block();
    scoringService.setModuleScore(moduleId2, 1, 10).block();

    final int moduleId3 = moduleService.create("IrrelevantModule").block().getId();
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
    Iterator<Integer> userIdIterator = userIds.iterator();
    Iterator<Clock> clockIterator = clocks.iterator();
    Iterator<String> flagIterator = flags.iterator();

    while (userIdIterator.hasNext() && clockIterator.hasNext() && flagIterator.hasNext()) {
      // Recreate the submission service every time with a new clock
      initializeService(clockIterator.next());

      final int currentUserId = userIdIterator.next();
      final String currentFlag = flagIterator.next();

      // Submit a new flag
      submissionService.submit(currentUserId, moduleId, currentFlag).block();
      submissionService.submit(currentUserId, moduleId2, currentFlag).block();
      submissionService.submit(currentUserId, moduleId3, currentFlag).block();
    }

    StepVerifier.create(scoringService.computeScoreForModule(moduleId)).expectNextCount(6).expectComplete()
        .verify();
    StepVerifier.create(scoringService.computeScoreForModule(moduleId2)).expectNextCount(6).expectComplete()
    .verify();
    StepVerifier.create(scoringService.computeScoreForModule(moduleId3)).expectNextCount(6).expectComplete()
    .verify();
  }

  private void initializeService(Clock injectedClock) {
    submissionService = new SubmissionService(moduleService, submissionRepository, injectedClock,
        submissionDatabaseClient);
  }

  @BeforeEach
  private void clear() {
    // Print more verbose errors if something goes wrong with reactor
    Hooks.onOperatorDebug();

    databaseService.clearAll().block();
  }
}
