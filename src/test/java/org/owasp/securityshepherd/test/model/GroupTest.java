package org.owasp.securityshepherd.test.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Group;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class GroupTest {

	@Test
	public void build_AllArguments_SuppliedValuesPresent() {

		assertEquals("builder_AllArguments_groupname",
				Group.builder().name("builder_AllArguments_groupname").build().getName());

	}


	@Test
	public void build_NullName_ThrowsNullPointerException() {

		assertThrows(NullPointerException.class, () -> Group.builder().name(null).build());

	}

	@Test
	public void build_ValidNameLength_ReturnsGroup() {

		String validLengthName = "Build_Name_exactly_70_chars_long_which_should_be_accepted_as_valid_123";

		assertEquals(70, validLengthName.length());

		Group build_ValidNameLengthGroup = Group.builder().name(validLengthName).build();

		assertTrue(build_ValidNameLengthGroup instanceof Group);

		assertEquals(validLengthName, build_ValidNameLengthGroup.getName());
	}

	@Test
	public void build_ZeroArguments_DefaultValuesPresent() {
		Group buildZeroArgumentsGroup = Group.builder().build();

		assertNotNull(buildZeroArgumentsGroup.getId());
		assertNotNull(buildZeroArgumentsGroup.getName());

	}

	@Test
	public void equals_AutomaticTesting() {
		EqualsVerifier.forClass(Group.class).withOnlyTheseFields("id").verify();
	}

	@Test
	public void toString_ValidData_NotNull() {

		assertNotNull(Group.builder().build().toString());

	}

	@Test
	public void groupBuildertoString_ValidData_NotNull() {

		assertNotNull(Group.builder().toString());

	}

}