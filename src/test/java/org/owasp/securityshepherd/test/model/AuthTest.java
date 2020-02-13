package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Auth;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.Auth.AuthBuilder;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthTest {

	@Test
	public void build_NoArguments_ThrowsException() {

		final AuthBuilder builder = Auth.builder();

		final Auth testAuth = builder.build();

		assertThat(testAuth.isEnabled(), is(false));
		assertThat(testAuth.getBadLoginCount(), is(0));
		assertThat(testAuth.isAdmin(), is(false));
		assertThat(testAuth.getSuspendedUntil(), is(nullValue()));
		assertThat(testAuth.getSuspensionMessage(), is(nullValue()));
		assertThat(testAuth.getAccountCreated(), is(nullValue()));
		assertThat(testAuth.getLastLogin(), is(nullValue()));
		assertThat(testAuth.getLastLoginMethod(), is(nullValue()));
		assertThat(testAuth.getPassword(), is(nullValue()));
		assertThat(testAuth.getSaml(), is(nullValue()));
		
	}
	
	@Test
	public void equals_AutomaticTesting() {
		
		EqualsVerifier.forClass(Auth.class).verify();
		
	}

}