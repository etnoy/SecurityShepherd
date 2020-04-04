package org.owasp.securityshepherd.service;

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
  
  public Mono<byte[]> hmac(final byte[] key, final byte[] message) {
    if (key == null) {
      return Mono.error(new NullPointerException("Key cannot be null"));
    }

    if (message == null) {
      return Mono.error(new NullPointerException("Message cannot be null"));
    }

    final Mac hmac512;

    try {
      hmac512 = Mac.getInstance("HmacSHA512");
    } catch (NoSuchAlgorithmException e) {
      throw new CryptographicException("Could not initialize HMAC-SHA512", e);
    }

    final SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA512");

    try {
      hmac512.init(keySpec);
    } catch (InvalidKeyException e) {
      throw new CryptographicException("Key was invalid when initializing HMAC-SHA512", e);
    }

    return Mono.just(hmac512.doFinal(message));
  }
}
