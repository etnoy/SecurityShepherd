/**
 * This file is part of Security Shepherd.
 *
 * <p>Security Shepherd is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with Security
 * Shepherd. If not, see <http://www.gnu.org/licenses/>.
 */
package org.owasp.securityshepherd.it.module.csrf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.owasp.securityshepherd.module.ModuleService;
import org.owasp.securityshepherd.module.csrf.CsrfAttackRepository;
import org.owasp.securityshepherd.module.csrf.CsrfService;
import org.owasp.securityshepherd.module.csrf.CsrfTutorial;
import org.owasp.securityshepherd.module.csrf.CsrfTutorialResult;
import org.owasp.securityshepherd.scoring.ScoreService;
import org.owasp.securityshepherd.scoring.SubmissionService;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = { "application.runner.enabled=false" })
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("CsrfTutorial integration test")
class CsrfTutorialIT {

	@Autowired
	CsrfService csrfService;

	@Autowired
	CsrfTutorial csrfTutorial;

	@Autowired
	CsrfAttackRepository csrfAttackRespository;

	@Autowired
	TestUtils testUtils;

	@Autowired
	UserService userService;

	@Autowired
	ModuleService moduleService;

	@Autowired
	SubmissionService submissionService;

	@Autowired
	ScoreService scoreService;

	@BeforeAll
	private static void reactorVerbose() {
		// Tell Reactor to print verbose error messages
		Hooks.onOperatorDebug();
	}

	@Test
	void activate_NonExistentPseudonym_ReturnsError() {
		final Long userId = userService.create("Attacker").block();

		StepVerifier.create(csrfTutorial.attack(userId, "Unknown target ID")).assertNext(result -> {
			assertThat(result.getMessage()).isNull();
			assertThat(result.getError()).isEqualTo("Unknown target ID");
		}).expectComplete().verify();
	}

	@Test
	void getTutorial_CorrectAttack_Success() {
		final Long userId1 = userService.create("TestUser1").block();
		final Long userId2 = userService.create("TestUser2").block();

		final CsrfTutorialResult tutorialResult = csrfTutorial.getTutorial(userId1).block();

		csrfTutorial.attack(userId2, tutorialResult.getPseudonym()).block();

		StepVerifier.create(csrfTutorial.getTutorial(userId1)).assertNext(result -> {
			assertThat(result.getFlag()).isNotNull();
			assertThat(result.getError()).isNull();
		}).expectComplete().verify();
	}

	@BeforeEach
	private void clear() {
		testUtils.deleteAll().block();
		csrfTutorial.initialize().block();
	}

	@Test
	void getTutorial_SelfActivation_NotAllowed() {
		final Long userId = userService.create("TestUser").block();

		final CsrfTutorialResult tutorialResult = csrfTutorial.getTutorial(userId).block();

		StepVerifier.create(csrfTutorial.attack(userId, tutorialResult.getPseudonym())).assertNext(result -> {
			assertThat(result.getMessage()).isNull();
			assertThat(result.getError()).isEqualTo("You cannot activate yourself");
		}).expectComplete().verify();
	}
}
