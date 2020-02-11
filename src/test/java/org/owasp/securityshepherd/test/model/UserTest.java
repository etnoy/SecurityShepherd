package org.owasp.securityshepherd.test.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.User;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserTest {

	@Test
	public void build_AllArguments_SuppliedValuesPresent() {
		User.UserBuilder buildAllArgumentsUserBuilder = User.builder().classId(99)
				.displayName("builder_AllArguments_username").email("builder_AllArguments@example.com");

		User buildAllArgumentsUser = buildAllArgumentsUserBuilder.build();

		assertEquals(99, buildAllArgumentsUser.getClassId());
		assertEquals("builder_AllArguments_username", buildAllArgumentsUser.getDisplayName());
		assertEquals("builder_AllArguments@example.com", buildAllArgumentsUser.getEmail());

	}

	@Test
	public void build_ValidDisplayName_ReturnsUser() {

		String validDisplayName = "build_ValidDisplayName";

		User build_ValidDisplayNameLengthUser = User.builder().displayName(validDisplayName).build();

		assertTrue(build_ValidDisplayNameLengthUser instanceof User);

		assertEquals(validDisplayName, build_ValidDisplayNameLengthUser.getDisplayName());
	}

	@Test
	public void build_ZeroArguments_DefaultValuesPresent() {

		User buildZeroArgumentsUser = User.builder().displayName("build_ZeroArguments").build();

		assertNotNull(buildZeroArgumentsUser.getId());
		assertNull(buildZeroArgumentsUser.getClassId());
		assertNotNull(buildZeroArgumentsUser.getDisplayName());
		assertNull(buildZeroArgumentsUser.getEmail());

	}

	@Test
	public void equals_AutomaticTesting() {
		EqualsVerifier.forClass(User.class).withOnlyTheseFields("id").verify();
	}

	@Test
	public void toString_ValidData_NotNull() {

		assertNotNull(User.builder().displayName("toString_ValidData").build().toString());

	}

	@Test
	public void userBuildertoString_ValidData_NotNull() {

		assertNotNull(User.builder().toString());

	}

}