package org.owasp.securityshepherd.it.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.repository.UserRepository;
import org.owasp.securityshepherd.service.FlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.primitives.Bytes;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FlagHandlingIT {

	@Autowired
	FlagService flagService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModuleRepository moduleRepository;

	@Autowired
	private SubmissionRepository submissionRepository;

	@Test
	public void validateFlag_ValidFlag_SavesResult() {

		User submittingUser = userRepository.save(User.builder().name("validateFlag_ValidFlag_user").build());

		Module submittedModule = moduleRepository.save(Module.builder().name("validateFlag_ValidFlag_module").build());

		String generatedFlag = FlagService.generateFlag(submittingUser, submittedModule);

		assertTrue(flagService.submitFlag(submittingUser, submittedModule, generatedFlag));

		assertEquals(1, submissionRepository.count());
		
		submissionRepository.findAll();

	}

}