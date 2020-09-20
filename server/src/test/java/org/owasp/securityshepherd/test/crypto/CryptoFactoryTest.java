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
package org.owasp.securityshepherd.test.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Mac;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.crypto.CryptoFactory;

import reactor.core.publisher.Hooks;

@ExtendWith(MockitoExtension.class)
@DisplayName("CryptoFactory unit test")
class CryptoFactoryTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  CryptoFactory cryptoFactory;

  @Test
  void getPrng_ReturnsSecureRandomInstance() throws Exception {
    assertThat(cryptoFactory.getPrng()).isInstanceOf(SecureRandom.class);
  }

  @Test
  void getHmac_ReturnsMacInstance() throws Exception {
    assertThat(cryptoFactory.getHmac("Hmac512")).isInstanceOf(Mac.class);
  }

  @Test
  void getHmacKey_ValidKey_ReturnsMacInstance() throws Exception {
    final byte[] key = {-91, -79, 67};
    assertThat(cryptoFactory.getSecretKeySpec("Hmac512", key)).isInstanceOf(Key.class);
  }

  @Test
  void getHmacKey_NullKey_ThrowsIllegalArgumentException() throws Exception {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> cryptoFactory.getSecretKeySpec("Hmac512", null));
  }

  @BeforeEach
  private void setUp() {
    cryptoFactory = new CryptoFactory();
  }
}
