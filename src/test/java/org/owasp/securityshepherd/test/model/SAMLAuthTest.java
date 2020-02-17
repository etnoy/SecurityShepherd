package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.SAMLAuth;
import org.owasp.securityshepherd.model.SAMLAuth.SAMLAuthBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SAMLAuthTest {

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

		assertThat(SAMLAuth.builder().samlId("TestID").build().toString(), is("SAMLAuth(samlId=TestID)"));

	}
	
	@Test
	public void builderToString_ValidData_AsExpected() {

		assertThat(SAMLAuth.builder().toString(), is("SAMLAuth.SAMLAuthBuilder(samlId=null)"));

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

}