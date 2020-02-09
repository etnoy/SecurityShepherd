package org.owasp.securityshepherd.test.model;

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
	public void build_ZeroArguments_DefaultValuesPresent() {
		ClassEntity buildZeroArgumentsClass = ClassEntity.builder().build();

		assertNotNull(buildZeroArgumentsClass.getId());
		assertNotNull(buildZeroArgumentsClass.getName());

	}

	@Test
	public void equals_AutomaticTesting() {
		EqualsVerifier.forClass(ClassEntity.class).withOnlyTheseFields("id").verify();
	}

	@Test
	public void toString_ValidData_NotNull() {

		assertNotNull(ClassEntity.builder().build().toString());

	}

	@Test
	public void classBuildertoString_ValidData_NotNull() {

		assertNotNull(ClassEntity.builder().toString());

	}

}