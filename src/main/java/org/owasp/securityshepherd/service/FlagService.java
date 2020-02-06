package org.owasp.securityshepherd.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.model.Submission.SubmissionBuilder;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.primitives.Bytes;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component

@NoArgsConstructor
public class FlagService {

	@Autowired
	private SubmissionRepository submissionRepository;

	private static final Mac HMAC512;

	private static final String flagPattern = "flag{%s}";

	static {

		try {
			HMAC512 = Mac.getInstance("HmacSHA512");
		} catch (NoSuchAlgorithmException e) {
			log.error("Could not initialize HMAC-SHA512");
			throw new RuntimeException(e);
		}

		byte serverKey[] = generateRandomBytes(16);

		SecretKeySpec keySpec = new SecretKeySpec(serverKey, "HmacSHA512");

		try {
			HMAC512.init(keySpec);
		} catch (InvalidKeyException e) {
			log.error("Server key was invalid when initializing HMAC-SHA512");
			throw new RuntimeException(e);
		}

	}

	public static String generateFlag(User user, Module module) {

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

	public static byte[] generateFlagKey() {

		return generateRandomBytes(16);

	}

	public static byte[] generateRandomBytes(int numberOfBytes) {

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

	public boolean submitFlag(User submittingUser, Module submittedModule, String submittedFlag) {

		log.debug("Submitted flag: " + submittedFlag);
		
		String correctFlag = generateFlag(submittingUser, submittedModule);
		
		log.debug("Correct flag: " + correctFlag);
		
		boolean isFlagValid = submittedFlag.equals(correctFlag);

		SubmissionBuilder submissionBuilder = Submission.builder();
		submissionBuilder.userId(submittingUser.getId());
		submissionBuilder.moduleId(submittedModule.getId());
		submissionBuilder.submittedFlag(submittedFlag);
		submissionBuilder.valid(isFlagValid);

		Submission newSubmission = submissionBuilder.build();
		
		log.trace("Submitting " + newSubmission.toString());

		submissionRepository.save(newSubmission);

		return isFlagValid;

	}

}