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

package org.owasp.securityshepherd.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.service.KeyService;
import reactor.core.publisher.Hooks;

@DisplayName("KeyService unit test")
public class KeyServiceTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private KeyService keyService;

  @Test
  public void convertByteKeyToString_ValidInput_ReturnsExpectedOutput() {
    assertThat(keyService.convertStringFlagToBytes("thisisaflag"))
        .isEqualTo(new byte[] {116, 104, 105, 115, 105, 115, 97, 102, 108, 97, 103});
  }

  @Test
  public void convertStringKeyToBytes_ValidInput_ReturnsExpectedOutput() {
    assertThat(keyService
        .convertByteKeyToString(new byte[] {116, 104, 105, 115, 105, 115, 97, 102, 108, 97, 103}))
            .isEqualTo("thisisaflag");
  }

  @Test
  public void generateRandomBytes_ValidLength_ReturnsRandomBytes() {
    final int[] testedLengths = {0, 1, 12, 16, 128, 4096};

    for (int length : testedLengths) {
      final byte[] randomBytes = keyService.generateRandomBytes(length);
      assertThat(randomBytes).isNotNull();
      assertThat(randomBytes).hasSize(length);
    }
  }

  @Test
  public void generateRandomString_ValidLength_ReturnsRandomString() {
    final int[] testedLengths = {0, 1, 12, 16, 128, 4096};
    for (int length : testedLengths) {
      final String randomString = keyService.generateRandomString(length);
      assertThat(randomString).isNotNull();
      assertThat(randomString).hasSize(length);
    }
  }

  @BeforeEach
  private void setUp() {
    keyService = new KeyService();
  }
}
