package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.persistence.model.Module;
import org.owasp.securityshepherd.persistence.model.Module.ModuleBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ModuleTest {

	private static final boolean[] BOOLEANS = { false, true };

	@Test
	public void build_NoArguments_ThrowsException() {

		assertThrows(NullPointerException.class, () -> Module.builder().build());

	}

	@Test
	public void buildDescription_ValidDescription_Builds() {

		final String[] descriptionsToTest = { "TestDescription", null, "", "a", "12345" };

		for (String description : descriptionsToTest) {

			final ModuleBuilder builder = Module.builder().name("TestModule");

			builder.description(description);

			assertThat(builder.build(), instanceOf(Module.class));
			assertThat(builder.build().getDescription(), is(description));

		}

	}

	@Test
	public void buildId_ValidId_Builds() {

		final ModuleBuilder builder = Module.builder();

		builder.id(123456);
		builder.name("TestModule");

		assertThat(builder.build(), instanceOf(Module.class));
		assertThat(builder.build().getId(), is(123456));

	}

	@Test
	public void buildIsExactFlag_TrueOrFalse_MatchesBuild() {

		for (boolean isFlagExact : BOOLEANS) {

			final ModuleBuilder builder = Module.builder().name("TestModule");

			builder.isFlagExact(isFlagExact);

			assertThat(builder.build(), instanceOf(Module.class));
			assertThat(builder.build().isFlagExact(), is(isFlagExact));

		}

	}

	@Test
	public void buildIsFlagEnabled_TrueOrFalse_MatchesBuild() {

		for (boolean isFlagEnabled : BOOLEANS) {

			final ModuleBuilder builder = Module.builder().name("TestModule");

			builder.isFlagEnabled(isFlagEnabled);

			assertThat(builder.build(), instanceOf(Module.class));
			assertThat(builder.build().isFlagEnabled(), is(isFlagEnabled));

		}

	}

	@Test
	public void buildIsOpen_TrueOrFalse_MatchesBuild() {

		for (boolean isOpen : BOOLEANS) {

			final ModuleBuilder builder = Module.builder().name("TestModule");

			builder.isOpen(isOpen);

			assertThat(builder.build(), instanceOf(Module.class));
			assertThat(builder.build().isOpen(), is(isOpen));

		}

	}

	@Test
	public void buildName_NullName_ThrowsNullPointerException() {

		assertThrows(NullPointerException.class, () -> Module.builder().name(null).build());

	}

	@Test
	public void buildName_ValidName_Builds() {

		final String[] namesToTest = { "TestModule", "", "a", "12345" };

		for (String name : namesToTest) {

			final ModuleBuilder builder = Module.builder();

			builder.name(name);

			assertThat(builder.build(), instanceOf(Module.class));
			assertThat(builder.build().getName(), is(name));

		}

	}

	@Test
	public void equals_AutomaticTesting() {
		EqualsVerifier.forClass(Module.class).withIgnoredAnnotations(NonNull.class).verify();
	}

	@Test
	public void moduleBuilderToString_ValidData_AsExpected() {

		final ModuleBuilder builder = Module.builder();

		assertThat(builder.toString(), is(
				"Module.ModuleBuilder(id=0, name=null, description=null, isFlagEnabled=false, isFlagExact=false, flag=null, isOpen=false)"));

	}

	@Test
	public void toString_ValidData_AsExpected() {

		final Module testModule = Module.builder().name("TestModule").build();

		assertThat(testModule.toString(), is(
				"Module(id=0, name=TestModule, description=null, isFlagEnabled=false, isFlagExact=false, flag=null, isOpen=false)"));

	}

	@Test
	public void withDescription_ValidDescription_ChangesDescription() {

		final String[] testedDescriptions = { "Description", null, "", "a", "Long Description With Spaces", "12345" };

		final Module testModule = Module.builder().name("TestModule").description("Description").build();

		assertThat(testModule.getDescription(), is("Description"));

		Module changedModule;

		for (String newDescription : testedDescriptions) {

			changedModule = testModule.withDescription(newDescription);
			assertThat(changedModule.getDescription(), is(newDescription));

		}

	}

	@Test
	public void withFlagExact_ValidBoolean_ChangesIsExactFlag() {

		final Module testModule = Module.builder().name("TestModule").build();

		assertThat(testModule.isFlagExact(), is(false));

		Module changedModule = testModule.withFlagExact(false);
		assertThat(changedModule.isFlagExact(), is(false));
		changedModule = testModule.withFlagExact(true);
		assertThat(changedModule.isFlagExact(), is(true));

	}

	@Test
	public void withFlag_ValidFlag_ChangesFlag() {

		final String[] testedFlags = { "abc123xyz789", null, "", "a", "Long Flag With Spaces", "12345" };

		final Module testModule = Module.builder().name("TestModule").flag("abc123xyz789").build();

		assertThat(testModule.getFlag(), is("abc123xyz789"));

		Module changedModule;

		for (String newFlag : testedFlags) {

			changedModule = testModule.withFlag(newFlag);
			assertThat(changedModule.getFlag(), is(newFlag));

		}

	}

	@Test
	public void withFlagEnabled_ValidBoolean_ChangesIsFlagEnabled() {

		final Module testModule = Module.builder().name("TestModule").build();

		assertThat(testModule.isFlagEnabled(), is(false));

		Module changedModule = testModule.withFlagEnabled(false);
		assertThat(changedModule.isFlagEnabled(), is(false));
		changedModule = testModule.withFlagEnabled(true);
		assertThat(changedModule.isFlagEnabled(), is(true));

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

	@Test
	public void withName_NullName_ThrowsException() {

		assertThrows(NullPointerException.class, () -> Module.builder().name("TestModule").build().withName(null));

	}

	@Test
	public void withName_ValidName_ChangesName() {

		final String[] testedNames = { "TestModule", "", "name", "Long Name With Spaces", "12345" };

		final Module testModule = Module.builder().name("TestModule").build();

		assertThat(testModule.getName(), is("TestModule"));

		Module changedModule;

		for (String newName : testedNames) {

			changedModule = testModule.withName(newName);
			assertThat(changedModule.getName(), is(newName));

		}

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

}