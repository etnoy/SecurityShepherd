package org.owasp.securityshepherd.service;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Service
public final class KeyService {

	public byte[] generateRandomBytes(final int numberOfBytes) {

		SecureRandom strongPRNG;
		try {
			strongPRNG = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			log.error("Could not initialize PRNG");
			throw new RuntimeException(e);
		}

		byte[] returnedBytes = new byte[numberOfBytes];

		strongPRNG.nextBytes(returnedBytes);

		return returnedBytes;
	}

	public byte[] convertStringKeyToBytes(final String keyString) {

		return keyString.getBytes();

	}

	public String convertByteKeyToString(final byte[] keyBytes) {

		return new String(keyBytes, StandardCharsets.UTF_16);

	}

	public String generateRandomString(final int numberOfBytes) {
		
		return convertByteKeyToString(generateRandomBytes(numberOfBytes));
		
	}

}