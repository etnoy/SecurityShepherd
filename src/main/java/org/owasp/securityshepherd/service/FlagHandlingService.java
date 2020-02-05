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
import org.owasp.securityshepherd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.primitives.Bytes;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component

@NoArgsConstructor
public class FlagHandlingService {

	@Autowired
	private SubmissionRepository submissionRepository;

	@Autowired
	private UserRepository userRepository;

	private static final Mac HMAC512;

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

	public static String encryptFlag(byte[] solutionKey) {

		byte[] flag = HMAC512.doFinal(solutionKey);

		StringBuilder sb = new StringBuilder();
		for (byte b : flag) {
			sb.append(String.format("%02X", b));
		}

		return sb.toString();

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

		byte[] userSolutionKey = submittingUser.getSolutionKey();
		byte[] moduleSolutionKey = submittedModule.getSolutionKey();

		byte[] concatenatedKey;

		if (submittedModule.isFixedSolutionKey()) {
			concatenatedKey = moduleSolutionKey;
		} else {
			concatenatedKey = Bytes.concat(userSolutionKey, moduleSolutionKey);
		}

		String correctFlag = encryptFlag(concatenatedKey);

		boolean isFlagValid = submittedFlag.equals(correctFlag);

		SubmissionBuilder submissionBuilder = Submission.builder();
		submissionBuilder.userId(submittingUser.getId());
		submissionBuilder.moduleId(submittedModule.getId());
		submissionBuilder.submittedFlag(submittedFlag);
		submissionBuilder.valid(isFlagValid);

		Submission newSubmission = submissionBuilder.build();

		userRepository.findAll();

		submissionRepository.save(newSubmission);

		return isFlagValid;

	}

}