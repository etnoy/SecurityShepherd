/**
 * This file is part of Security Shepherd.
 *
 * <p>Security Shepherd is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with Security
 * Shepherd. If not, see <http://www.gnu.org/licenses/>.
 */
package org.owasp.securityshepherd.test.module.sql;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.module.sqlinjection.SqlInjectionDatabaseClientFactory;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Hooks;

@ExtendWith(MockitoExtension.class)
@DisplayName("SqlInjectionDatabaseClientFactory unit test")
public class SqlInjectionDatabaseClientFactoryTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private final SqlInjectionDatabaseClientFactory sqlInjectionDatabaseClientFactory =
      new SqlInjectionDatabaseClientFactory();

  @Test
  public void create_ValidConnectionUrl_ReturnsDatabaseClient() {
    final String connectionUrl = "r2dbc:h2:mem:///db";
    final DatabaseClient client = sqlInjectionDatabaseClientFactory.create(connectionUrl);
    assertThat(client).isInstanceOf(DatabaseClient.class);
  }
}
