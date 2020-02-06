package org.owasp.securityshepherd.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.primitives.Bytes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component

public class FlagService {

	@Autowired
	private CryptoService cryptoService;

	private final Mac HMAC512;

	private final String flagPattern = "flag{%s}";
	
	FlagService() {

		try {
			HMAC512 = Mac.getInstance("HmacSHA512");
		} catch (NoSuchAlgorithmException e) {
			log.error("Could not initialize HMAC-SHA512");
			throw new RuntimeException(e);
		}

		byte[] serverKey = cryptoService.generateRandomBytes(16);

		SecretKeySpec keySpec = new SecretKeySpec(serverKey, "HmacSHA512");

		try {
			HMAC512.init(keySpec);
		} catch (InvalidKeyException e) {
			log.error("Server key was invalid when initializing HMAC-SHA512");
			throw new RuntimeException(e);
		}

	}

	public String generateFlag(User user, Module module) {

		byte[] moduleSolutionKey = module.getFlagKey();
		byte[] flagKey;
		String flagFormat;

		if (module.isStaticFlag()) {
			flagKey = moduleSolutionKey;
			flagFormat = "%s";
		} else {
			flagKey = Bytes.concat(user.getFlagKey(), moduleSolutionKey);
			flagFormat = flagPattern;
		}

		log.trace("flagKey: " + flagKey);

		byte[] hashedFlag = HMAC512.doFinal(flagKey);

		StringBuilder sb = new StringBuilder();
		for (byte b : hashedFlag) {
			sb.append(String.format("%02X", b));
		}

		return String.format(flagFormat, sb.toString());

	}

	public byte[] generateFlagKey() {

		return cryptoService.generateRandomBytes(16);

	}

	public boolean validateFlag(User submittingUser, Module submittedModule, String submittedFlag) {

		log.debug("Submitted flag: " + submittedFlag);

		String correctFlag = generateFlag(submittingUser, submittedModule);

		log.debug("Correct flag: " + correctFlag);

		return submittedFlag.equals(correctFlag);

	}

}