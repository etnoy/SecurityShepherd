package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.ClassEntity;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ClassTest {

	@Test
	public void build_AllArguments_SuppliedValuesPresent() {

		assertEquals("builder_AllArguments_classname",
				ClassEntity.builder().name("builder_AllArguments_classname").build().getName());

	}

	@Test
	public void build_NullName_ThrowsNullPointerException() {

		assertThrows(NullPointerException.class, () -> ClassEntity.builder().name(null).build());

	}

	@Test
	public void build_ValidNameLength_ReturnsClass() {

		String validLengthName = "Build_Name_exactly_70_chars_long_which_should_be_accepted_as_valid_123";

		assertEquals(70, validLengthName.length());

		ClassEntity build_ValidNameLengthClass = ClassEntity.builder().name(validLengthName).build();

		assertTrue(build_ValidNameLengthClass instanceof ClassEntity);

		assertEquals(validLengthName, build_ValidNameLengthClass.getName());
	}

	@Test
	public void classBuildertoString_ValidData_NotNull() {

		assertNotNull(ClassEntity.builder().toString());

	}

	@Test
	public void equals_AutomaticTesting() {
		EqualsVerifier.forClass(ClassEntity.class).withOnlyTheseFields("id").verify();
	}

	@Test
	public void toString_ValidData_NotNull() {

		assertNotNull(ClassEntity.builder().name("TestClass").build().toString());

	}

	@Test
	public void withId_ValidId_ChangesId() {

		final int originalId = 1;
		final int[] testedIds = { originalId, 0, -1, 1000, -1000, 123456789 };

		final ClassEntity testClass = ClassEntity.builder().id(originalId).name("Test Class").build();

		assertThat(testClass.getId(), is(originalId));

		ClassEntity changedClass;

		for (int newId : testedIds) {

			changedClass = testClass.withId(newId);
			assertThat(changedClass.getId(), is(newId));

		}

	}

	@Test
	public void withName_ValidName_ChangesName() {

		final String name = "Test Class";

		final ClassEntity testClass = ClassEntity.builder().name(name).build();

		assertThat(testClass.getName(), is(name));

		final String[] testedNames = { name, "", "newClass", "Long  With     Whitespace", "12345" };

		ClassEntity changedClass;
		for (String newName : testedNames) {

			changedClass = testClass.withName(newName);
			assertThat(changedClass.getName(), is(newName));
			assertThat(changedClass, is(testClass));

		}

	}

	@Test
	public void withName_NullName_ThrowsException() {

		final String name = "withName_NullName";

		final ClassEntity testClass = ClassEntity.builder().name(name).build();

		assertThat(testClass.getName(), is(name));

		assertThrows(NullPointerException.class, () -> testClass.withName(null));

	}

}