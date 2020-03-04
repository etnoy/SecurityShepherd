package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.persistence.model.SAMLAuth;
import org.owasp.securityshepherd.persistence.model.SAMLAuth.SAMLAuthBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SAMLAuthTest {

	@Test
	public void builderToString_ValidData_AsExpected() {

		assertThat(SAMLAuth.builder().toString(), is("SAMLAuth.SAMLAuthBuilder(id=0, user=0, samlId=null)"));

	}

	@Test
	public void buildSamlid_NullSamlId_ThrowsException() {

		assertThrows(NullPointerException.class, () -> SAMLAuth.builder().samlId(null));

	}

	@Test
	public void buildSamlId_ValidSamlId_Builds() {

		final String[] samlIdsToTest = { "", "me@example.com", "a", "1" };

		for (String samlId : samlIdsToTest) {

			final SAMLAuthBuilder builder = SAMLAuth.builder();

			builder.samlId(samlId);

			assertThat(builder.build(), instanceOf(SAMLAuth.class));
			assertThat(builder.build().getSamlId(), is(samlId));

		}

	}

	@Test
	public void equals_AutomaticTesting() {
		EqualsVerifier.forClass(SAMLAuth.class).withIgnoredAnnotations(NonNull.class).verify();
	}

	@Test
	public void toString_ValidData_AsExpected() {

		assertThat(SAMLAuth.builder().samlId("TestID").build().toString(), is("SAMLAuth(id=0, user=0, samlId=TestID)"));

	}

	@Test
	public void withId_ValidId_ChangesId() {

		final int originalId = 1;
		final int[] testedIds = { originalId, 0, -1, 1000, -1000, 123456789 };

		final SAMLAuth newPasswordAuth = SAMLAuth.builder().id(originalId).build();

		assertThat(newPasswordAuth.getId(), is(originalId));

		for (int newId : testedIds) {

			assertThat(newPasswordAuth.withId(newId).getId(), is(newId));

		}

	}

	@Test
	public void withSamlid_NullSamlId_ThrowsException() {

		assertThrows(NullPointerException.class, () -> SAMLAuth.builder().samlId("Test").build().withSamlId(null));

	}

	@Test
	public void withSamlId_ValidSamlId_ChangesSamlId() {

		final SAMLAuth samlAuth = SAMLAuth.builder().samlId("me@example.com").build();

		final String[] testedSamlIds = { "me@example.com", "", "banned", "Long  With     Whitespace", "12345" };

		for (String newSamlId : testedSamlIds) {

			final SAMLAuth changedAuth = samlAuth.withSamlId(newSamlId);
			assertThat(changedAuth.getSamlId(), is(newSamlId));

		}

	}

	@Test
	public void withUser_ValidUser_ChangesUser() {

		final int originalUser = 1;
		final int[] testedUsers = { originalUser, 0, -1, 1000, -1000, 123456789 };

		final SAMLAuth newPasswordAuth = SAMLAuth.builder().user(originalUser).build();

		assertThat(newPasswordAuth.getUser(), is(originalUser));

		for (int newUser : testedUsers) {

			assertThat(newPasswordAuth.withUser(newUser).getUser(), is(newUser));

		}

	}

}