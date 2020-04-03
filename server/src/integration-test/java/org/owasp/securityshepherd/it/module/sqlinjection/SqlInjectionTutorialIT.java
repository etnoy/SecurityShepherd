package org.owasp.securityshepherd.it.module.sqlinjection;

import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorial;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.ScoreService;
import org.owasp.securityshepherd.service.SubmissionService;
import org.owasp.securityshepherd.service.UserService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("SqlInjectionTutorial integration test")
public class SqlInjectionTutorialIT {

  @Autowired
  SqlInjectionTutorial sqlInjectionTutorial;

  @Autowired
  TestUtils testUtils;

  @Autowired
  UserService userService;

  @Autowired
  ModuleService moduleService;

  @Autowired
  SubmissionService submissionService;

  @Autowired
  ScoreService scoreService;

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private String extractFlagFromRow(final Map<String, Object> row) {
    return row.get("COMMENT").toString().replaceAll("Well done, flag is ", "");
  }

  @Test
  public void test() {
    final Long userId1 = userService.create("TestUser1").block();
    final Long userId2 = userService.create("TestUser2").block();

    final long moduleId = moduleService.create("Sql Injection Tutorial").block().getId();

    moduleService.setDynamicFlag(moduleId).block();

    scoreService.setModuleScore(moduleId, 0, 100).block();
    scoreService.setModuleScore(moduleId, 1, 10).block();
    scoreService.setModuleScore(moduleId, 2, 5).block();

    StepVerifier.create(sqlInjectionTutorial.submitSql(userId1, moduleId, "test")).expectComplete()
        .verify();

    StepVerifier.create(sqlInjectionTutorial.submitSql(userId1, moduleId, "OR 1=1"))
        .expectNextCount(1).expectComplete().verify();

    StepVerifier.create(sqlInjectionTutorial.submitSql(userId2, moduleId, "' OR '1' = '1"))
        .expectNextCount(6).expectComplete().verify();

    final Mono<String> flagVerificationMono = sqlInjectionTutorial
        .submitSql(userId1, moduleId, "' OR '1' = '1").skip(5).next().map(this::extractFlagFromRow);

    StepVerifier.create(
        flagVerificationMono.flatMap(flag -> submissionService.submit(userId1, moduleId, flag))
            .map(Submission::isValid))
        .expectNext(true).expectComplete().verify();
  }

  @BeforeEach
  private void clear() {
    testUtils.deleteAll().block();
  }
}
