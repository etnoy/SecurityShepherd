package org.owasp.securityshepherd.it.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.owasp.securityshepherd.repository.UserRepository;
import org.owasp.securityshepherd.service.FlagHandlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class FlagHandlingIT {

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

		FlagHandlingService flagHandler = new FlagHandlingService(submittingUser, submittedModule);

		assertTrue(flagHandler.submitFlag(flagHandler.generateFlag()));

	}

}