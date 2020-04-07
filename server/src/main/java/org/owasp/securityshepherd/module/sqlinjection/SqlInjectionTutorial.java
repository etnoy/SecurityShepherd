package org.owasp.securityshepherd.module.sqlinjection;

import java.util.Map;
import org.owasp.securityshepherd.service.ModuleService;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import io.r2dbc.spi.ConnectionFactories;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SqlInjectionTutorial {

  private final ModuleService moduleService;

  public Flux<Map<String, Object>> submitSql(final long userId, final long moduleId,
      final String usernameQuery) {
    // Generate a dynamic flag and add it as a row to the database creation script. The flag is
    // different for every user to prevent copying flags
    final Mono<String> insertionQuery = moduleService.getDynamicFlag(userId, moduleId)
        .map(flag -> String.format(
            "INSERT INTO sqlinjection.users values ('666', 'Union Jack', 'Well done, flag is %s')",
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

    // Return all rows that match
    return databaseClientMono.flatMapMany(client -> client.execute(injectionQuery).fetch().all());
  }
}
