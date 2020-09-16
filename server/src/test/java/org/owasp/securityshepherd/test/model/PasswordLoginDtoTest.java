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
import javax.validation.constraints.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.authentication.PasswordLoginDto;
import org.owasp.securityshepherd.test.util.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("PasswordLoginDto unit test")
class PasswordLoginDtoTest {
  @Test
  void buildComment_ValidComment_Builds() {
    for (final String userName : TestUtils.STRINGS) {
      for (final String password : TestUtils.STRINGS) {
        final PasswordLoginDto passwordLoginDto = new PasswordLoginDto(userName, password);
        assertThat(passwordLoginDto.getUserName()).isEqualTo(userName);
        assertThat(passwordLoginDto.getPassword()).isEqualTo(password);
      }
    }
  }

  @Test
  void equals_EqualsVerifier_AsExpected() {
    EqualsVerifier.forClass(PasswordLoginDto.class).withIgnoredAnnotations(NotNull.class).verify();
  }

  @Test
  void toString_ValidData_AsExpected() {
    final PasswordLoginDto passwordLoginDto = new PasswordLoginDto("loginName", "password");
    assertThat(passwordLoginDto.toString())
        .isEqualTo("PasswordLoginDto(userName=loginName, password=password)");
  }
}
