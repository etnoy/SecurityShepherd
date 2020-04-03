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

    final Mono<String> insertedFlag = moduleService.getDynamicFlag(userId, moduleId).map(
        flag -> "INSERT INTO sqlinjection.users values ('666', 'Union Jack', 'Well done, flag is "
            + flag + "')");

    final Mono<String> connectionUrlMono =
        insertedFlag.map(flag -> "r2dbc:h2:mem:///sql-injection-tutorial-for-uid"
            + Long.toString(userId) + ";INIT=RUNSCRIPT FROM 'classpath:module/sql-injection.sql'"
            // The following inserts URL encoded backslash and semicolon, i.e. "\;"
            + "%5C%3B" + flag);

    final Mono<DatabaseClient> databaseClientMono =
        connectionUrlMono.map(url -> ConnectionFactories.get(url.replaceAll(" ", "%20")))
            .map(DatabaseClient::create);

    final String injectionQuery =
        "SELECT * FROM sqlinjection.users WHERE name = '" + usernameQuery + "'";

    return databaseClientMono.flatMapMany(client -> client.execute(injectionQuery).fetch().all());
  }
}
