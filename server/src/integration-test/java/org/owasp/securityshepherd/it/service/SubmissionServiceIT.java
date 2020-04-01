package org.owasp.securityshepherd.it.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.SubmissionDatabaseClient;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.repository.UserRepository;
import org.owasp.securityshepherd.service.DatabaseService;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.SubmissionService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@Slf4j
@DisplayName("SubmissionService integration test")
public class SubmissionServiceIT {

  SubmissionService submissionService;

  @Autowired
  UserService userService;

  @Autowired
  ModuleService moduleService;

  @Autowired
  Clock clock;

  @Autowired
  ModuleRepository moduleRepository;

  @Autowired
  SubmissionRepository submissionRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  SubmissionDatabaseClient submissionDatabaseClient;

  @Autowired
  DatabaseService databaseService;

  @Test
  public void submitFlag_ValidExactFlag_Success() throws Exception {
    final String flag = "thisisaflag";

    final Mono<Integer> userIdMono = userService.create("TestUser");

    final Mono<Integer> moduleIdMono = moduleService.create("Test Module").map(Module::getId)
        .flatMap(moduleId -> moduleService.setExactFlag(moduleId, flag)).map(Module::getId);

    StepVerifier
        .create(Mono.zip(userIdMono, moduleIdMono).flatMap(tuple -> submissionService
            .submit(tuple.getT1(), tuple.getT2(), flag).map(Submission::isValid)))
        .assertNext(correctFlag -> {
          assertThat(correctFlag, is(true));
        }).expectComplete().verify();
  }

  @Test
  public void sortSubmissionsPerModule_NoTies_ReturnsChronologicalListOfSubmissions() {
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

    log.debug("Users: " + userIds);

    // Create a module to submit to
    final int moduleId = moduleService.create("TestModule").block().getId();

    // Set that module to have an exact flag
    moduleService.setExactFlag(moduleId, flag).block();

    // Create a fixed clock from which we will base our offset submission times
    final Clock startTime = Clock.fixed(Instant.parse("2000-01-01T10:00:00.00Z"), ZoneId.of("Z"));

    // Create a list of times at which the above six users will submit their solutions
    List<Integer> timeOffsets = Arrays.asList(4, 1, 3, 2, 1, 0);

    // The duration between times should be 1 day
    final List<Clock> clocks = timeOffsets.stream().map(Duration::ofDays)
        .map(duration -> Clock.offset(startTime, duration)).collect(Collectors.toList());

    final List<String> flags = Arrays.asList(flag, flag, flag, wrongFlag, flag, flag);

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
    }

    // Now verify that the submission service finds all valid submissions and lists them
    // chronologically
    StepVerifier.create(submissionService.findAllValidByModuleIdSortedBySubmissionTime(moduleId))
        .assertNext(result -> {
          assertThat(result.getUserId(), is(userIds.get(5))); // userId 6 ranks 1
          assertThat(result.getRank(), is(1));
        }).assertNext(result -> {
          assertThat(result.getUserId(), is(userIds.get(1))); // userId 2 ranks 2
          assertThat(result.getRank(), is(2));
        }).assertNext(result -> {
          assertThat(result.getUserId(), is(userIds.get(4))); // userId 5 ranks 2
          assertThat(result.getRank(), is(2));
        }).assertNext(result -> {
          assertThat(result.getUserId(), is(userIds.get(2))); // userId 3 ranks 4
          assertThat(result.getRank(), is(4));
        }).assertNext(result -> {
          assertThat(result.getUserId(), is(userIds.get(0))); // userId 1 ranks 5
          assertThat(result.getRank(), is(5));
        }).expectComplete().verify();
  }

  private void initializeService(Clock injectedClock) {
    submissionService = new SubmissionService(moduleService, submissionRepository, injectedClock,
        submissionDatabaseClient);
  }

  @BeforeEach
  private void clear() {
    // Print more verbose errors if something goes wrong with reactor
    Hooks.onOperatorDebug();

    // Initialize services with the real clock
    initializeService(clock);

    databaseService.clearAll().block();
  }
}
