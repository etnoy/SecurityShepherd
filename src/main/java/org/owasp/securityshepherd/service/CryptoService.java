package org.owasp.securityshepherd.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.owasp.securityshepherd.exception.CryptographicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;

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
			throw new CryptographicException("Could not initialize HMAC-SHA512", e);
		}

		SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA512");

		try {
			hmac512.init(keySpec);
		} catch (InvalidKeyException e) {
			throw new CryptographicException("Key was invalid when initializing HMAC-SHA512", e);
		}

		return hmac512.doFinal(message);
	}

}