package org.owasp.securityshepherd.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component

@NoArgsConstructor
public class CryptoService {

	public byte[] generateRandomBytes(int numberOfBytes) {

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

}