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
import static org.junit.jupiter.api.Assertions.assertThrows;

import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.module.ModuleListItem;
import org.owasp.securityshepherd.module.ModuleListItem.ModuleListItemBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;

@DisplayName("ModuleListItem unit test")
class ModuleListItemTest {
  @Test
  void build_IdNotGiven_ThrowsNullPointerException() {
    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleListItemBuilder.build());
    assertThat(thrownException.getMessage()).isEqualTo("id is marked non-null but is null");
  }

  @Test
  void buildId_ValidId_BuildsModuleListItem() {
    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();
    for (final String name : TestUtils.NAMES) {
      final ModuleListItem moduleListItem = moduleListItemBuilder.name(name).build();
      assertThat(moduleListItem.getName()).isEqualTo(name);
    }
  }

  @Test
  void buildIsSolved_TrueOrFalse_MatchesBuild() {
    final ModuleListItemBuilder builder = ModuleListItem.builder().name("id");
    for (final Boolean isSolved : TestUtils.BOOLEANS_WITH_NULL) {
      builder.isSolved(isSolved);

      final ModuleListItem moduleListItem = builder.build();

      assertThat(moduleListItem).isInstanceOf(ModuleListItem.class);
      assertThat(moduleListItem.getIsSolved()).isEqualTo(isSolved);
    }
  }

  @Test
  void buildId_NullId_ThrowsNullPointerException() {
    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();
    Throwable thrownException =
        assertThrows(NullPointerException.class, () -> moduleListItemBuilder.name(null));
    assertThat(thrownException.getMessage()).isEqualTo("id is marked non-null but is null");
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
    final ModuleListItem moduleListItem = ModuleListItem.builder().name("id").build();

    assertThat(moduleListItem).hasToString("ModuleListItem(id=id, isSolved=null)");
  }
}
