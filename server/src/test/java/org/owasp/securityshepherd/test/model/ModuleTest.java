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
    final ModuleBuilder builder =
        Module.builder().name("TestModule").shortName("test-module").key(new byte[] {120, 56, 111});
    for (final String description : TestUtils.STRINGS_WITH_NULL) {
      builder.description(description);
      final Module module = builder.build();
      assertThat(module.getDescription()).isEqualTo(description);
    }
  }

  @Test
  void buildIsStaticFlag_TrueOrFalse_MatchesBuild() {
    // TODO
    for (final boolean isFlagStatic : TestUtils.BOOLEANS) {
      final ModuleBuilder builder =
          Module.builder()
              .name("TestModule")
              .key(new byte[] {120, 56, 111})
              .shortName("test-module");

      builder.isFlagStatic(isFlagStatic);

      assertThat(builder.build()).isInstanceOf(Module.class);
      assertThat(builder.build().isFlagStatic()).isEqualTo(isFlagStatic);
    }
  }

  @Test
  void buildIsOpen_TrueOrFalse_MatchesBuild() {
    for (final boolean isOpen : TestUtils.BOOLEANS) {

      final ModuleBuilder builder =
          Module.builder()
              .name("TestModule")
              .shortName("test-module")
              .key(new byte[] {120, 56, 111});

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
    final ModuleBuilder builder =
        Module.builder().shortName("test-module").key(new byte[] {120, 56, 111});
    for (final String name : TestUtils.STRINGS) {
      builder.name(name);
      final Module module = builder.build();
      assertThat(module).isInstanceOf(Module.class);
      assertThat(module.getName()).isEqualTo(name);
    }
  }

  @Test
  void buildShortName_ValidShortName_Builds() {
    final ModuleBuilder builder =
        Module.builder().name("TestModule").key(new byte[] {120, 56, 111});
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

    assertThat(builder)
        .hasToString(
            "Module.ModuleBuilder(id=null, name=null, shortName=null, "
                + "description=null, isFlagStatic=false, staticFlag=null, key=null, isOpen=false)");
  }

  @Test
  void toString_ValidData_AsExpected() {
    final Module testModule =
        Module.builder()
            .name("TestModule")
            .shortName("test-module")
            .key(new byte[] {120, 56, 111})
            .build();

    assertThat(testModule)
        .hasToString(
            "Module(id=null, name=TestModule, shortName=test-module, "
                + "description=null, isFlagStatic=false, staticFlag=null, "
                + "key=[120, 56, 111], isOpen=false)");
  }

  @Test
  void withDescription_ValidDescription_ChangesDescription() {
    final Module testModule =
        Module.builder()
            .name("TestModule")
            .shortName("test-module")
            .description(TestUtils.INITIAL_STRING)
            .key(new byte[] {120, 56, 111})
            .build();

    for (final String newDescription : TestUtils.STRINGS_WITH_NULL) {
      assertThat(testModule.withDescription(newDescription).getDescription())
          .isEqualTo(newDescription);
    }
  }

  @Test
  void withStaticFlag_ValidStaticFlag_ChangesFlag() {
    final Module module =
        Module.builder()
            .name("TestModule")
            .shortName("test-module")
            .staticFlag(TestUtils.INITIAL_STRING)
            .key(new byte[] {120, 56, 111})
            .build();

    for (final String newStaticFlag : TestUtils.STRINGS_WITH_NULL) {
      assertThat(module.withStaticFlag(newStaticFlag).getStaticFlag()).isEqualTo(newStaticFlag);
    }
  }

  @Test
  void withFlagStatic_ValidBoolean_ChangesIsStaticFlag() {
    // TODO: Refactor
    final Module testModule =
        Module.builder()
            .name("TestModule")
            .key(new byte[] {120, 56, 111})
            .shortName("test-module")
            .build();

    assertThat(testModule.isFlagStatic()).isFalse();

    Module changedModule = testModule.withFlagStatic(false);
    assertThat(changedModule.isFlagStatic()).isFalse();
    changedModule = testModule.withFlagStatic(true);
    assertThat(changedModule.isFlagStatic()).isTrue();
  }

  @Test
  void withId_ValidId_ChangesId() {
    final Module module =
        Module.builder()
            .id(TestUtils.INITIAL_LONG)
            .name("TestModule")
            .shortName("test-module")
            .key(new byte[] {120, 56, 111})
            .build();

    for (final long id : TestUtils.LONGS) {
      assertThat(module.withId(id).getId()).isEqualTo(id);
    }
  }

  @Test
  void withName_NullName_ThrowsNullPointerException() {
    final Module module =
        Module.builder()
            .name("TestModule")
            .shortName("test-module")
            .key(new byte[] {120, 56, 111})
            .build();
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> module.withName(null));
    assertThat(thrownException.getMessage()).isEqualTo("name is marked non-null but is null");
  }

  @Test
  void withName_ValidName_ChangesName() {
    final Module testModule =
        Module.builder()
            .name(TestUtils.INITIAL_STRING)
            .key(new byte[] {120, 56, 111})
            .shortName("test-module")
            .build();

    for (final String moduleName : TestUtils.STRINGS) {
      final Module withModule = testModule.withName(moduleName);
      assertThat(withModule.getName()).isEqualTo(moduleName);
    }
  }

  @Test
  void withOpen_ValidBoolean_ChangesOpen() {
    final Module testModule =
        Module.builder()
            .name("TestModule")
            .key(new byte[] {120, 56, 111})
            .shortName("test-module")
            .build();

    assertThat(testModule.isOpen()).isFalse();

    Module changedModule = testModule.withOpen(false);
    assertThat(changedModule.isOpen()).isFalse();
    changedModule = testModule.withOpen(true);
    assertThat(changedModule.isOpen()).isTrue();
  }

  @Test
  void withShortName_NullShortName_ThrowsNullPointerException() {
    final Module module =
        Module.builder()
            .name("TestModule")
            .key(new byte[] {120, 56, 111})
            .shortName("test-module")
            .build();
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> module.withShortName(null));
    assertThat(thrownException.getMessage()).isEqualTo("shortName is marked non-null but is null");
  }

  @Test
  void withShortName_ValidShortName_ChangesName() {
    final Module testModule =
        Module.builder()
            .name("TestModule")
            .key(new byte[] {120, 56, 111})
            .shortName(TestUtils.INITIAL_STRING)
            .build();

    for (final String shortName : TestUtils.STRINGS) {
      final Module withModule = testModule.withShortName(shortName);
      assertThat(withModule.getShortName()).isEqualTo(shortName);
    }
  }
}
