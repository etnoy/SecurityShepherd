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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.module.ModuleListItem;
import org.owasp.securityshepherd.module.ModuleListItem.ModuleListItemBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("ModuleListItem unit test")
public class ModuleListItemTest {
  @Test
  void build_IdNotGiven_ThrowsNullPointerException() {
    Throwable thrownException =
        assertThrows(
            NullPointerException.class,
            () -> ModuleListItem.builder().name("TestModule").shortName("test-module").build());
    assertThat(thrownException.getMessage(), is("id is marked non-null but is null"));
  }

  @Test
  void build_NameNotGiven_ThrowsNullPointerException() {
    Throwable thrownException =
        assertThrows(
            NullPointerException.class,
            () -> ModuleListItem.builder().id(1L).shortName("test-module").build());
    assertThat(thrownException.getMessage(), is("name is marked non-null but is null"));
  }

  @Test
  void build_ShortNameNotGiven_ThrowsNullPointerException() {
    final ModuleListItemBuilder moduleListItemBuilder =
        ModuleListItem.builder().id(1L).name("TestModuleListItem");
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleListItemBuilder.build());
    assertThat(thrownException.getMessage(), is("shortName is marked non-null but is null"));
  }

  @Test
  void buildId_ValidId_BuildsModuleListItem() {
    final ModuleListItemBuilder moduleListItemBuilder =
        ModuleListItem.builder().name("TestModule").shortName("test-module");
    for (final long id : TestUtils.LONGS) {
      final ModuleListItem moduleListItem = moduleListItemBuilder.id(id).build();
      assertThat(moduleListItem.getId(), is(id));
    }
  }

  @Test
  void buildDescription_ValidDescription_Builds() {
    final ModuleListItemBuilder builder =
        ModuleListItem.builder().id(1L).name("TestModuleListItem").shortName("test-module");
    for (final String description : TestUtils.STRINGS_WITH_NULL) {
      builder.description(description);
      final ModuleListItem moduleListItem = builder.build();
      assertThat(moduleListItem.getDescription(), is(description));
    }
  }

  @Test
  void buildIsSolved_TrueOrFalse_MatchesBuild() {
    final ModuleListItemBuilder builder =
        ModuleListItem.builder().id(1L).name("TestModule").shortName("test-module");
    for (final Boolean isSolved : TestUtils.BOOLEANS_WITH_NULL) {
      builder.isSolved(isSolved);

      final ModuleListItem moduleListItem = builder.build();

      assertThat(moduleListItem, instanceOf(ModuleListItem.class));
      assertThat(moduleListItem.getIsSolved(), is(isSolved));
    }
  }

  @Test
  void buildId_NullId_ThrowsNullPointerException() {
    final ModuleListItemBuilder moduleBuilder =
        ModuleListItem.builder().name("TestModule").shortName("test-module");
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleBuilder.id(null));
    assertThat(thrownException.getMessage(), is("id is marked non-null but is null"));
  }

  @Test
  void buildName_NullName_ThrowsNullPointerException() {
    final ModuleListItemBuilder moduleBuilder = ModuleListItem.builder().shortName("test-module");
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleBuilder.name(null));
    assertThat(thrownException.getMessage(), is("name is marked non-null but is null"));
  }

  @Test
  void buildName_NullShortName_ThrowsNullPointerException() {
    final ModuleListItemBuilder moduleBuilder = ModuleListItem.builder().name("TestModuleListItem");
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleBuilder.shortName(null));
    assertThat(thrownException.getMessage(), is("shortName is marked non-null but is null"));
  }

  @Test
  void buildName_ValidName_Builds() {
    final ModuleListItemBuilder builder = ModuleListItem.builder().id(1L).shortName("test-module");
    for (final String name : TestUtils.STRINGS) {
      builder.name(name);
      final ModuleListItem moduleListItem = builder.build();
      assertThat(moduleListItem, instanceOf(ModuleListItem.class));
      assertThat(moduleListItem.getName(), is(name));
    }
  }

  @Test
  void buildShortName_ValidShortName_Builds() {
    final ModuleListItemBuilder builder =
        ModuleListItem.builder().id(1L).name("TestModuleListItem");
    for (final String shortName : TestUtils.STRINGS) {
      builder.shortName(shortName);
      final ModuleListItem moduleListItem = builder.build();
      assertThat(moduleListItem, instanceOf(ModuleListItem.class));
      assertThat(moduleListItem.getShortName(), is(shortName));
    }
  }

  @Test
  void equals_AutomaticTesting() {
    EqualsVerifier.forClass(ModuleListItem.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  void moduleListItemBuilderToString_ValidData_AsExpected() {
    final ModuleListItemBuilder builder = ModuleListItem.builder();

    assertThat(
        builder.toString(),
        is(
            "ModuleListItem.ModuleListItemBuilder(id=null, name=null, shortName=null, "
                + "description=null, isSolved=null)"));
  }

  @Test
  void toString_ValidData_AsExpected() {
    final ModuleListItem testModuleListItem =
        ModuleListItem.builder().id(1L).name("TestModuleListItem").shortName("test-module").build();

    assertThat(
        testModuleListItem.toString(),
        is(
            "ModuleListItem(id=1, name=TestModuleListItem, shortName=test-module, description=null, isSolved=null)"));
  }
}
