package org.owasp.securityshepherd.module;

import java.util.Map;
import org.owasp.securityshepherd.service.ModuleService;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SqlModule {

  private final ModuleService moduleService;

  public Flux<Map<String, Object>> submitSql(final long userId, final long moduleId,
      final String usernameQuery) {

    final Mono<String> insertedFlag = moduleService.getDynamicFlag(userId, moduleId).map(
        flag -> "INSERT INTO sqlinjection.users values ('666', 'Union Jack', 'Well done, flag is "
            + flag + "')");

    final Mono<String> connectionUrlMono =
        insertedFlag.map(flag -> "r2dbc:h2:mem:///sql-injection-lesson-for-uid"
            + Long.toString(userId) + ";INIT=RUNSCRIPT FROM 'classpath:module/sql-injection.sql'"
            // The following inserts URL encoded backslash and semicolon, i.e. "\;"
            + "%5C%3B" + flag);

    final Mono<ConnectionFactory> connectionFactoryMono =
        connectionUrlMono.map(url -> ConnectionFactories.get(url.replaceAll(" ", "%20")));

    Mono<DatabaseClient> databaseClientMono = connectionFactoryMono.map(DatabaseClient::create);

    return databaseClientMono.flatMapMany(client -> client
        .execute("SELECT * FROM sqlinjection.users WHERE name = '" + usernameQuery + "'").fetch()
        .all());
  }
}
