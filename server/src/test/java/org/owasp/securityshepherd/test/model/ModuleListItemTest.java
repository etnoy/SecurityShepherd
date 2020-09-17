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
import org.owasp.securityshepherd.module.ModuleListItem;
import org.owasp.securityshepherd.module.ModuleListItem.ModuleListItemBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("ModuleListItem unit test")
class ModuleListItemTest {
  @Test
  void build_IdNotGiven_ThrowsNullPointerException() {
    final ModuleListItemBuilder moduleListItemBuilder =
        ModuleListItem.builder().name("TestModule").shortName("test-module");
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleListItemBuilder.build());
    assertThat(thrownException.getMessage()).isEqualTo("id is marked non-null but is null");
  }

  @Test
  void build_NameNotGiven_ThrowsNullPointerException() {
    final ModuleListItemBuilder moduleListItemBuilder =
        ModuleListItem.builder().id(1L).shortName("test-module");
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleListItemBuilder.build());
    assertThat(thrownException.getMessage()).isEqualTo("name is marked non-null but is null");
  }

  @Test
  void build_ShortNameNotGiven_ThrowsNullPointerException() {
    final ModuleListItemBuilder moduleListItemBuilder =
        ModuleListItem.builder().id(1L).name("TestModuleListItem");
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleListItemBuilder.build());
    assertThat(thrownException.getMessage()).isEqualTo("shortName is marked non-null but is null");
  }

  @Test
  void buildId_ValidId_BuildsModuleListItem() {
    final ModuleListItemBuilder moduleListItemBuilder =
        ModuleListItem.builder().name("TestModule").shortName("test-module");
    for (final long id : TestUtils.LONGS) {
      final ModuleListItem moduleListItem = moduleListItemBuilder.id(id).build();
      assertThat(moduleListItem.getId()).isEqualTo(id);
    }
  }

  @Test
  void buildDescription_ValidDescription_Builds() {
    final ModuleListItemBuilder moduleListItemBuilder =
        ModuleListItem.builder().id(1L).name("TestModuleListItem").shortName("test-module");
    for (final String description : TestUtils.STRINGS_WITH_NULL) {
      moduleListItemBuilder.description(description);
      final ModuleListItem moduleListItem = moduleListItemBuilder.build();
      assertThat(moduleListItem.getDescription()).isEqualTo(description);
    }
  }

  @Test
  void buildIsSolved_TrueOrFalse_MatchesBuild() {
    final ModuleListItemBuilder builder =
        ModuleListItem.builder().id(1L).name("TestModule").shortName("test-module");
    for (final Boolean isSolved : TestUtils.BOOLEANS_WITH_NULL) {
      builder.isSolved(isSolved);

      final ModuleListItem moduleListItem = builder.build();

      assertThat(moduleListItem).isInstanceOf(ModuleListItem.class);
      assertThat(moduleListItem.getIsSolved()).isEqualTo(isSolved);
    }
  }

  @Test
  void buildId_NullId_ThrowsNullPointerException() {
    final ModuleListItemBuilder moduleListItemBuilder =
        ModuleListItem.builder().name("TestModule").shortName("test-module");
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleListItemBuilder.id(null));
    assertThat(thrownException.getMessage()).isEqualTo("id is marked non-null but is null");
  }

  @Test
  void buildName_NullName_ThrowsNullPointerException() {
    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder().shortName("test-module");
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleListItemBuilder.name(null));
    assertThat(thrownException.getMessage()).isEqualTo("name is marked non-null but is null");
  }

  @Test
  void buildName_NullShortName_ThrowsNullPointerException() {
    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder().name("TestModuleListItem");
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleListItemBuilder.shortName(null));
    assertThat(thrownException.getMessage()).isEqualTo("shortName is marked non-null but is null");
  }

  @Test
  void buildName_ValidName_Builds() {
    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder().id(1L).shortName("test-module");
    for (final String name : TestUtils.STRINGS) {
      moduleListItemBuilder.name(name);
      final ModuleListItem moduleListItem = moduleListItemBuilder.build();
      assertThat(moduleListItem).isInstanceOf(ModuleListItem.class);
      assertThat(moduleListItem.getName()).isEqualTo(name);
    }
  }

  @Test
  void buildShortName_ValidShortName_Builds() {
    final ModuleListItemBuilder moduleListItemBuilder =
        ModuleListItem.builder().id(1L).name("TestModuleListItem");
    for (final String shortName : TestUtils.STRINGS) {
      moduleListItemBuilder.shortName(shortName);
      final ModuleListItem moduleListItem = moduleListItemBuilder.build();
      assertThat(moduleListItem).isInstanceOf(ModuleListItem.class);
      assertThat(moduleListItem.getShortName()).isEqualTo(shortName);
    }
  }

  @Test
  void equals_AutomaticTesting() {
    EqualsVerifier.forClass(ModuleListItem.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  void moduleListItemBuilderToString_ValidData_AsExpected() {
    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();

    assertThat(moduleListItemBuilder)
        .hasToString(
            "ModuleListItem.ModuleListItemBuilder(id=null, name=null, shortName=null, "
                + "description=null, isSolved=null)");
  }

  @Test
  void toString_ValidData_AsExpected() {
    final ModuleListItem moduleListItem =
        ModuleListItem.builder().id(1L).name("TestModuleListItem").shortName("test-module").build();

    assertThat(moduleListItem)
        .hasToString(
            "ModuleListItem(id=1, name=TestModuleListItem, shortName=test-module, "
                + "description=null, isSolved=null)");
  }
}
