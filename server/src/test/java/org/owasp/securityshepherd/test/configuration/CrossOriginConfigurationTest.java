/*
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
package org.owasp.securityshepherd.test.configuration;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.configuration.CrossOriginConfiguration;

@ExtendWith(MockitoExtension.class)
@DisplayName("CrossOriginConfiguration unit test")
class CrossOriginConfigurationTest {
  CrossOriginConfiguration crossOriginConfiguration;

  @Test
  void authenticate_EmptyPassword_ReturnsIllegalArgumentException() {
    assertThatCode(() -> crossOriginConfiguration.corsConfigurationSource())
        .doesNotThrowAnyException();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    crossOriginConfiguration = new CrossOriginConfiguration();
  }
}
