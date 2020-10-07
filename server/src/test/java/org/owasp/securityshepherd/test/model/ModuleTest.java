/*
 * This file is part of Security Shepherd.
 * 
 * Security Shepherd is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Security Shepherd.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.owasp.securityshepherd.test.model;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.Module.ModuleBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;

@DisplayName("Module unit test")
class ModuleTest {
  @Test
  void buildIsStaticFlag_TrueOrFalse_MatchesBuild() {
    // TODO
    for (final boolean isFlagStatic : TestUtils.BOOLEANS) {
      final ModuleBuilder builder = Module.builder().key(new byte[] {120, 56, 111});

      builder.isFlagStatic(isFlagStatic);

      assertThat(builder.build()).isInstanceOf(Module.class);
      assertThat(builder.build().isFlagStatic()).isEqualTo(isFlagStatic);
    }
  }

  @Test
  void buildIsOpen_TrueOrFalse_MatchesBuild() {
    for (final boolean isOpen : TestUtils.BOOLEANS) {

      final ModuleBuilder builder = Module.builder().key(new byte[] {120, 56, 111});

      builder.isOpen(isOpen);

      assertThat(builder.build()).isInstanceOf(Module.class);
      assertThat(builder.build().isOpen()).isEqualTo(isOpen);
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
    final Module testModule = Module.builder().key(new byte[] {120, 56, 111}).build();

    assertThat(testModule)
        .hasToString(
            "Module(id=null, name=TestModule, shortName=test-module, "
                + "description=null, isFlagStatic=false, staticFlag=null, "
                + "key=[120, 56, 111], isOpen=false)");
  }

  @Test
  void withStaticFlag_ValidStaticFlag_ChangesFlag() {
    final Module module =
        Module.builder()
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
    final Module testModule = Module.builder().key(new byte[] {120, 56, 111}).build();

    assertThat(testModule.isFlagStatic()).isFalse();

    Module changedModule = testModule.withFlagStatic(false);
    assertThat(changedModule.isFlagStatic()).isFalse();
    changedModule = testModule.withFlagStatic(true);
    assertThat(changedModule.isFlagStatic()).isTrue();
  }

  @Test
  void withId_ValidId_ChangesId() {
    final Module module =
        Module.builder().name(TestUtils.INITIAL_NAMES).key(new byte[] {120, 56, 111}).build();

    for (final String name : TestUtils.NAMES) {
      assertThat(module.withName(name).getId()).isEqualTo(name);
    }
  }

  @Test
  void withOpen_ValidBoolean_ChangesOpen() {
    final Module testModule = Module.builder().key(new byte[] {120, 56, 111}).build();

    assertThat(testModule.isOpen()).isFalse();

    Module changedModule = testModule.withOpen(false);
    assertThat(changedModule.isOpen()).isFalse();
    changedModule = testModule.withOpen(true);
    assertThat(changedModule.isOpen()).isTrue();
  }
}
