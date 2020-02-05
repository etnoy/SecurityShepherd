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
import org.springframework.stereotype.Service;
import com.google.common.primitives.Bytes;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@NoArgsConstructor
public class FlagHandlingService {

	@Autowired
	private SubmissionRepository submissionRepository;

	@NonNull
	private User submittingUser;

	@NonNull
	private Module submittedModule;

	private byte[] concatenatedKey;

	private static final Mac HMAC512 = initializeHMAC();

	public String generateFlag() {

		byte[] flag = HMAC512.doFinal(concatenatedKey);

		StringBuilder sb = new StringBuilder();
		for (byte b : flag) {
			sb.append(String.format("%02X", b));
		}

		return sb.toString();

	}

	public FlagHandlingService(User submittingUser, Module submittedModule) {

		this.submittingUser = submittingUser;
		this.submittedModule = submittedModule;

		byte[] userSolutionKey = this.submittingUser.getSolutionKey();
		byte[] moduleSolutionKey = this.submittedModule.getSolutionKey();

		if (this.submittedModule.isFixedSolutionKey()) {
			this.concatenatedKey = moduleSolutionKey;
		} else {
			this.concatenatedKey = Bytes.concat(userSolutionKey, moduleSolutionKey);
		}

	}

	public boolean submitFlag(String submittedFlag) {

		boolean isFlagValid = validate(submittedFlag);

		SubmissionBuilder submissionBuilder = Submission.builder();
		submissionBuilder.userId(submittingUser.getId());
		submissionBuilder.moduleId(submittedModule.getId());
		submissionBuilder.submittedFlag(submittedFlag);
		submissionBuilder.valid(isFlagValid);

		Submission newSubmission = submissionBuilder.build();

		System.out.println(submittedFlag);
		
		//submissionRepository.findAll();
		
		//submissionRepository.save(newSubmission);

		return isFlagValid;

	}

	private boolean validate(String submittedFlag) {

		return generateFlag().equals(submittedFlag);

	}

	private static Mac initializeHMAC() {

		Mac HMAC;

		try {
			HMAC = Mac.getInstance("HmacSHA512");
		} catch (NoSuchAlgorithmException e) {
			log.error("Could not initialize HMAC-SHA512");
			throw new RuntimeException(e);
		}

		byte serverKey[] = generateRandomBytes(16);

		SecretKeySpec keySpec = new SecretKeySpec(serverKey, "HmacSHA512");

		try {
			HMAC.init(keySpec);
		} catch (InvalidKeyException e) {
			log.error("Server key was invalid when initializing HMAC-SHA512");
			throw new RuntimeException(e);
		}

		return HMAC;
	}

	public static byte[] generateRandomBytes(int length) {

		SecureRandom strongPRNG;
		try {
			strongPRNG = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			log.error("Could not initialize PRNG");
			throw new RuntimeException(e);
		}

		byte[] returnedBytes = new byte[length];

		strongPRNG.nextBytes(returnedBytes);

		return returnedBytes;
	}

}