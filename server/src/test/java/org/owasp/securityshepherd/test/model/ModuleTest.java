/**
 * This file is part of Security Shepherd.
 *
 * <p>Security Shepherd is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with Security
 * Shepherd. If not, see <http://www.gnu.org/licenses/>.
 */
package org.owasp.securityshepherd.test.model;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.Module.ModuleBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("Module unit test")
class ModuleTest {
  @Test
  void build_NameNotGiven_ThrowsNullPointerException() {
    final ModuleBuilder moduleBuilder = Module.builder().shortName("test-module");
    
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleBuilder.build());
    assertThat(thrownException.getMessage()).isEqualTo("name is marked non-null but is null");
  }

  @Test
  void build_ShortNameNotGiven_ThrowsNullPointerException() {
    final ModuleBuilder moduleBuilder = Module.builder().name("test-module");

    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleBuilder.build());
    assertThat(thrownException.getMessage()).isEqualTo("shortName is marked non-null but is null");
  }

  @Test
  void buildDescription_ValidDescription_Builds() {
    final ModuleBuilder builder = Module.builder().name("TestModule").shortName("test-module");
    for (final String description : TestUtils.STRINGS_WITH_NULL) {
      builder.description(description);
      final Module module = builder.build();
      assertThat(module.getDescription()).isEqualTo(description);
    }
  }

  @Test
  void buildIsExactFlag_TrueOrFalse_MatchesBuild() {
    // TODO
    for (final boolean isFlagExact : TestUtils.BOOLEANS) {
      final ModuleBuilder builder = Module.builder().name("TestModule").shortName("test-module");

      builder.isFlagExact(isFlagExact);

      assertThat(builder.build()).isInstanceOf(Module.class);
      assertThat(builder.build().isFlagExact()).isEqualTo(isFlagExact);
    }
  }

  @Test
  void buildIsFlagEnabled_TrueOrFalse_MatchesBuild() {
    for (final boolean isFlagEnabled : TestUtils.BOOLEANS) {
      final ModuleBuilder builder = Module.builder().name("TestModule").shortName("test-module");

      builder.isFlagEnabled(isFlagEnabled);

      assertThat(builder.build()).isInstanceOf(Module.class);
      assertThat(builder.build().isFlagEnabled()).isEqualTo(isFlagEnabled);
    }
  }

  @Test
  void buildIsOpen_TrueOrFalse_MatchesBuild() {
    for (final boolean isOpen : TestUtils.BOOLEANS) {

      final ModuleBuilder builder = Module.builder().name("TestModule").shortName("test-module");

      builder.isOpen(isOpen);

      assertThat(builder.build()).isInstanceOf(Module.class);
      assertThat(builder.build().isOpen()).isEqualTo(isOpen);
    }
  }

  @Test
  void buildName_NullName_ThrowsNullPointerException() {
    final ModuleBuilder moduleBuilder = Module.builder().shortName("test-module");
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleBuilder.name(null));
    assertThat(thrownException.getMessage()).isEqualTo("name is marked non-null but is null");
  }

  @Test
  void buildName_NullShortName_ThrowsNullPointerException() {
    final ModuleBuilder moduleBuilder = Module.builder().name("TestModule");
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleBuilder.shortName(null));
    assertThat(thrownException.getMessage()).isEqualTo("shortName is marked non-null but is null");
  }

  @Test
  void buildName_ValidName_Builds() {
    final ModuleBuilder builder = Module.builder().shortName("test-module");
    for (final String name : TestUtils.STRINGS) {
      builder.name(name);
      final Module module = builder.build();
      assertThat(module).isInstanceOf(Module.class);
      assertThat(module.getName()).isEqualTo(name);
    }
  }

  @Test
  void buildShortName_ValidShortName_Builds() {
    final ModuleBuilder builder = Module.builder().name("TestModule");
    for (final String shortName : TestUtils.STRINGS) {
      builder.shortName(shortName);
      final Module module = builder.build();
      assertThat(module).isInstanceOf(Module.class);
      assertThat(module.getShortName()).isEqualTo(shortName);
    }
  }

  @Test
  void equals_AutomaticTesting() {
    EqualsVerifier.forClass(Module.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  void moduleBuilderToString_ValidData_AsExpected() {
    final ModuleBuilder builder = Module.builder();

    assertThat(builder.toString()).isEqualTo(
            "Module.ModuleBuilder(id=null, name=null, shortName=null, description=null, isFlagEnabled=false, "
                + "isFlagExact=false, flag=null, isOpen=false)");
  }

  @Test
  void toString_ValidData_AsExpected() {
    final Module testModule = Module.builder().name("TestModule").shortName("test-module").build();

    assertThat(testModule.toString()).isEqualTo(
            "Module(id=null, name=TestModule, shortName=test-module, description=null, isFlagEnabled=false, isFlagExact=false, "
                + "flag=null, isOpen=false)");
  }

  @Test
  void withDescription_ValidDescription_ChangesDescription() {
    final String[] testedDescriptions = {
      "Description", null, "", "a", "Long Description With Spaces", "12345"
    };

    final Module testModule =
        Module.builder()
            .name("TestModule")
            .shortName("test-module")
            .description("Description")
            .build();

    assertThat(testModule.getDescription()).isEqualTo("Description");

    for (final String newDescription : testedDescriptions) {
      assertThat(testModule.withDescription(newDescription).getDescription()).isEqualTo(newDescription);
    }
  }

  @Test
  void withFlag_ValidFlag_ChangesFlag() {
    // TODO: refactor
    final String originalFlag = "abc123xyz789";
    final String[] testedFlags = {originalFlag, null, "", "a", "Long Flag With Spaces", "12345"};

    final Module module =
        Module.builder().name("TestModule").shortName("test-module").flag("abc123xyz789").build();

    assertThat(module.getFlag()).isEqualTo(originalFlag);

    for (final String newFlag : testedFlags) {
      assertThat(module.withFlag(newFlag).getFlag()).isEqualTo(newFlag);
    }
  }

  @Test
  void withFlagEnabled_ValidBoolean_ChangesIsFlagEnabled() {
    // TODO: refactor

    final Module testModule = Module.builder().name("TestModule").shortName("test-module").build();

    assertThat(testModule.isFlagEnabled()).isFalse();

    Module changedModule = testModule.withFlagEnabled(false);
    assertThat(changedModule.isFlagEnabled()).isFalse();
    changedModule = testModule.withFlagEnabled(true);
    assertThat(changedModule.isFlagEnabled()).isTrue();
  }

  @Test
  void withFlagExact_ValidBoolean_ChangesIsExactFlag() {
    // TODO: refactor

    final Module testModule = Module.builder().name("TestModule").shortName("test-module").build();

    assertThat(testModule.isFlagExact()).isFalse();

    Module changedModule = testModule.withFlagExact(false);
    assertThat(changedModule.isFlagExact()).isFalse();
    changedModule = testModule.withFlagExact(true);
    assertThat(changedModule.isFlagExact()).isTrue();
  }

  @Test
  void withId_ValidId_ChangesId() {
    final Module testModule =
        Module.builder()
            .id(TestUtils.INITIAL_LONG)
            .name("TestModule")
            .shortName("test-module")
            .build();

    for (final long id : TestUtils.LONGS) {
      final Module withModule = testModule.withId(id);
      assertThat(withModule.getId()).isEqualTo(id);
    }
  }

  @Test
  void withName_NullName_ThrowsNullPointerException() {
    final Module module = Module.builder().name("TestModule").shortName("test-module").build();
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> module.withName(null));
    assertThat(thrownException.getMessage()).isEqualTo("name is marked non-null but is null");
  }

  @Test
  void withName_ValidName_ChangesName() {
    final Module testModule =
        Module.builder().name(TestUtils.INITIAL_STRING).shortName("test-module").build();

    for (final String moduleName : TestUtils.STRINGS) {
      final Module withModule = testModule.withName(moduleName);
      assertThat(withModule.getName()).isEqualTo(moduleName);
    }
  }

  @Test
  void withOpen_ValidBoolean_ChangesOpen() {
    final Module testModule = Module.builder().name("TestModule").shortName("test-module").build();

    assertThat(testModule.isOpen()).isFalse();

    Module changedModule = testModule.withOpen(false);
    assertThat(changedModule.isOpen()).isFalse();
    changedModule = testModule.withOpen(true);
    assertThat(changedModule.isOpen()).isTrue();
  }

  @Test
  void withShortName_NullShortName_ThrowsNullPointerException() {
    final Module module = Module.builder().name("TestModule").shortName("test-module").build();
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> module.withShortName(null));
    assertThat(thrownException.getMessage()).isEqualTo("shortName is marked non-null but is null");
  }

  @Test
  void withShortName_ValidShortName_ChangesName() {
    final Module testModule =
        Module.builder().name("TestModule").shortName(TestUtils.INITIAL_STRING).build();

    for (final String shortName : TestUtils.STRINGS) {
      final Module withModule = testModule.withShortName(shortName);
      assertThat(withModule.getShortName()).isEqualTo(shortName);
    }
  }
}
