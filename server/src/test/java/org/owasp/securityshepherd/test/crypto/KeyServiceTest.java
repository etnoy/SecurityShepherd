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

package org.owasp.securityshepherd.test.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.crypto.KeyService;
import org.owasp.securityshepherd.crypto.PrngFactory;
import org.owasp.securityshepherd.exception.RngException;
import reactor.core.publisher.Hooks;

@ExtendWith(MockitoExtension.class)
@DisplayName("KeyService unit test")
public class KeyServiceTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  KeyService keyService;

  @Mock
  PrngFactory prngFactory;

  @Test
  public void convertByteKeyToString_ValidInput_ReturnsExpectedOutput() {
    assertThat(keyService.convertStringFlagToBytes("thisisaflag"))
        .isEqualTo(new byte[] {116, 104, 105, 115, 105, 115, 97, 102, 108, 97, 103});
  }

  @Test
  public void byteFlagToString_ValidBytes_ReturnsString() {
    assertThat(keyService
        .byteFlagToString(new byte[] {116, 104, 105, 115, 105, 115, 97, 102, 108, 97, 103}))
            .isEqualTo("74686973697361666c6167");
  }

  @Test
  public void stringFlagToByte_ValidString_ReturnsString() throws DecoderException {
    assertThat(keyService.stringFlagToByte("74686973697361666c6167"))
        .isEqualTo(new byte[] {116, 104, 105, 115, 105, 115, 97, 102, 108, 97, 103});
  }

  @Test
  public void convertStringKeyToBytes_ValidInput_ReturnsExpectedOutput() {
    assertThat(keyService
        .convertByteKeyToString(new byte[] {116, 104, 105, 115, 105, 115, 97, 102, 108, 97, 103}))
            .isEqualTo("thisisaflag");
  }

  @Test
  public void generateRandomBytes_NoSuchAlgorithmException_ThrowsRngException() throws Exception {
    final int[] testedLengths = {0, 1, 12, 16, 128, 4096};

    when(prngFactory.getPrng()).thenThrow(
        new NoSuchAlgorithmException("Null/empty securerandom.strongAlgorithms Security Property"));

    for (int length : testedLengths) {
      assertThatExceptionOfType(RngException.class)
          .isThrownBy(() -> keyService.generateRandomBytes(length))
          .withMessageMatching("Could not initialize PRNG");
    }
  }

  @Test
  public void generateRandomBytes_ValidLength_ReturnsRandomBytes() throws Exception {
    final int[] testedLengths = {0, 1, 12, 16, 128, 4096};

    final SecureRandom mockPrng = mock(SecureRandom.class);

    when(prngFactory.getPrng()).thenReturn(mockPrng);

    for (int length : testedLengths) {
      final byte[] randomBytes = keyService.generateRandomBytes(length);
      assertThat(randomBytes).isNotNull();
      assertThat(randomBytes).hasSize(length);
    }
  }

  @Test
  public void generateRandomString_ValidLength_ReturnsRandomString() throws Exception {
    final SecureRandom mockPrng = mock(SecureRandom.class);

    when(prngFactory.getPrng()).thenReturn(mockPrng);
    final int[] testedLengths = {0, 1, 12, 16, 128, 4096};
    for (int length : testedLengths) {
      final String randomString = keyService.generateRandomString(length);
      assertThat(randomString).isNotNull();
      assertThat(randomString).hasSize(length);
    }
  }

  @BeforeEach
  private void setUp() {
    keyService = new KeyService(prngFactory);
  }
}
