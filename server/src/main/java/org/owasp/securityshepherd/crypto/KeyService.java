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
package org.owasp.securityshepherd.crypto;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.owasp.securityshepherd.exception.RngException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

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

  public String byteFlagToString(final byte[] bytes) {
    return Hex.encodeHexString(bytes, true);
  }

  public byte[] stringFlagToByte(final String stringFlag) throws DecoderException {
    return Hex.decodeHex(stringFlag);
  }

  public byte[] convertStringFlagToBytes(final String keyString) {
    return keyString.getBytes();
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
