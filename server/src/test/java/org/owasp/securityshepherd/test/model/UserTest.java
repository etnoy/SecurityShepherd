package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.persistence.model.Auth;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.persistence.model.Auth.AuthBuilder;
import org.owasp.securityshepherd.persistence.model.User.UserBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserTest {

	@Test
	public void build_NoArguments_ThrowsException() {

		assertThrows(NullPointerException.class, () -> User.builder().build());

	}

	@Test
	public void build_ValidDisplayName_ReturnsUser() {

		final String validDisplayName = "build_ValidDisplayName";

		final User build_ValidDisplayNameLengthUser = User.builder().displayName(validDisplayName).build();

		assertThat(build_ValidDisplayNameLengthUser, instanceOf(User.class));

		assertThat(build_ValidDisplayNameLengthUser.getDisplayName(), is(validDisplayName));
	}

	@Test
	public void buildClassId_ValidClassId_Builds() {

		final UserBuilder builder = User.builder().displayName("TestUser");

		builder.classId(1);

		final User newUser = builder.build();

		assertThat(newUser.getClassId(), is(1));

	}

	@Test
	public void buildDisplayName_NullDisplayName_ThrowsException() {

		assertThrows(NullPointerException.class, () -> User.builder().displayName(null));

	}

	@Test
	public void buildDisplayName_ValidDisplayName_Builds() {

		final String displayName = "buildDisplayName_ValidDisplayName";

		final UserBuilder builder = User.builder();
		builder.displayName(displayName);

		assertThat(builder.build(), instanceOf(User.class));

		assertThat(builder.build().getDisplayName(), is(displayName));

	}
	
	// TODO: we need to test userbuilder auth()
	
	@Test
	public void buildId_ValidId_Builds() {

		final UserBuilder builder = User.builder();

		builder.id(12345);
		builder.displayName("TestUser");

		assertThat(builder.build(), instanceOf(User.class));
		assertThat(builder.build().getId(), is(12345));

	}

	@Test
	public void equals_AutomaticTesting() {

		EqualsVerifier.forClass(User.class).verify();

	}

	@Test
	public void toString_ValidData_AsExpected() {

		final User testUser = User.builder().displayName("TestUser").build();

		assertThat(testUser.toString(),
				is("User(id=0, displayName=TestUser, classId=null, email=null, key=null, auth=null)"));

	}

	@Test
	public void userBuilderToString_ValidData_AsExpected() {
		final UserBuilder builder = User.builder();

		assertThat(builder.toString(), is(
				"User.UserBuilder(id=0, displayName=null, classId=null, email=null, key=null, auth=null)"));

	}

	@Test
	public void withAuth_ValidAuth_ChangesAuth() {

		final AuthBuilder authBuilder = Auth.builder();

		final Auth originalAuth = authBuilder.build();

		final User newUser = User.builder().displayName("Test User").auth(originalAuth).build();

		assertThat(newUser.getAuth(), is(originalAuth));

		assertThat(newUser.withAuth(originalAuth).getAuth(), is(originalAuth));

		final AuthBuilder[] testedAuthBuilders = { authBuilder, authBuilder.isAdmin(true),
				authBuilder.badLoginCount(3) };

		User changedUser;
		Auth newAuth;

		for (AuthBuilder newBuilder : testedAuthBuilders) {

			newAuth = newBuilder.build();
			changedUser = newUser.withAuth(newAuth);
			assertThat(changedUser.getAuth(), is(newAuth));

		}

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

		}

	}

	@Test
	public void withDisplayName_NullDisplayName_ThrowsException() {

		assertThrows(NullPointerException.class,
				() -> User.builder().displayName("TestUser").build().withDisplayName(null));

	}

	@Test
	public void withDisplayName_ValidDisplayName_ChangesDisplayName() {

		final String displayName = "withDisplayName_ValidDisplayName";

		final User newUser = User.builder().displayName(displayName).build();

		assertThat(newUser.getDisplayName(), is(displayName));

		final String[] testedDisplayNames = { displayName, "", "newUser", "Long  With     Whitespace", "12345" };

		User changedUser;
		for (String newDisplayName : testedDisplayNames) {

			changedUser = newUser.withDisplayName(newDisplayName);
			assertThat(changedUser.getDisplayName(), is(newDisplayName));

		}

	}

	@Test
	public void withEmail_ValidEmail_ChangesEmail() {

		final String displayName = "withEmail_ValidEmail";

		final String email = "validEmail@example.com";

		final String[] testedStrings = { email, null, "", "newEmail@example.com", "e@e", "a",
				"alongemail@example.com" };

		final User newUser = User.builder().displayName(displayName).email(email).build();

		assertThat(newUser.getEmail(), is(email));

		User changedUser;

		for (String newEmail : testedStrings) {

			changedUser = newUser.withEmail(newEmail);
			assertThat(changedUser.getEmail(), is(newEmail));

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

			changedUser = newUser.withId(newId);
			assertThat(changedUser.getId(), is(newId));

		}

	}

	@Test
	public void withKey_ValidKey_ChangesKey() {

		final String displayName = "withKey_ValidKey";

		final byte[] key = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

		final byte[][] testedKeys = { key, null, {}, { 1 }, { 19, 26, 127, -128 } };

		final User newUser = User.builder().displayName(displayName).key(key).build();

		assertThat(newUser.getKey(), is(key));

		User changedUser;

		for (byte[] newKey : testedKeys) {

			changedUser = newUser.withKey(newKey);
			assertThat(changedUser.getKey(), is(newKey));

		}

	}

}