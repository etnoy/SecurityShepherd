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
package org.owasp.securityshepherd.test.module.sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.r2dbc.spi.R2dbcBadGrammarException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.crypto.KeyService;
import org.owasp.securityshepherd.exception.ModuleNotInitializedException;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionDatabaseClientFactory;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorial;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionTutorialRow;
import org.springframework.data.r2dbc.BadSqlGrammarException;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("SqlInjectionTutorial unit test")
class SqlInjectionTutorialTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  SqlInjectionTutorial sqlInjectionTutorial;

  @Mock SqlInjectionDatabaseClientFactory sqlInjectionDatabaseClientFactory;

  @Mock ModuleService moduleService;

  @Mock FlagHandler flagHandler;

  @Mock KeyService keyService;

  private DatabaseClient getClient(final String args, final Flux<SqlInjectionTutorialRow> rows) {
    final DatabaseClient mockDatabaseClient = mock(DatabaseClient.class, RETURNS_DEEP_STUBS);
    when(mockDatabaseClient
            .execute(any(String.class))
            .as(SqlInjectionTutorialRow.class)
            .fetch()
            .all())
        .thenReturn(rows);
    return mockDatabaseClient;
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    sqlInjectionTutorial =
        new SqlInjectionTutorial(
            moduleService, flagHandler, sqlInjectionDatabaseClientFactory, keyService);
  }

  @Test
  void submitQuery_BadSqlGrammarException_ReturnsErrorToUser() {
    final long mockUserId = 318L;
    final Module mockModule = mock(Module.class);
    final String mockFlag = "mockedflag";
    final String query = "username";

    final String mockModuleId = "id";

    when(moduleService.create("sql-injection-tutorial")).thenReturn(Mono.just(mockModule));

    final byte[] randomBytes = {120, 56, 111};
    when(keyService.generateRandomBytes(16)).thenReturn(randomBytes);

    when(mockModule.getId()).thenReturn(mockModuleId);
    when(moduleService.setDynamicFlag(mockModuleId)).thenReturn(Mono.just(mockModule));
    when(flagHandler.getDynamicFlag(mockUserId, mockModuleId)).thenReturn(Mono.just(mockFlag));

    final DatabaseClient mockDatabaseClient = mock(DatabaseClient.class, RETURNS_DEEP_STUBS);
    when(sqlInjectionDatabaseClientFactory.create(any())).thenReturn(mockDatabaseClient);

    when(mockDatabaseClient
            .execute(any(String.class))
            .as(SqlInjectionTutorialRow.class)
            .fetch()
            .all())
        .thenReturn(
            Flux.error(
                new BadSqlGrammarException(
                    "Error", query, new R2dbcBadGrammarException("Syntax error, yo"))));

    StepVerifier.create(sqlInjectionTutorial.submitQuery(mockUserId, query))
        .assertNext(
            row -> {
              assertThat(row.getName()).isNull();
              assertThat(row.getComment()).isNull();
              assertThat(row.getError())
                  .isEqualTo("io.r2dbc.spi.R2dbcBadGrammarException: Syntax error, yo");
            })
        .verifyComplete();
  }

  @Test
  void submitQuery_ModuleInitialized_ReturnsSqlInjectionTutorialRow() {
    final long mockUserId = 606L;
    final Module mockModule = mock(Module.class);
    final String mockFlag = "mockedflag";
    final String query = "username";
    final String mockModuleId = "id";

    when(moduleService.create("sql-injection-tutorial")).thenReturn(Mono.just(mockModule));

    when(mockModule.getId()).thenReturn(mockModuleId);
    when(moduleService.setDynamicFlag(mockModuleId)).thenReturn(Mono.just(mockModule));
    when(flagHandler.getDynamicFlag(mockUserId, mockModuleId)).thenReturn(Mono.just(mockFlag));

    final byte[] randomBytes = {120, 56, 111};
    when(keyService.generateRandomBytes(16)).thenReturn(randomBytes);

    final SqlInjectionTutorialRow mockSqlInjectionTutorialRow1 =
        mock(SqlInjectionTutorialRow.class);
    final SqlInjectionTutorialRow mockSqlInjectionTutorialRow2 =
        mock(SqlInjectionTutorialRow.class);

    when(sqlInjectionDatabaseClientFactory.create(any(String.class)))
        .thenAnswer(
            args ->
                getClient(
                    args.getArgument(0, String.class),
                    Flux.just(mockSqlInjectionTutorialRow1, mockSqlInjectionTutorialRow2)));

    StepVerifier.create(sqlInjectionTutorial.submitQuery(mockUserId, query))
        .expectNext(mockSqlInjectionTutorialRow1)
        .expectNext(mockSqlInjectionTutorialRow2)
        .expectComplete()
        .verify();
  }

  @Test
  void submitQuery_ModuleNotIntialized_ReturnsModuleNotInitializedException() {
    final byte[] randomBytes = {120, 56, 111};
    when(keyService.generateRandomBytes(16)).thenReturn(randomBytes);
    final long mockUserId = 419L;
    final String query = "username";
    assertThrows(
        ModuleNotInitializedException.class,
        () -> sqlInjectionTutorial.submitQuery(mockUserId, query));
  }

  @Test
  void submitQuery_OtherException_ThrowsException() {
    final long mockUserId = 810L;
    final Module mockModule = mock(Module.class);
    final String mockFlag = "mockedflag";
    final String query = "username";
    final String mockModuleId = "id";

    final byte[] randomBytes = {120, 56, 111};
    when(keyService.generateRandomBytes(16)).thenReturn(randomBytes);

    when(moduleService.create("sql-injection-tutorial")).thenReturn(Mono.just(mockModule));

    when(mockModule.getId()).thenReturn(mockModuleId);
    when(moduleService.setDynamicFlag(mockModuleId)).thenReturn(Mono.just(mockModule));
    when(flagHandler.getDynamicFlag(mockUserId, mockModuleId)).thenReturn(Mono.just(mockFlag));

    final DatabaseClient mockDatabaseClient = mock(DatabaseClient.class, RETURNS_DEEP_STUBS);

    when(mockDatabaseClient
            .execute(any(String.class))
            .as(SqlInjectionTutorialRow.class)
            .fetch()
            .all())
        .thenReturn(Flux.error(new RuntimeException()));

    StepVerifier.create(sqlInjectionTutorial.submitQuery(mockUserId, query))
        .expectError(RuntimeException.class);
  }
}
