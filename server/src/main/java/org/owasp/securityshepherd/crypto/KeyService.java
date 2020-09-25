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
package org.owasp.securityshepherd.crypto;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.owasp.securityshepherd.exception.RngException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public final class KeyService {
  private final CryptoFactory cryptoFactory;

  private byte[] byteGenerator(final SecureRandom strongPRNG, final int numberOfBytes) {
    byte[] randomBytes = new byte[numberOfBytes];
    strongPRNG.nextBytes(randomBytes);
    return randomBytes;
  }

  public String convertByteKeyToString(final byte[] keyBytes) {
    return new String(keyBytes, StandardCharsets.US_ASCII);
  }

  public String bytesToHexString(final byte[] bytes) {
    return Hex.encodeHexString(bytes, true);
  }

  public byte[] hexStringToBytes(final String stringFlag) throws DecoderException {
    return Hex.decodeHex(stringFlag);
  }

  public byte[] generateRandomBytes(final int numberOfBytes) {
    try {
      final SecureRandom prng = cryptoFactory.getPrng();
      return byteGenerator(prng, numberOfBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new RngException("Could not initialize PRNG", e);
    }
  }

  public String generateRandomString(final int numberOfBytes) {
    return convertByteKeyToString(generateRandomBytes(numberOfBytes));
  }
}
