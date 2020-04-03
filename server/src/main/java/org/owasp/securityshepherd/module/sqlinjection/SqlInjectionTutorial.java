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
    final Mono<String> insertedFlag = moduleService.getDynamicFlag(userId, moduleId).map(
        flag -> "INSERT INTO sqlinjection.users values ('666', 'Union Jack', 'Well done, flag is "
            + flag + "')");

    // Create a H2SQL in-memory database that loads the data needed for this tutorial.
    final Mono<String> connectionUrlMono =
        insertedFlag.map(flag -> "r2dbc:h2:mem:///sql-injection-tutorial-for-uid"
            + Long.toString(userId) + ";INIT=RUNSCRIPT FROM 'classpath:module/sql-injection.sql'"
            // The following inserts URL encoded backslash and semicolon, i.e. "\;"
            + "%5C%3B" + flag);

    // Create a databaseclient that interacts with the database
    final Mono<DatabaseClient> databaseClientMono = connectionUrlMono
        .map(url -> ConnectionFactories.get(url.replace(" ", "%20"))).map(DatabaseClient::create);

    // Create the database query. Yes, this is vulnerable to SQL injection. That's the whole point.
    final String injectionQuery =
        "SELECT * FROM sqlinjection.users WHERE name = '" + usernameQuery + "'";

    // Return all rows that match
    return databaseClientMono.flatMapMany(client -> client.execute(injectionQuery).fetch().all());
  }
}
