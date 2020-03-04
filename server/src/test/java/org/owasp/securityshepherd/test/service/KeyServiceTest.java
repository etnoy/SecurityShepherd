package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class KeyServiceTest {

	@Autowired
	private KeyService keyService;

	@Test
	public void convertByteKeyToString_ValidInput_ReturnsExpectedOutput() throws Exception {

		assertThat(keyService.convertStringKeyToBytes("thisisaflag"),
				is(new byte[] { 116, 104, 105, 115, 105, 115, 97, 102, 108, 97, 103 }));

	}
	
	@Test
	public void convertStringKeyToBytes_ValidInput_ReturnsExpectedOutput() throws Exception {

		assertThat(
				keyService.convertByteKeyToString(new byte[] { 116, 104, 105, 115, 105, 115, 97, 102, 108, 97, 103 }),
				is("thisisaflag"));

	}

	@Test
	public void generateRandomBytes_ValidLength_ReturnsRandomBytes() throws Exception {

		final int[] testedLengths = { 0, 1, 12, 16, 128, 4096 };

		for (int length : testedLengths) {

			StepVerifier.create(keyService.generateRandomBytes(length)).assertNext(randomBytes -> {

				assertThat(randomBytes, is(notNullValue()));
				assertThat(randomBytes.length, is(length));

			}).expectComplete().verify();
		}
	}

	@Test
	public void generateRandomString_ValidLength_ReturnsRandomString() throws Exception {

		final int[] testedLengths = { 0, 1, 12, 16, 128, 4096 };

		for (int length : testedLengths) {

			StepVerifier.create(keyService.generateRandomString(length)).assertNext(randomBytes -> {

				assertThat(randomBytes, is(notNullValue()));
				assertThat(randomBytes.length(), is(length));

			}).expectComplete().verify();
		}
	}

	@BeforeEach
	private void setUp() {
		// Print more verbose errors if something goes wrong
		Hooks.onOperatorDebug();

	}

}