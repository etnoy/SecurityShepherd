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
		User.UserBuilder buildAllArgumentsUserBuilder = User.builder().classId("builder_AllArguments_classid")
				.name("builder_AllArguments_username").role("admin").email("builder_AllArguments@example.com");

		User buildAllArgumentsUser = buildAllArgumentsUserBuilder.build();

		assertEquals("builder_AllArguments_classid", buildAllArgumentsUser.getClassId());
		assertEquals("builder_AllArguments_username", buildAllArgumentsUser.getName());
		assertEquals("admin", buildAllArgumentsUser.getRole());
		assertEquals("builder_AllArguments@example.com", buildAllArgumentsUser.getEmail());

	}

	@Test
	public void build_ValidClassIdLength_ReturnsUser() {

		String validLengthClassId = "classId_exactly__30_chars_long";

		assertEquals(30, validLengthClassId.length());

		User build_ValidClassIdLengthUser = User.builder().classId(validLengthClassId).build();

		assertTrue(build_ValidClassIdLengthUser instanceof User);

		assertEquals(validLengthClassId, build_ValidClassIdLengthUser.getClassId());
	}

	@Test
	public void build_ValidNameLength_ReturnsUser() {

		String validLengthName = "Build_Name_exactly_70_chars_long_which_should_be_accepted_as_valid_123";

		assertEquals(70, validLengthName.length());

		User build_ValidNameLengthUser = User.builder().name(validLengthName).build();

		assertTrue(build_ValidNameLengthUser instanceof User);

		assertEquals(validLengthName, build_ValidNameLengthUser.getName());
	}

	@Test
	public void build_ZeroArguments_DefaultValuesPresent() {
		User buildZeroArgumentsUser = User.builder().build();

		assertNotNull(buildZeroArgumentsUser.getId());
		assertEquals(null, buildZeroArgumentsUser.getClassId());
		assertNotNull(buildZeroArgumentsUser.getName());
		assertEquals("player", buildZeroArgumentsUser.getRole());
		assertEquals(null, buildZeroArgumentsUser.getEmail());

	}

	@Test
	public void equals_AutomaticTesting() {
		EqualsVerifier.forClass(User.class).withOnlyTheseFields("id").verify();
	}

	@Test
	public void toString_ValidData_NotNull() {

		assertNotNull(User.builder().build().toString());

	}

	@Test
	public void userBuildertoString_ValidData_NotNull() {

		assertNotNull(User.builder().toString());

	}


}