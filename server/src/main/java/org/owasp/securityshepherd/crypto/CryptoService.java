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
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public final class CryptoService {
  public Mono<byte[]> hmac(final byte[] key, final byte[] message, final String algorithm) {
    if (key == null) {
      return Mono.error(new NullPointerException("Key cannot be null"));
    }
    if (message == null) {
      return Mono.error(new NullPointerException("Message cannot be null"));
    }

    final Mac hmac512;

    try {
      hmac512 = Mac.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      return Mono.error(new CryptographicException("Could not initialize MAC algorithm", e));
    }

    final SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);

    try {
      hmac512.init(keySpec);
    } catch (InvalidKeyException e) {
      throw new CryptographicException("Invalid key supplied to MAC", e);
    }

    return Mono.just(hmac512.doFinal(message));
  }

  public Mono<byte[]> hmac(final byte[] key, final byte[] message) {
    return hmac(key, message, "HmacSHA512");
  }
}
