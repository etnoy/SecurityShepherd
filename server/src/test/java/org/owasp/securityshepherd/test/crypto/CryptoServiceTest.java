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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.crypto.CryptoFactory;
import org.owasp.securityshepherd.crypto.CryptoService;
import org.owasp.securityshepherd.exception.CryptographicException;
import reactor.core.publisher.Hooks;

@ExtendWith(MockitoExtension.class)
@DisplayName("CryptoService unit test")
public class CryptoServiceTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  CryptoService cryptoService;

  @Mock CryptoFactory cryptoFactory;

  @Test
  void hmac_GetHmacThrowsNoSuchAlgorithmException_ThrowsCryptographicException()
      throws Exception {
    final byte[] key = {-91, -79, 67};
    final byte[] message = {120, 56};
    when(cryptoFactory.getHmac()).thenThrow(new NoSuchAlgorithmException());
    assertThatExceptionOfType(CryptographicException.class)
        .isThrownBy(() -> cryptoService.hmac(key, message));
  }

  @Test
  void hmac_InvalidKeyException_ThrowsCryptographicException() throws Exception {
    final byte[] key = {-91};
    final byte[] message = {120, 56, 111};

    Mac mockMac = mock(Mac.class);
    when(cryptoFactory.getHmac()).thenReturn(mockMac);

    SecretKeySpec mockSecretKeySpec = mock(SecretKeySpec.class);
    when(cryptoFactory.getHmacKey(key)).thenReturn(mockSecretKeySpec);

    doThrow(new InvalidKeyException()).when(mockMac).init(mockSecretKeySpec);

    assertThatExceptionOfType(CryptographicException.class)
        .isThrownBy(() -> cryptoService.hmac(key, message));
  }

  @Test
  void hmac_NullKey_ThrowsNullPointerException() {
    final byte[] message = {120, 56, 111, -98, -118, 44, -65, -127, 39, 35};

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> cryptoService.hmac(null, message));
  }

  @Test
  void hmac_NullMessage_ThrowsNullPointerException() {
    final byte[] key = {-91, -79, 67, -107, 9, 91, 62, -95, 80, 78};

    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> cryptoService.hmac(key, null));
  }

  @Test
  void hmac_ValidData_ReturnsHash() throws Exception {
    final byte[] key = {-91};
    final byte[] message = {120, 56, 111};
    final byte[] expectedHash = {46};

    Mac mockMac = mock(Mac.class);
    when(cryptoFactory.getHmac()).thenReturn(mockMac);

    SecretKeySpec mockSecretKeySpec = mock(SecretKeySpec.class);
    when(cryptoFactory.getHmacKey(key)).thenReturn(mockSecretKeySpec);

    when(mockMac.doFinal(message)).thenReturn(expectedHash);

    assertThat(cryptoService.hmac(key, message)).isEqualTo(expectedHash);
  }

  @BeforeEach
  private void setUp() {
    cryptoService = new CryptoService(cryptoFactory);
  }
}
