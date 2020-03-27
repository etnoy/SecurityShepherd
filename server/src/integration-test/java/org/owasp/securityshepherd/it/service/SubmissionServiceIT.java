package org.owasp.securityshepherd.it.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.User.UserBuilder;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.repository.UserRepository;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.SubmissionService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
public class SubmissionServiceIT {

  @Autowired
  UserService userService;

  @Autowired
  ModuleService moduleService;

  @Autowired
  SubmissionService submissionService;

  @Autowired
  ModuleRepository moduleRepository;

  @Autowired
  SubmissionRepository submissionRepository;

  @Autowired
  UserRepository userRepository;

  @Test
  public void submitModule_ValidExactFlag_Success() throws Exception {

    final String flag = "thisisaflag";

    final Mono<Integer> userIdMono = userService.create("TestUser").map(User::getId);

    final Mono<Integer> moduleIdMono =
        moduleService.create("Test Module").map(Module::getId).flatMap(moduleId -> {

          try {
            return moduleService.setExactFlag(moduleId, flag);
          } catch (InvalidFlagException | InvalidModuleIdException e) {
            return Mono.error(e);
          }

        }).map(Module::getId);


    StepVerifier
        .create(Mono.zip(userIdMono, moduleIdMono)
            .flatMap(tuple -> submissionService.submit(tuple.getT1(), tuple.getT2(), flag)))
        .assertNext(correctFlag -> {
          assertThat(correctFlag, is(true));
        }).expectComplete().verify();

  }

  @Test
  public void checkSort() throws Exception {

    final UserBuilder userBuilder = User.builder();

    final Flux<Integer> userIds = Flux.just("1", "2", "3", "4", "5")
        .map(userName -> userBuilder.displayName(userName).build()).flatMap(userRepository::save)
        .map(User::getId);

    final Flux<LocalDateTime> LocalDateTimes =
        Flux.just(1000, 5000, 2000, 4000, 3000).map(Timestamp::new).map(Timestamp::toLocalDateTime);

    final Mono<Integer> moduleId = 
        moduleRepository.save(Module.builder().name("TestModule").build()).map(Module::getId);

    final Flux<Tuple2<Integer, LocalDateTime>> submissionData = Flux.zip(userIds, LocalDateTimes);

    final Flux<Submission> submissions = submissionData.flatMap(tuple -> moduleId.map(id -> {
      return Submission.builder().moduleId(id).userId(tuple.getT1()).time(tuple.getT2()).build();
    })).flatMap(submissionRepository::save);

    // final Flux<Submission> submissions = submissionData.flatMap(tuple -> moduleId.map(id -> {
    // return Submission.builder().moduleId(id).userId(tuple.getT1()).time(tuple.getT2()).build();
    // })).flatMap(submissionRepository::save);

    Flux<String> orderedDisplayNames =
        submissions.thenMany(moduleId.flatMapMany(submissionService::findSortedByModuleId))
            .map(Submission::getUserId).flatMap(userService::findById).map(User::getDisplayName);

    StepVerifier.create(orderedDisplayNames).expectNext("1").expectNext("3").expectNext("5")
        .expectNext("4").expectNext("2").expectComplete().verify();

  }

  @BeforeEach
  private void setUp() {
    // Print more verbose errors if something goes wrong with reactor
    Hooks.onOperatorDebug();

    // Clear all users and modules from repository before every test
    userService.deleteAll().block();
    moduleService.deleteAll().block();
    submissionService.deleteAll().block();

  }

}
