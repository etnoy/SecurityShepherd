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

package org.owasp.securityshepherd.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.owasp.securityshepherd.exception.CryptographicException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public final class CryptoService {
  private final CryptoFactory cryptoFactory;

  public byte[] hmac(final byte[] key, final byte[] message) {
    if (key == null) {
      throw new NullPointerException("Key cannot be null");
    }
    if (message == null) {
      throw new NullPointerException("Message cannot be null");
    }

    final Mac hmac512;

    try {
      hmac512 = cryptoFactory.getHmac();
    } catch (NoSuchAlgorithmException e) {
      throw new CryptographicException("Could not initialize MAC algorithm", e);
    }

    SecretKeySpec secretKeySpec = cryptoFactory.getHmacKey(key);

    try {
      hmac512.init(secretKeySpec);
    } catch (InvalidKeyException e) {
      throw new CryptographicException("Invalid key supplied to MAC", e);
    }

    return hmac512.doFinal(message);
  }
}
