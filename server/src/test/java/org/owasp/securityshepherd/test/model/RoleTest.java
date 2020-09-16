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
package org.owasp.securityshepherd.test.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.authentication.Role;

@DisplayName("Role unit test")
public class RoleTest {
  @Test
  public void userRole_ToString_IsRoleUser() {
    assertThat(Role.ROLE_USER.toString()).isEqualTo("ROLE_USER");
  }

  @Test
  public void adminRole_ToString_IsRoleAdmin() {
    assertThat(Role.ROLE_ADMIN.toString()).isEqualTo("ROLE_ADMIN");
  }
}
