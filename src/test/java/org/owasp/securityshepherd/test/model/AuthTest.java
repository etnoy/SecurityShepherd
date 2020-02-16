package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Timestamp;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Auth;
import org.owasp.securityshepherd.model.Auth.AuthBuilder;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.SAMLAuth;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthTest {

	@Test
	public void build_NoArguments_AsExpected() {

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
	public void authBuildertoString_ValidData_AsExpected() {

		final AuthBuilder builder = Auth.builder();

		assertThat(builder.toString(), is(equalTo(
				"Auth.AuthBuilder(isEnabled$value=false, badLoginCount$value=0, isAdmin$value=false, suspendedUntil$value=null, suspensionMessage=null, accountCreated$value=null, lastLogin$value=null, lastLoginMethod=null, password=null, saml=null)")));

	}

	@Test
	public void equals_AutomaticTesting() {

		EqualsVerifier.forClass(Auth.class).verify();

	}

	@Test
	public void withLastLoginMethod_ValidLastLoginMethod_ChangesLastLoginMethod() {

		final Auth auth = Auth.builder().build();

		assertThat(auth.getLastLoginMethod(), is(nullValue()));

		final String[] testedLastLoginMethods = { null, "", "password", "saml", "ldap", "Long  With     Whitespace",
				"12345" };

		Auth changedAuth;
		for (String newLastLoginMethod : testedLastLoginMethods) {

			changedAuth = auth.withSuspensionMessage(newLastLoginMethod);
			assertThat(changedAuth.getSuspensionMessage(), is(equalTo(newLastLoginMethod)));

		}

	}

	@Test
	public void withSuspensionMessage_ValidSuspensionMessage_ChangesSuspensionMessage() {

		final Auth auth = Auth.builder().build();

		assertThat(auth.getSuspensionMessage(), is(nullValue()));

		final String[] testedSuspensionMessages = { null, "", "banned", "Long  With     Whitespace", "12345",
				"You tried to hack the server, fool!" };

		Auth changedAuth;
		for (String newSuspensionMessage : testedSuspensionMessages) {

			changedAuth = auth.withSuspensionMessage(newSuspensionMessage);
			assertThat(changedAuth.getSuspensionMessage(), is(equalTo(newSuspensionMessage)));

		}

	}

}