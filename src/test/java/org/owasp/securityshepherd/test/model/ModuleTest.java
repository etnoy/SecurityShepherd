package org.owasp.securityshepherd.test.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Module;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ModuleTest {

	@Test
	public void build_AllArguments_SuppliedValuesPresent() {

		assertEquals("builder_AllArguments_modulename",
				Module.builder().name("builder_AllArguments_modulename").build().getName());

	}


	@Test
	public void build_NullName_ThrowsNullPointerException() {

		assertThrows(NullPointerException.class, () -> Module.builder().name(null).build());

	}

	@Test
	public void build_ValidNameLength_ReturnsModule() {

		String validLengthName = "Build_Name_exactly_70_chars_long_which_should_be_accepted_as_valid_123";

		assertEquals(70, validLengthName.length());

		Module build_ValidNameLengthModule = Module.builder().name(validLengthName).build();

		assertTrue(build_ValidNameLengthModule instanceof Module);

		assertEquals(validLengthName, build_ValidNameLengthModule.getName());
	}

	@Test
	public void build_ZeroArguments_DefaultValuesPresent() {
		Module buildZeroArgumentsModule = Module.builder().name("build_ZeroArguments").build();

		assertNotNull(buildZeroArgumentsModule.getId());

	}

	@Test
	public void equals_AutomaticTesting() {
		EqualsVerifier.forClass(Module.class).withOnlyTheseFields("id").verify();
	}

	@Test
	public void toString_ValidData_NotNull() {

		assertNotNull(Module.builder().name("toString_ValidData").build().toString());

	}

	@Test
	public void moduleBuildertoString_ValidData_NotNull() {

		assertNotNull(Module.builder().toString());

	}

}