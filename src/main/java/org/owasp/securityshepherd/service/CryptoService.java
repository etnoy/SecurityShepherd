package org.owasp.securityshepherd.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Service
public final class CryptoService {
	
	@Autowired
	KeyService keyService;

	public byte[] hmac(final byte[] key, final byte[] message) {
		
		final Mac hmac512;
		
		try {
			hmac512 = Mac.getInstance("HmacSHA512");
		} catch (NoSuchAlgorithmException e) {
			log.error("Could not initialize HMAC-SHA512");
			throw new RuntimeException(e);
		}

		SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA512");

		try {
			hmac512.init(keySpec);
		} catch (InvalidKeyException e) {
			log.error("Server key was invalid when initializing HMAC-SHA512");
			throw new RuntimeException(e);
		}
		
		return hmac512.doFinal(message);
	}

}