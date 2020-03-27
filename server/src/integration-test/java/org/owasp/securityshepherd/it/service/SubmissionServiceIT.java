package org.owasp.securityshepherd.it.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.SubmissionService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

  @BeforeEach
  private void setUp() {
    // Print more verbose errors if something goes wrong with reactor
    Hooks.onOperatorDebug();

    // Clear all users and modules from repository before every test
    userService.deleteAll().block();
    moduleService.deleteAll().block();

  }

}
