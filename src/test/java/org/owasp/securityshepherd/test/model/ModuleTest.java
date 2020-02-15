package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.Module.ModuleBuilder;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.User.UserBuilder;
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

	@Test
	public void moduleBuildertoString_ValidData_AsExpected() {
		final ModuleBuilder builder = Module.builder();

		assertThat(builder.toString(), is(equalTo(
				"Module.ModuleBuilder(id=0, name=null, description=null, flagEnabled$value=false, exactFlag$value=false, flag=null, isOpen$value=false)")));

	}

	@Test
	public void withName_ValidName_ChangesName() {

		final String[] testedNames = { "TestModule", "", "name", "Long Name With Spaces", "12345" };

		final Module testModule = Module.builder().name("TestModule").build();

		assertThat(testModule.getName(), is(equalTo("TestModule")));

		Module changedModule;

		for (String newName : testedNames) {

			changedModule = testModule.withName(newName);
			assertThat(changedModule.getName(), is(equalTo(newName)));
			assertThat(changedModule, is(equalTo(testModule)));

		}

	}

	@Test
	public void withName_NullName_ThrowsException() {

		assertThrows(NullPointerException.class, () -> Module.builder().name("TestModule").build().withName(null));

	}

	@Test
	public void withDescription_ValidDescription_ChangesDescription() {

		final String[] testedDescriptions = { "Description", null, "", "a", "Long Description With Spaces", "12345" };

		final Module testModule = Module.builder().name("TestModule").description("Description").build();

		assertThat(testModule.getDescription(), is(equalTo("Description")));

		Module changedModule;

		for (String newDescription : testedDescriptions) {

			changedModule = testModule.withDescription(newDescription);
			assertThat(changedModule.getDescription(), is(equalTo(newDescription)));
			assertThat(changedModule, is(equalTo(testModule)));

		}

	}

	@Test
	public void withFlag_ValidFlag_ChangesFlag() {

		final String[] testedFlags = { "abc123xyz789", null, "", "a", "Long Flag With Spaces", "12345" };

		final Module testModule = Module.builder().name("TestModule").flag("abc123xyz789").build();

		assertThat(testModule.getFlag(), is(equalTo("abc123xyz789")));

		Module changedModule;

		for (String newFlag : testedFlags) {

			changedModule = testModule.withFlag(newFlag);
			assertThat(changedModule.getFlag(), is(equalTo(newFlag)));
			assertThat(changedModule, is(equalTo(testModule)));

		}

	}

	@Test
	public void withFlagEnabled_ValidBoolean_ChangesFlagEnabled() {

		final Module testModule = Module.builder().name("TestModule").build();

		assertThat(testModule.isFlagEnabled(), is(false));

		Module changedModule = testModule.withFlagEnabled(false);
		assertThat(changedModule.isFlagEnabled(), is(false));
		changedModule = testModule.withFlagEnabled(true);
		assertThat(changedModule.isFlagEnabled(), is(true));

	}
	
	@Test
	public void withExactFlag_ValidBoolean_ChangesExactFlag() {

		final Module testModule = Module.builder().name("TestModule").build();

		assertThat(testModule.isExactFlag(), is(false));

		Module changedModule = testModule.withExactFlag(false);
		assertThat(changedModule.isExactFlag(), is(false));
		changedModule = testModule.withExactFlag(true);
		assertThat(changedModule.isExactFlag(), is(true));

	}
	
	@Test
	public void withOpen_ValidBoolean_ChangesOpen() {

		final Module testModule = Module.builder().name("TestModule").build();

		assertThat(testModule.isOpen(), is(false));

		Module changedModule = testModule.withOpen(false);
		assertThat(changedModule.isOpen(), is(false));
		changedModule = testModule.withOpen(true);
		assertThat(changedModule.isOpen(), is(true));

	}
	
	@Test
	public void withId_ValidId_ChangesId() {

		final int originalId = 1;
		final int[] testedIds = { originalId, 0, -1, 1000, -1000, 123456789, -12346789 };

		final Module testModule = Module.builder().id(originalId).name("TestModule").build();

		assertThat(testModule.getId(), is(originalId));

		Module changedModule;

		for (int newId : testedIds) {

			changedModule = testModule.withId(newId);
			assertThat(changedModule.getId(), is(newId));

		}

	}

}