package org.owasp.securityshepherd.it.module.sqlinjection;

import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.owasp.securityshepherd.model.Module;
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
@Execution(ExecutionMode.SAME_THREAD)
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
  public void submitSql_QueryWithNoMatches_EmptyResultSet() {
    final Long userId = userService.create("TestUser1").block();

    final Long moduleId = moduleService.create("Sql Injection Tutorial").map(Module::getId).block();

    moduleService.setDynamicFlag(moduleId).block();

    StepVerifier.create(sqlInjectionTutorial.submitQuery(userId, moduleId, "test")).expectComplete()
        .verify();
  }

  @Test
  public void submitSql_CorrectAttackQuery_ReturnsWholeDatabase() {
    final Long userId = userService.create("TestUser1").block();

    final long moduleId = moduleService.create("Sql Injection Tutorial").block().getId();

    moduleService.setDynamicFlag(moduleId).block();

    StepVerifier.create(sqlInjectionTutorial.submitQuery(userId, moduleId, "' OR '1' = '1"))
        .expectNextCount(6).expectComplete().verify();
  }

  @Test
  public void submitSql_CorrectAttackQuery_ReturnedFlagIsCorrect() {
    final Long userId = userService.create("TestUser1").block();

    final long moduleId = moduleService.create("Sql Injection Tutorial").block().getId();

    moduleService.setDynamicFlag(moduleId).block();

    final Mono<String> flagVerificationMono = sqlInjectionTutorial
        .submitQuery(userId, moduleId, "' OR '1' = '1").skip(5).next().map(this::extractFlagFromRow);

    // Submit the flag we got from the sql injection and make sure it validates
    StepVerifier.create(flagVerificationMono
        .flatMap(flag -> submissionService.submit(userId, moduleId, flag)).map(Submission::isValid))
        .expectNext(true).expectComplete().verify();
  }

  @Test
  public void submitSql_CorrectAttackQuery_ModifiedFlagIsWrong() {
    final Long userId = userService.create("TestUser1").block();

    final long moduleId = moduleService.create("Sql Injection Tutorial").block().getId();

    moduleService.setDynamicFlag(moduleId).block();

    final Mono<String> flagVerificationMono = sqlInjectionTutorial
        .submitQuery(userId, moduleId, "' OR '1' = '1").skip(5).next().map(this::extractFlagFromRow);

    // Take the flag we got from the tutorial, modify it, and expect validation to fail
    StepVerifier.create(flagVerificationMono
        .flatMap(flag -> submissionService.submit(userId, moduleId, flag + "wrong"))
        .map(Submission::isValid)).expectNext(false).expectComplete().verify();
  }
  
  @BeforeEach
  private void clear() {
    testUtils.deleteAll().block();
  }
}
