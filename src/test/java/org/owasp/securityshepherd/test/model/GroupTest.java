package org.owasp.securityshepherd.test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Group;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class GroupTest {

	@Test
	public void build_AllArguments_SuppliedValuesPresent() {
		Group.GroupBuilder buildAllArgumentsGroupBuilder = Group.builder().id("builder_AllArguments_id")
				.name("builder_AllArguments_name");

		Group buildAllArgumentsGroup = buildAllArgumentsGroupBuilder.build();

		assertEquals("builder_AllArguments_id", buildAllArgumentsGroup.getId());
		assertEquals("builder_AllArguments_name", buildAllArgumentsGroup.getName());

	}

	@Test
	public void build_IdTooLong_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class,
				() -> Group.builder().id("buildId_TooLong_morethan30chars").build());

	}

	@Test
	public void build_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> Group.builder().id("").build());

	}

	@Test
	public void build_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> Group.builder().name("").build());

	}

	@Test
	public void build_NameTooLong_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> Group.builder()
				.name("Build_Name_Too_Long_more_than_70_chars_Build_Name_Too_Long_Too_Long_70_").build());

	}

	@Test
	public void build_NullArguments_DefaultValuesPresent() {
		Group buildNullArgumentsGroup = Group.builder().id(null).name(null).build();

		assertNotNull(buildNullArgumentsGroup.getId());
		assertNotNull(buildNullArgumentsGroup.getName());

	}

	@Test
	public void build_ValidData_ReturnsGroupObject() {
		Group build_ValidDataGroup = Group.builder().build();

		assertTrue(build_ValidDataGroup instanceof Group);

	}

	@Test
	public void build_ValidIdLength_ReturnsGroup() {

		String validLengthId = "buildId_exactly__30_chars_long";

		assertEquals(30, validLengthId.length());

		Group build_ValidIdLengthGroup = Group.builder().id(validLengthId).build();

		assertTrue(build_ValidIdLengthGroup instanceof Group);

		assertEquals(validLengthId, build_ValidIdLengthGroup.getId());
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
		EqualsVerifier.forClass(Group.class).withOnlyTheseFields("id").suppress(Warning.STRICT_INHERITANCE).verify();
	}

	@Test
	public void setName_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> Group.builder().build().setName(null));

		assertThrows(IllegalArgumentException.class, () -> Group.builder().build().setName(""));

	}

	@Test
	public void setName_NameTooLong_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> Group.builder().build()
				.setName("Build_Name_Too_Long_more_than_70_chars_Build_Name_Too_Long_Too_Long_70_"));

	}

	@Test
	public void setName_ValidNameLength_SetsName() {

		String validLengthName = "Build_Name_exactly_70_chars_long_which_should_be_accepted_as_valid_123";

		Group setName_ValidNameLengthGroup = Group.builder().build();

		setName_ValidNameLengthGroup.setName(validLengthName);

		assertTrue(setName_ValidNameLengthGroup instanceof Group);

		assertEquals(validLengthName, setName_ValidNameLengthGroup.getName());
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