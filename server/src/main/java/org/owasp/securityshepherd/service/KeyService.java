package org.owasp.securityshepherd.service;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.owasp.securityshepherd.exception.RNGException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public final class KeyService {

	private Mono<byte[]> byteGenerator(final SecureRandom strongPRNG, final int numberOfBytes) {

		byte[] randomBytes = new byte[numberOfBytes];
		strongPRNG.nextBytes(randomBytes);
		return Mono.just(randomBytes);
	}

	public String convertByteKeyToString(final byte[] keyBytes) {

		return new String(keyBytes, StandardCharsets.UTF_16);

	}

	public byte[] convertStringKeyToBytes(final String keyString) {

		return keyString.getBytes();

	}

	public Mono<byte[]> generateRandomBytes(final int numberOfBytes) {

		try {
			return Mono.just(SecureRandom.getInstanceStrong()).flatMap(prng -> byteGenerator(prng, numberOfBytes));
		} catch (NoSuchAlgorithmException e) {
			return Mono.error(new RNGException("Could not initialize PRNG", e));
		}

	}

	public Mono<String> generateRandomString(final int numberOfBytes) {

		return generateRandomBytes(numberOfBytes).map(this::convertByteKeyToString);

	}

}