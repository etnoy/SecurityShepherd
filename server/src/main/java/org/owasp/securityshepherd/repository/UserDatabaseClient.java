package org.owasp.securityshepherd.repository;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class UserDatabaseClient {
  private final DatabaseClient databaseClient;

  public Mono<Integer> findUserIdByLoginName(final String loginName) {
    return databaseClient
        // TODO: prevent sql injection with template (but how?)
        .execute("SELECT user_id from password_auth WHERE login_name = " + loginName + " LIMIT 1")
        .map((row, rowMetadata) -> {
          return row.get("user_id", Integer.class);
        }).one();
  }
}
