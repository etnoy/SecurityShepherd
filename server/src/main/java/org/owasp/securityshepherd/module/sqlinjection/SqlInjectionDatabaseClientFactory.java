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
package org.owasp.securityshepherd.module.sqlinjection;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import io.r2dbc.spi.ConnectionFactories;

@Component
public class SqlInjectionDatabaseClientFactory {
  public DatabaseClient create(final String connectionUrl) {
    // We have to replace all spaces with URL-encoded spaces for r2dbc to work
    final String escapedUrl = connectionUrl.replace(" ", "%20");

    // Create a connection factory from the URL
    // Create a database client from the connection factory
    return DatabaseClient.create(ConnectionFactories.get(escapedUrl));
  }
}
