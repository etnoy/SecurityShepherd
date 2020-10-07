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
import org.owasp.securityshepherd.scoring.ModulePoint;
import org.owasp.securityshepherd.scoring.ModulePoint.ModulePointBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;

@DisplayName("ModulePoint unit test")
class ModulePointTest {

  @Test
  void build_ModuleIdNotGiven_ThrowsNullPointerException() {
    final ModulePointBuilder modulePointBuilder = ModulePoint.builder().rank(1).points(2);
    assertThrows(NullPointerException.class, () -> modulePointBuilder.build());
  }

  @Test
  void build_RankNotGiven_ThrowsNullPointerException() {
    final ModulePointBuilder modulePointBuilder = ModulePoint.builder().moduleId("id").points(2);
    assertThrows(NullPointerException.class, () -> modulePointBuilder.build());
  }

  @Test
  void build_ScoreNotGiven_ThrowsNullPointerException() {
    final ModulePointBuilder modulePointBuilder = ModulePoint.builder().moduleId("id").rank(2);
    assertThrows(NullPointerException.class, () -> modulePointBuilder.build());
  }

  @Test
  void buildId_ValidId_Builds() {
    final long[] idsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (final long id : idsToTest) {
      final ModulePointBuilder builder = ModulePoint.builder().moduleId("id").rank(123).points(1);

      builder.id(id);

      assertThat(builder.build()).isInstanceOf(ModulePoint.class);
      assertThat(builder.build().getId()).isEqualTo(id);
    }
  }

  @Test
  void buildModuleId_NullModuleId_ThrowsNullPointerException() {
    final ModulePointBuilder modulePointBuilder = ModulePoint.builder();
    assertThrows(NullPointerException.class, () -> modulePointBuilder.moduleId(null));
  }

  @Test
  void buildModuleId_ValidModuleId_Builds() {
    final long[] moduleIdsToTest = {0L, 1L, -1L, 1000L, -1000L, 1234567L, -1234567L, 42L};

    for (final long moduleId : moduleIdsToTest) {
      final ModulePointBuilder builder = ModulePoint.builder().moduleId("id").rank(123).points(1);

      assertThat(builder.build()).isInstanceOf(ModulePoint.class);
      assertThat(builder.build().getModuleId()).isEqualTo(moduleId);
    }
  }

  @Test
  void buildRank_NullRank_ThrowsNullPointerException() {
    final ModulePointBuilder modulePointBuilder = ModulePoint.builder();
    assertThrows(NullPointerException.class, () -> modulePointBuilder.rank(null));
  }

  @Test
  void buildRank_ValidRank_Builds() {
    final Integer[] ranksToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (final Integer rank : ranksToTest) {
      final ModulePointBuilder builder = ModulePoint.builder().moduleId("id").points(1);
      builder.rank(rank);

      assertThat(builder.build()).isInstanceOf(ModulePoint.class);
      assertThat(builder.build().getRank()).isEqualTo(rank);
    }
  }

  @Test
  void buildScore_NullScore_ThrowsNullPointerException() {
    final ModulePointBuilder modulePointBuilder = ModulePoint.builder();
    assertThrows(NullPointerException.class, () -> modulePointBuilder.points(null));
  }

  @Test
  void buildScore_ValidScore_Builds() {
    final Integer[] pointsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (final Integer points : pointsToTest) {
      final ModulePointBuilder builder = ModulePoint.builder().moduleId("id").rank(1);
      builder.points(points);

      assertThat(builder.build()).isInstanceOf(ModulePoint.class);
      assertThat(builder.build().getPoints()).isEqualTo(points);
    }
  }

  @Test
  void equals_AutomaticTesting() {
    EqualsVerifier.forClass(ModulePoint.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  void modulePointBuilderToString_ValidData_AsExpected() {
    final ModulePointBuilder builder =
        ModulePoint.builder().id(17L).moduleId("id").rank(1).points(54);

    assertThat(builder)
        .hasToString("ModulePoint.ModulePointBuilder(id=17, moduleId=83, rank=1, points=54)");
  }

  @Test
  void toString_ValidData_AsExpected() {
    final ModulePoint testModulePoint =
        ModulePoint.builder().id(1337L).moduleId("id").rank(6789).points(987).build();

    assertThat(testModulePoint)
        .hasToString("ModulePoint(id=1337, moduleId=123, rank=6789, points=987)");
  }

  @Test
  void withId_ValidId_ChangesId() {
    final long originalId = 1;
    final ModulePoint modulePoint =
        ModulePoint.builder().id(originalId).moduleId("id").rank(15).points(29).build();

    for (final Long id : TestUtils.LONGS_WITH_NULL) {
      final ModulePoint newModulePoint = modulePoint.withId(id);
      assertThat(newModulePoint.getId()).isEqualTo(id);
    }
  }

  @Test
  void withModuleId_NullModuleId_ThrowsNullPointerException() {
    final ModulePoint modulePoint =
        ModulePoint.builder().moduleId("id").points(5).rank(15).id(29L).build();
    assertThrows(NullPointerException.class, () -> modulePoint.withModuleId(null));
  }

  @Test
  void withModuleId_ValidModuleId_ChangesModuleId() {

    final ModulePoint modulePoint =
        ModulePoint.builder()
            .moduleId(TestUtils.INITIAL_ID_STRING)
            .points(79)
            .rank(15)
            .id(2944L)
            .build();

    for (final String moduleId : TestUtils.ID_STRINGS) {
      final ModulePoint newModulePoint = modulePoint.withModuleId(moduleId);
      assertThat(newModulePoint.getModuleId()).isEqualTo(moduleId);
    }
  }

  @Test
  void withRank_NullRank_ThrowsNullPointerException() {
    final ModulePoint modulePoint =
        ModulePoint.builder().moduleId("id").points(326).points(5).rank(15).id(2944L).build();
    assertThrows(NullPointerException.class, () -> modulePoint.withRank(null));
  }

  @Test
  void withRank_ValidRank_ChangesRank() {
    final Integer originalRank = 1;
    final Integer[] testedRanks = {originalRank, 0, -1, 1000, -1000, 123456789, -12346789};

    final ModulePoint modulePoint =
        ModulePoint.builder()
            .points(17)
            .moduleId("id")
            .points(5)
            .rank(originalRank)
            .id(291L)
            .build();

    for (Integer rank : testedRanks) {
      final ModulePoint newModulePoint = modulePoint.withRank(rank);
      assertThat(newModulePoint.getRank()).isEqualTo(rank);
    }
  }

  @Test
  void withScore_NullScore_ThrowsNullPointerException() {
    final ModulePoint modulePoint =
        ModulePoint.builder().moduleId("id").points(326).rank(15).id(12529L).build();
    assertThrows(NullPointerException.class, () -> modulePoint.withPoints(null));
  }

  @Test
  void withScore_ValidScore_ChangesScore() {
    final Integer originalScore = 199;
    final Integer[] testedScores = {originalScore, 0, -1, 1000, -1000, 123456789, -12346789};

    final ModulePoint modulePoint =
        ModulePoint.builder().points(originalScore).moduleId("id").rank(15).id(2169L).build();

    for (Integer points : testedScores) {
      final ModulePoint newModulePoint = modulePoint.withPoints(points);
      assertThat(newModulePoint.getPoints()).isEqualTo(points);
    }
  }
}
