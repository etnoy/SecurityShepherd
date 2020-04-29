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

package org.owasp.securityshepherd.test.model;

import static org.assertj.core.api.Assertions.assertThat;
import javax.validation.constraints.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.owasp.securityshepherd.user.PasswordRegistrationDto;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("PasswordRegistrationDto unit test")
public class PasswordRegistrationDtoTest {
  @Test
  public void buildComment_ValidComment_Builds() {
    for (final String displayName : TestUtils.STRINGS) {
      for (final String userName : TestUtils.STRINGS) {
        for (final String password : TestUtils.STRINGS) {
          final PasswordRegistrationDto passwordRegistrationDto =
              new PasswordRegistrationDto(displayName, userName, password);
          assertThat(passwordRegistrationDto.getDisplayName()).isEqualTo(displayName);
          assertThat(passwordRegistrationDto.getUserName()).isEqualTo(userName);
          assertThat(passwordRegistrationDto.getPassword()).isEqualTo(password);
        }
      }
    }
  }

  @Test
  public void equals_EqualsVerifier_AsExpected() {
    EqualsVerifier.forClass(PasswordRegistrationDto.class).withIgnoredAnnotations(NotNull.class) .verify();
  }

  @Test
  public void toString_ValidData_AsExpected() {
    final PasswordRegistrationDto passwordRegistrationDto =
        new PasswordRegistrationDto("displayName", "loginName", "password");
    assertThat(passwordRegistrationDto.toString()).isEqualTo(
        "PasswordRegistrationDto(displayName=displayName, userName=loginName, password=password)");
  }
}
