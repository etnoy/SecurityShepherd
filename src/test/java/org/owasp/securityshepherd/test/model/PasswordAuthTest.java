package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.PasswordAuth.PasswordAuthBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PasswordAuthTest {

	private static final boolean[] BOOLEANS = { false, true };

	@Test
	public void builderToString_ValidData_AsExpected() {

		assertThat(PasswordAuth.builder().toString(), is(
				"PasswordAuth.PasswordAuthBuilder(loginName=null, hashedPassword=null, isPasswordExpired$value=false)"));

	}

	@Test
	public void buildHashedPassword_ValidHashedPassword_Builds() {

		final String[] hashedPasswordsToTest = { null, "abc123", "0xdeadbeef", "", "me@example.com", "a", "1",
				"password" };

		for (String hashedPassword : hashedPasswordsToTest) {

			final PasswordAuthBuilder builder = PasswordAuth.builder().loginName("TestUser");

			builder.hashedPassword(hashedPassword);

			assertThat(builder.build(), instanceOf(PasswordAuth.class));
			assertThat(builder.build().getHashedPassword(), is(hashedPassword));

		}

	}

	@Test
	public void buildIsPasswordExpired_TrueOrFalse_MatchesBuild() {

		for (boolean isPasswordNonExpired : BOOLEANS) {

			final PasswordAuthBuilder builder = PasswordAuth.builder().loginName("TestUser");

			builder.isPasswordNonExpired(isPasswordNonExpired);

			assertThat(builder.build(), instanceOf(PasswordAuth.class));
			assertThat(builder.build().isPasswordNonExpired(), is(isPasswordNonExpired));

		}

	}

	@Test
	public void buildLoginName_NullLoginname_ThrowsException() {

		assertThrows(NullPointerException.class, () -> PasswordAuth.builder().loginName(null));

	}

	@Test
	public void buildLoginName_ValidLoginName_Builds() {

		final String[] loginNamesToTest = { "", "me@example.com", "a", "1", "userName" };

		for (String loginName : loginNamesToTest) {

			final PasswordAuthBuilder builder = PasswordAuth.builder();

			builder.loginName(loginName);

			assertThat(builder.build(), instanceOf(PasswordAuth.class));
			assertThat(builder.build().getLoginName(), is(loginName));

		}

	}

	@Test
	public void equals_AutomaticTesting() {
		EqualsVerifier.forClass(PasswordAuth.class).withIgnoredAnnotations(NonNull.class).verify();
	}

	@Test
	public void toString_ValidData_AsExpected() {

		assertThat(PasswordAuth.builder().loginName("TestUser").build().toString(),
				is("PasswordAuth(loginName=TestUser, hashedPassword=null, isPasswordExpired=true)"));

	}

	@Test
	public void withHashedPassword_ValidHashedPassword_ChangesHashedPassword() {

		final PasswordAuth passwordAuth = PasswordAuth.builder().loginName("abc123hash").build();

		final String[] testedHashedPasswords = { "abc123hash", null, "", "!\"+,-", "userName",
				"Long  With     Whitespace", "12345" };

		for (String hashedPassword : testedHashedPasswords) {

			final PasswordAuth changedAuth = passwordAuth.withHashedPassword(hashedPassword);
			assertThat(changedAuth.getHashedPassword(), is(hashedPassword));

		}

	}

	@Test
	public void withLoginName_NullLoginName_ThrowsException() {

		assertThrows(NullPointerException.class,
				() -> PasswordAuth.builder().loginName("Test").build().withLoginName(null));

	}

	@Test
	public void withLoginName_ValidLoginName_ChangesLoginName() {

		final PasswordAuth passwordAuth = PasswordAuth.builder().loginName("me@example.com").build();

		final String[] testedLoginNames = { "me@example.com", "", "userName", "Long  With     Whitespace", "12345" };

		for (String newLoginName : testedLoginNames) {

			final PasswordAuth changedAuth = passwordAuth.withLoginName(newLoginName);
			assertThat(changedAuth.getLoginName(), is(newLoginName));

		}

	}

	@Test
	public void withPasswordExpired_ValidBoolean_ChangesPasswordExpired() {

		final PasswordAuth testPasswordAuth = PasswordAuth.builder().loginName("TestUser").build();

		final PasswordAuth changedPasswordAuth1 = testPasswordAuth.withPasswordNonExpired(false);
		assertThat(changedPasswordAuth1.isPasswordNonExpired(), is(false));
		final PasswordAuth changedPasswordAuth2 = testPasswordAuth.withPasswordNonExpired(true);
		assertThat(changedPasswordAuth2.isPasswordNonExpired(), is(true));

	}

}