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
package org.owasp.securityshepherd.it.module.sqlinjection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorial;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorialRow;
import org.owasp.securityshepherd.scoring.ScoreService;
import org.owasp.securityshepherd.scoring.Submission;
import org.owasp.securityshepherd.scoring.SubmissionService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"application.runner.enabled=false"})
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("SqlInjectionTutorial integration test")
class SqlInjectionTutorialIT {

  @Autowired SqlInjectionTutorial sqlInjectionTutorial;

  @Autowired TestUtils testUtils;

  @Autowired UserService userService;

  @Autowired ModuleService moduleService;

  @Autowired SubmissionService submissionService;

  @Autowired ScoreService scoreService;

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private String extractFlagFromRow(final SqlInjectionTutorialRow row) {
    return row.getComment().replaceAll("Well done, flag is ", "");
  }

  @Test
  void submitSql_QueryWithNoMatches_EmptyResultSet() {
    final Long userId = userService.create("TestUser1").block();
    StepVerifier.create(sqlInjectionTutorial.submitQuery(userId, "test")).expectComplete().verify();
  }

  @Test
  void submitSql_CorrectAttackQuery_ReturnsWholeDatabase() {
    final Long userId = userService.create("TestUser1").block();

    StepVerifier.create(sqlInjectionTutorial.submitQuery(userId, "' OR '1' = '1"))
        .expectNextCount(6)
        .expectComplete()
        .verify();
  }

  @Test
  void submitSql_SqlSyntaxError_ReturnsError() {
    final Long userId = userService.create("TestUser1").block();

    final String errorMessage =
        "io.r2dbc.spi.R2dbcBadGrammarException: [42000] [42000] "
            + "Syntax error in SQL statement \"SELECT * FROM sqlinjection.users "
            + "WHERE name = ''[*]'\"; SQL statement:\n"
            + "SELECT * FROM sqlinjection.users WHERE name = ''' [42000-200]";

    StepVerifier.create(
            sqlInjectionTutorial.submitQuery(userId, "'").map(SqlInjectionTutorialRow::getError))
        .expectNext(errorMessage)
        .expectComplete()
        .verify();
  }

  @Test
  void submitSql_CorrectAttackQuery_ReturnedFlagIsCorrect() {
    final Long userId = userService.create("TestUser1").block();
    final Long moduleId = sqlInjectionTutorial.getModuleId();

    final Mono<String> flagMono =
        sqlInjectionTutorial
            .submitQuery(userId, "' OR '1' = '1")
            .skip(5)
            .next()
            .map(this::extractFlagFromRow);

    // Submit the flag we got from the sql injection and make sure it validates
    StepVerifier.create(
            flagMono
                .flatMap(flag -> submissionService.submit(userId, moduleId, flag))
                .map(Submission::isValid))
        .expectNext(true)
        .expectComplete()
        .verify();
  }

  @Test
  void submitSql_CorrectAttackQuery_ModifiedFlagIsWrong() {
    final Long userId = userService.create("TestUser1").block();
    final Long moduleId = sqlInjectionTutorial.initialize().block();

    moduleService.setDynamicFlag(moduleId).block();

    final Mono<String> flagVerificationMono =
        sqlInjectionTutorial
            .submitQuery(userId, "' OR '1' = '1")
            .skip(5)
            .next()
            .map(this::extractFlagFromRow);

    // Take the flag we got from the tutorial, modify it, and expect validation to fail
    StepVerifier.create(
            flagVerificationMono
                .flatMap(flag -> submissionService.submit(userId, moduleId, flag + "wrong"))
                .map(Submission::isValid))
        .expectNext(false)
        .expectComplete()
        .verify();
  }

  // TODO:

  // ' OR '1=1 gives numberformatexception

  @BeforeEach
  private void clear() {
    testUtils.deleteAll().block();
    sqlInjectionTutorial.initialize().block();
  }
}
