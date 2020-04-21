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

package org.owasp.securityshepherd.module.sqlinjection;

import javax.annotation.PostConstruct;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.SubmittableModule;
import org.springframework.data.r2dbc.BadSqlGrammarException;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.R2dbcBadGrammarException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class SqlInjectionTutorial implements SubmittableModule {

  private final ModuleService moduleService;

  private final FlagHandler flagComponent;

  private Long moduleId;

  public static final String SHORT_NAME = "sql-injection-tutorial";

  @PostConstruct
  public Mono<Long> initialize() {
    log.info("Creating sql tutorial module");
    final Mono<Module> moduleMono = moduleService.create("Sql Injection Tutorial", SHORT_NAME,
        "Tutorial for making sql injections");

    return moduleMono.flatMap(module -> {
      this.moduleId = module.getId();
      return moduleService.setDynamicFlag(moduleId).then(Mono.just(this.moduleId));
    });
  }

  public Flux<SqlInjectionTutorialRow> submitQuery(final long userId, final String usernameQuery) {
    if (this.moduleId == null) {
      return Flux.error(new RuntimeException("Must initialize module before submitting to it"));
    }
    log.trace("Processing query " + usernameQuery);
    // Generate a dynamic flag and add it as a row to the database creation script. The flag is
    // different for every user to prevent copying flags
    final Mono<String> insertionQuery =
        flagComponent.getDynamicFlag(userId, this.moduleId)
            .map(flag -> String.format(
                "INSERT INTO sqlinjection.users values ('Union Jack', 'Well done, flag is %s')",
                flag));

    // Create a connection URL to a H2SQL in-memory database. Each submission call creates a
    // completely new instance of this database.
    final Mono<String> connectionUrl = insertionQuery
        .map(query -> String.format("r2dbc:h2:mem:///sql-injection-tutorial-for-uid%d;"
            // Load the initial sql file
            + "INIT=RUNSCRIPT FROM 'classpath:module/sql-injection-tutorial.sql'" +
            // %5C%3B is a backslash and semicolon URL-formatted
            "%s%s", userId, "%5C%3B", query));

    // Create a DatabaseClient that allows us to manually interact with the database
    final Mono<DatabaseClient> databaseClientMono = connectionUrl
        // We have to replace all spaces with URL-encoded spaces for r2dbc to work
        .map(url -> url.replace(" ", "%20"))
        // Create a connection factory from the URL
        .map(ConnectionFactories::get)
        // Create a database client from the connection factory
        .map(DatabaseClient::create);

    // Create the database query. Yes, this is vulnerable to SQL injection. That's the whole point.
    final String injectionQuery =
        String.format("SELECT * FROM sqlinjection.users WHERE name = '%s'", usernameQuery);

    return databaseClientMono
        // Execute database query
        .flatMapMany(databaseClient -> databaseClient.execute(injectionQuery)
            .as(SqlInjectionTutorialRow.class).fetch().all())
        // Handle errors
        .onErrorResume(exception -> {
          // We want to forward database syntax errors to the user
          if (exception instanceof BadSqlGrammarException && exception.getCause() != null
              && exception.getCause() instanceof R2dbcBadGrammarException) {
            return Flux.just(
                SqlInjectionTutorialRow.builder().error(exception.getCause().toString()).build());
          } else {
            // All other errors are handled in the usual way
            return Mono.error(exception);
          }
        });
  }

  @Override
  public Long getModuleId() {
    return this.moduleId;
  }
}
