package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.User.UserBuilder;
import org.owasp.securityshepherd.model.Auth;

import org.owasp.securityshepherd.model.Auth.AuthBuilder;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserTest {

	@Test
	public void build_NoArguments_ThrowsException() {

		final UserBuilder builder = User.builder();

		assertThrows(NullPointerException.class, () -> builder.build());

	}

	@Test
	public void buildDisplayName_ValidId_Builds() {

		final String displayName = "Test User";

		final UserBuilder builder = User.builder();

		builder.id(12345);
		builder.displayName(displayName);

		assertThat(builder.build(), instanceOf(User.class));

		assertThat(builder.build().getId(), is(12345));

	}

	@Test
	public void buildDisplayName_ValidDisplayName_Builds() {

		final String displayName = "buildDisplayName_ValidDisplayName";

		final UserBuilder builder = User.builder();
		builder.displayName(displayName);

		assertThat(builder.build(), instanceOf(User.class));

		assertThat(builder.build().getDisplayName(), is(equalTo(displayName)));

	}

	@Test
	public void buildDisplayName_NullDisplayName_ThrowsException() {

		final UserBuilder builder = User.builder();

		assertThrows(NullPointerException.class, () -> builder.displayName(null));

	}

	@Test
	public void buildClassId_ValidClassId_Builds() {

		final UserBuilder builder = User.builder().displayName("TestUser");

		builder.classId(1);

		final User newUser = builder.build();

		assertThat(newUser.getClassId(), is(1));

	}

	@Test
	public void build_ValidDisplayName_ReturnsUser() {

		final String validDisplayName = "build_ValidDisplayName";

		final User build_ValidDisplayNameLengthUser = User.builder().displayName(validDisplayName).build();

		assertThat(build_ValidDisplayNameLengthUser, instanceOf(User.class));

		assertThat(build_ValidDisplayNameLengthUser.getDisplayName(), is(equalTo(validDisplayName)));
	}

	@Test
	public void build_ZeroArguments_DefaultValuesPresent() {

		final User buildZeroArgumentsUser = User.builder().displayName("build_ZeroArguments").build();

		assertThat(buildZeroArgumentsUser.getId(), is(notNullValue()));
		assertThat(buildZeroArgumentsUser.getClassId(), is(nullValue()));
		assertThat(buildZeroArgumentsUser.getDisplayName(), is(notNullValue()));
		assertThat(buildZeroArgumentsUser.getEmail(), is(nullValue()));

	}

	@Test
	public void equals_AutomaticTesting() {
		
		EqualsVerifier.forClass(User.class).withOnlyTheseFields("id").verify();
		
	}

	@Test
	public void userBuildertoString_ValidData_NotNull() {
		final UserBuilder builder = User.builder();

		assertThat(builder.toString(), is(equalTo(
				"User.UserBuilder(id=0, displayName=null, classId$value=null, email=null, key=null, auth=null)")));

		builder.id(12345);
		builder.displayName("Test User");
		builder.classId(6789);
		builder.email("me@example.com");

		assertThat(builder.toString(), is(equalTo(
				"User.UserBuilder(id=12345, displayName=Test User, classId$value=6789, email=me@example.com, key=null, auth=null)")));

	}

	@Test
	public void withDisplayName_ValidDisplayName_ChangesDisplayName() {

		final String displayName = "withDisplayName_ValidDisplayName";

		final User newUser = User.builder().displayName(displayName).build();

		assertThat(newUser.getDisplayName(), is(equalTo(displayName)));

		final String[] testedDisplayNames = { displayName, "", "newUser", "Long  With     Whitespace", "12345" };

		User changedUser;
		for (String newDisplayName : testedDisplayNames) {

			changedUser = newUser.withDisplayName(newDisplayName);
			assertThat(changedUser.getDisplayName(), is(equalTo(newDisplayName)));
			assertThat(changedUser, is(equalTo(newUser)));

		}

	}

	@Test
	public void withDisplayName_NullDisplayName_ThrowsException() {

		final String displayName = "withDisplayName_NullDisplayName";

		final User newUser = User.builder().displayName(displayName).build();

		assertThat(newUser.getDisplayName(), is(equalTo(displayName)));

		assertThrows(NullPointerException.class, () -> newUser.withDisplayName(null));

	}

	@Test
	public void withClassId_ValidClassId_ChangesClassId() {

		final String displayName = "withClassId_ValidClassId";

		final int[] testedClassIds = { 0, 1, -1, 1000, -1000, 123456789 };

		final User newUser = User.builder().displayName(displayName).classId(0).build();

		assertThat(newUser.getClassId(), is(0));

		User changedUser;

		for (int newClassId : testedClassIds) {

			changedUser = newUser.withClassId(newClassId);
			assertThat(changedUser.getClassId(), is(newClassId));
			assertThat(changedUser, is(equalTo(newUser)));

		}

	}

	@Test
	public void withId_ValidId_ChangesId() {

		final int originalId = 1;
		final int[] testedIds = { originalId, 0, -1, 1000, -1000, 123456789 };

		final User newUser = User.builder().id(originalId).displayName("Test User").build();

		assertThat(newUser.getId(), is(originalId));

		User changedUser;

		for (int newId : testedIds) {

			changedUser = newUser.withClassId(newId);
			assertThat(changedUser.getClassId(), is(newId));
			assertThat(changedUser, is(equalTo(newUser)));

		}

	}

	@Test
	public void withEmail_ValidEmail_ChangesEmail() {

		final String displayName = "withEmail_ValidEmail";

		final String email = "validEmail@example.com";

		final String[] testedStrings = { email, null, "", "newEmail@example.com", "e@e", "a", "alongemail@example.com" };

		final User newUser = User.builder().displayName(displayName).email(email).build();

		assertThat(newUser.getEmail(), is(equalTo(email)));

		User changedUser;

		for (String newEmail : testedStrings) {

			changedUser = newUser.withEmail(newEmail);
			assertThat(changedUser.getEmail(), is(equalTo(newEmail)));
			assertThat(changedUser, is(equalTo(newUser)));

		}

	}

	@Test
	public void withKey_ValidKey_ChangesKey() {

		final String displayName = "withKey_ValidKey";

		final byte[] key = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

		final byte[][] testedKeys = { key, null, {}, { 1 }, { 19, 26, 127, -128 } };

		final User newUser = User.builder().displayName(displayName).key(key).build();

		assertThat(newUser.getKey(), is(equalTo(key)));

		User changedUser;

		for (byte[] newKey : testedKeys) {

			changedUser = newUser.withKey(newKey);
			assertThat(changedUser.getKey(), is(equalTo(newKey)));
			assertThat(changedUser, is(equalTo(newUser)));

		}

	}

	@Test
	public void withAuth_ValidAuth_ChangesAuth() {

		final AuthBuilder authBuilder = Auth.builder();

		final Auth originalAuth = authBuilder.build();

		final User newUser = User.builder().displayName("Test User").auth(originalAuth).build();

		assertThat(newUser.getAuth(), is(equalTo(originalAuth)));

		final AuthBuilder[] testedAuthBuilders = { authBuilder, authBuilder.isAdmin(true), authBuilder.badLoginCount(3) };

		User changedUser;
		Auth newAuth;

		for (AuthBuilder newBuilder : testedAuthBuilders) {

			newAuth = newBuilder.build();
			changedUser = newUser.withAuth(newAuth);
			assertThat(changedUser.getAuth(), is(equalTo(newAuth)));
			assertThat(changedUser, is(equalTo(newUser)));

		}

	}

}