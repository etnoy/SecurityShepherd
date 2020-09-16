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
package org.owasp.securityshepherd.test.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.configuration.PasswordEncoderConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordEncoderConfiguration unit test")
public class PasswordEncoderConfigurationTest {
  PasswordEncoderConfiguration passwordEncoderConfiguration;

  @Test
  void passwordEncoder_NoArgument_ReturnsBcryptPasswordEncoder() {
    final PasswordEncoder passwordEncoder = passwordEncoderConfiguration.passwordEncoder();
    assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    passwordEncoderConfiguration = new PasswordEncoderConfiguration();
  }
}
