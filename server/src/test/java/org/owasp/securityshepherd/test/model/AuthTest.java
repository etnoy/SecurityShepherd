package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.persistence.model.Auth;
import org.owasp.securityshepherd.persistence.model.PasswordAuth;
import org.owasp.securityshepherd.persistence.model.SAMLAuth;
import org.owasp.securityshepherd.persistence.model.Auth.AuthBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthTest {

	private static final boolean[] BOOLEANS = { false, true };

	@Test
	public void authBuildertoString_ValidData_AsExpected() {

		final AuthBuilder builder = Auth.builder();

		assertThat(builder.toString(), is(
				"Auth.AuthBuilder(id=0, isEnabled$value=false, badLoginCount$value=0, isAdmin$value=false, suspendedUntil$value=null, suspensionMessage=null, accountCreated$value=null, lastLogin$value=null, lastLoginMethod=null)"));

	}

	@Test
	public void buildAccountCreated_ValidTime_Builds() {

		final int[] timesToTest = { 0, 1, 2, 1000, 4000, 1581806000, 42 };

		for (int accountCreated : timesToTest) {

			final AuthBuilder builder = Auth.builder();

			builder.accountCreated(new Timestamp(accountCreated));

			assertThat(builder.build(), instanceOf(Auth.class));
			assertThat(builder.build().getAccountCreated(), is(new Timestamp(accountCreated)));

		}

	}

	@Test
	public void buildBadLoginCount_ValidBadLoginCount_Builds() {

		final int[] badLoginCountsToTest = { 0, 1, 1000, -1, 999999, 42, 567890, -1234567 };

		for (int badLoginCount : badLoginCountsToTest) {

			final AuthBuilder builder = Auth.builder();

			builder.badLoginCount(badLoginCount);

			assertThat(builder.build(), instanceOf(Auth.class));
			assertThat(builder.build().getBadLoginCount(), is(badLoginCount));

		}

	}

	@Test
	public void buildIsAdmin_TrueOrFalse_MatchesBuild() {

		for (boolean isAdmin : BOOLEANS) {

			final AuthBuilder builder = Auth.builder();

			builder.isAdmin(isAdmin);

			assertThat(builder.build(), instanceOf(Auth.class));
			assertThat(builder.build().isAdmin(), is(isAdmin));

		}

	}

	@Test
	public void buildIsEnabled_TrueOrFalse_MatchesBuild() {

		for (boolean isEnabled : BOOLEANS) {

			final AuthBuilder builder = Auth.builder();

			builder.isEnabled(isEnabled);

			assertThat(builder.build(), instanceOf(Auth.class));
			assertThat(builder.build().isEnabled(), is(isEnabled));

		}

	}

	@Test
	public void buildLastLogin_ValidTime_Builds() {

		final int[] timesToTest = { 0, 1, 2, 1000, 4000, 1581806000, 42 };

		for (int lastLogin : timesToTest) {

			final AuthBuilder builder = Auth.builder();

			builder.lastLogin(new Timestamp(lastLogin));

			assertThat(builder.build(), instanceOf(Auth.class));
			assertThat(builder.build().getLastLogin(), is(new Timestamp(lastLogin)));

		}

	}

	@Test
	public void buildLastLoginMethod_ValidLastLoginMethod_Builds() {

		final String[] lastLoginMethodsToTest = { "password", "saml", "ldap", null, "", "a", "login method with spaces",
				"_+^" };

		for (String lastLoginMethod : lastLoginMethodsToTest) {

			final AuthBuilder builder = Auth.builder();

			builder.lastLoginMethod(lastLoginMethod);

			assertThat(builder.build(), instanceOf(Auth.class));
			assertThat(builder.build().getLastLoginMethod(), is(lastLoginMethod));

		}

	}

	@Test
	public void buildSuspendedUntil_ValidTime_Builds() {

		final int[] timesToTest = { 0, 1, 2, 1000, 4000, 1581806000, 42 };

		for (int suspendedUntil : timesToTest) {

			final AuthBuilder builder = Auth.builder();

			builder.suspendedUntil(new Timestamp(suspendedUntil));

			assertThat(builder.build(), instanceOf(Auth.class));
			assertThat(builder.build().getSuspendedUntil(), is(new Timestamp(suspendedUntil)));

		}

	}

	@Test
	public void buildSuspensionMessage_ValidSuspensionMessage_Builds() {

		final String[] suspensionMessagesToTest = { "You are suspended!", null, "", "banned",
				"Long  With     Whitespace", "12345", "You tried to hack the server, fool!" };

		for (String suspensionMessage : suspensionMessagesToTest) {

			final AuthBuilder builder = Auth.builder();

			builder.suspensionMessage(suspensionMessage);

			assertThat(builder.build(), instanceOf(Auth.class));
			assertThat(builder.build().getSuspensionMessage(), is(suspensionMessage));

		}

	}

	@Test
	public void equals_AutomaticTesting() {

		EqualsVerifier.forClass(Auth.class).verify();

	}

	@Test
	public void toString_ValidData_AsExpected() {

		final Auth testAuth = Auth.builder().build();

		assertThat(testAuth.toString(), is(
				"Auth(id=0, isEnabled=false, badLoginCount=0, isAdmin=false, suspendedUntil=null, suspensionMessage=null, accountCreated=null, lastLogin=null, lastLoginMethod=null)"));

	}

	@Test
	public void withLastLoginMethod_ValidLastLoginMethod_ChangesLastLoginMethod() {

		final Auth auth = Auth.builder().build();

		assertThat(auth.getLastLoginMethod(), is(nullValue()));

		final String[] testedLastLoginMethods = { null, "", "password", "saml", "ldap", "Long  With     Whitespace",
				"12345" };

		for (String newLastLoginMethod : testedLastLoginMethods) {

			final Auth changedAuth = auth.withLastLoginMethod(newLastLoginMethod);
			assertThat(changedAuth.getLastLoginMethod(), is(newLastLoginMethod));

		}

	}

	@Test
	public void withSuspensionMessage_ValidSuspensionMessage_ChangesSuspensionMessage() {

		final Auth auth = Auth.builder().build();

		assertThat(auth.getSuspensionMessage(), is(nullValue()));

		final String[] testedSuspensionMessages = { null, "", "banned", "Long  With     Whitespace", "12345",
				"You tried to hack the server, fool!" };

		for (String newSuspensionMessage : testedSuspensionMessages) {

			final Auth changedAuth = auth.withSuspensionMessage(newSuspensionMessage);
			assertThat(changedAuth.getSuspensionMessage(), is(newSuspensionMessage));

		}

	}

	@Test
	public void withBadLoginCount_ValidBadLoginCount_ChangesBadLoginCount() {

		final int originalBadLoginCount = 0;
		final int[] testedBadLoginCounts = { originalBadLoginCount, 1, -1, 1000, -1000, 123456789, -12346789 };

		final Auth testAuth = Auth.builder().build();

		for (int badLoginCount : testedBadLoginCounts) {

			final Auth changedAuth = testAuth.withBadLoginCount(badLoginCount);
			assertThat(changedAuth.getBadLoginCount(), is(badLoginCount));

		}

	}

	@Test
	public void withAdmin_ValidBoolean_ChangesIsAdmin() {

		final Auth testAuth = Auth.builder().build();

		for (boolean isAdmin : BOOLEANS) {

			final Auth changedAuth = testAuth.withAdmin(isAdmin);
			assertThat(changedAuth.isAdmin(), is(isAdmin));

		}

	}

	@Test
	public void withEnabled_ValidBoolean_ChangesIsEnabled() {

		final Auth testAuth = Auth.builder().build();

		for (boolean isEnabled : BOOLEANS) {

			final Auth changedAuth = testAuth.withEnabled(isEnabled);
			assertThat(changedAuth.isEnabled(), is(isEnabled));

		}

	}

	@Test
	public void withAccountCreated_ValidTime_ChangesAccountCreationTime() {

		final Timestamp originalTime = new Timestamp(0);

		final Timestamp[] timesToTest = { originalTime, new Timestamp(1), new Timestamp(2), new Timestamp(1000),
				new Timestamp(4000), new Timestamp(1581806000), new Timestamp(42) };

		final Auth testAuth = Auth.builder().accountCreated(originalTime).build();

		for (Timestamp time : timesToTest) {

			final Auth changedAuth = testAuth.withAccountCreated(time);

			assertThat(changedAuth.getAccountCreated(), is(time));

		}

	}

	@Test
	public void withLastLogin_ValidTime_ChangesLastLoginTime() {

		final Timestamp originalTime = new Timestamp(0);

		final Timestamp[] timesToTest = { originalTime, new Timestamp(1), new Timestamp(2), new Timestamp(1000),
				new Timestamp(4000), new Timestamp(1581806000), new Timestamp(42) };

		final Auth testAuth = Auth.builder().lastLogin(originalTime).build();

		for (Timestamp time : timesToTest) {

			final Auth changedAuth = testAuth.withLastLogin(time);

			assertThat(changedAuth.getLastLogin(), is(time));

		}

	}

	@Test
	public void withSuspendedUntil_ValidTime_ChangesSuspendedUntilTime() {

		final Timestamp originalTime = new Timestamp(0);

		final Timestamp[] timesToTest = { originalTime, new Timestamp(1), new Timestamp(2), new Timestamp(1000),
				new Timestamp(4000), new Timestamp(1581806000), new Timestamp(42) };

		final Auth testAuth = Auth.builder().suspendedUntil(originalTime).build();

		for (Timestamp time : timesToTest) {

			final Auth changedAuth = testAuth.withSuspendedUntil(time);

			assertThat(changedAuth.getSuspendedUntil(), is(time));

		}

	}

}