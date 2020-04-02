package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.model.ModulePoint;
import org.owasp.securityshepherd.model.ModulePoint.ModulePointBuilder;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("ModuleScore unit test")
public class ModuleScoreTest {

  @Test
  public void build_ModuleIdNotGiven_ThrowsException() {
    assertThrows(NullPointerException.class, () -> ModulePoint.builder().rank(1).points(2).build());
  }

  @Test
  public void build_NoArguments_ThrowsException() {
    assertThrows(NullPointerException.class, () -> ModulePoint.builder().build());
  }

  @Test
  public void build_RankNotGiven_ThrowsException() {
    assertThrows(NullPointerException.class,
        () -> ModulePoint.builder().moduleId(37L).points(2).build());
  }

  @Test
  public void build_ScoreNotGiven_ThrowsException() {
    assertThrows(NullPointerException.class,
        () -> ModulePoint.builder().moduleId(22L).rank(2).build());
  }

  @Test
  public void buildId_ValidId_Builds() {
    final long[] idsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (final long id : idsToTest) {
      final ModulePointBuilder builder = ModulePoint.builder().moduleId(4546L).rank(123).points(1);

      builder.id(id);

      assertThat(builder.build(), instanceOf(ModulePoint.class));
      assertThat(builder.build().getId(), is(id));
    }
  }

  @Test
  public void buildModuleId_NullModuleId_ThrowsException() {
    assertThrows(NullPointerException.class, () -> ModulePoint.builder().moduleId(null));
  }

  @Test
  public void buildModuleId_ValidModuleId_Builds() {
    final long[] moduleIdsToTest = {0L, 1L, -1L, 1000L, -1000L, 1234567L, -1234567L, 42L};

    for (final long moduleId : moduleIdsToTest) {
      final ModulePointBuilder builder =
          ModulePoint.builder().moduleId(moduleId).rank(123).points(1);
      builder.moduleId(moduleId);

      assertThat(builder.build(), instanceOf(ModulePoint.class));
      assertThat(builder.build().getModuleId(), is(moduleId));
    }
  }

  @Test
  public void buildRank_NullRank_ThrowsException() {
    assertThrows(NullPointerException.class, () -> ModulePoint.builder().rank(null));
  }

  @Test
  public void buildRank_ValidRank_Builds() {
    final Integer[] ranksToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (final Integer rank : ranksToTest) {
      final ModulePointBuilder builder = ModulePoint.builder().moduleId(12L).points(1);
      builder.rank(rank);

      assertThat(builder.build(), instanceOf(ModulePoint.class));
      assertThat(builder.build().getRank(), is(rank));
    }
  }

  @Test
  public void buildScore_NullScore_ThrowsException() {
    assertThrows(NullPointerException.class, () -> ModulePoint.builder().points(null));
  }

  @Test
  public void buildScore_ValidScore_Builds() {
    final Integer[] pointsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (final Integer points : pointsToTest) {
      final ModulePointBuilder builder = ModulePoint.builder().moduleId(1773L).rank(1);
      builder.points(points);

      assertThat(builder.build(), instanceOf(ModulePoint.class));
      assertThat(builder.build().getPoints(), is(points));
    }
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(ModulePoint.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void moduleScoreBuilderToString_ValidData_AsExpected() {
    final ModulePointBuilder builder =
        ModulePoint.builder().id(17L).moduleId(83L).rank(1).points(54);

    assertThat(builder.toString(),
        is("ModulePoint.ModulePointBuilder(id=17, moduleId=83, rank=1, points=54)"));
  }

  @Test
  public void toString_ValidData_AsExpected() {
    final ModulePoint testModuleScore =
        ModulePoint.builder().id(1337L).moduleId(123L).rank(6789).points(987).build();

    assertThat(testModuleScore.toString(),
        is("ModulePoint(id=1337, moduleId=123, rank=6789, points=987)"));
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final long originalId = 1;
    final Long[] testedIds = {originalId, 0L, null, -1L, 1000L, -1000L, 123456789L, -12346789L};

    final ModulePoint moduleScore =
        ModulePoint.builder().id(originalId).moduleId(163L).rank(15).points(29).build();

    for (final Long id : testedIds) {
      final ModulePoint newModuleScore = moduleScore.withId(id);
      assertThat(newModuleScore.getId(), is(id));
    }
  }

  @Test
  public void withModuleId_NullModuleId_ThrowsNullPointerException() {
    final ModulePoint moduleScore =
        ModulePoint.builder().moduleId(15L).points(5).rank(15).id(29L).build();
    assertThrows(NullPointerException.class, () -> moduleScore.withModuleId(null));
  }

  @Test
  public void withModuleId_ValidModuleId_ChangesModuleId() {
    final long originalModuleId = 1L;
    final long[] testedModuleIds =
        {originalModuleId, 0L, -1L, 1000L, -1000L, 123456789L, -12346789L};

    final ModulePoint moduleScore =
        ModulePoint.builder().moduleId(originalModuleId).points(79).rank(15).id(2944L).build();

    for (final long moduleId : testedModuleIds) {
      final ModulePoint newModuleScore = moduleScore.withModuleId(moduleId);
      assertThat(newModuleScore.getModuleId(), is(moduleId));
    }
  }

  @Test
  public void withRank_NullRank_ThrowsNullPointerException() {
    final ModulePoint moduleScore =
        ModulePoint.builder().moduleId(1535L).points(326).points(5).rank(15).id(2944L).build();
    assertThrows(NullPointerException.class, () -> moduleScore.withRank(null));
  }

  @Test
  public void withRank_ValidRank_ChangesRank() {
    final Integer originalRank = 1;
    final Integer[] testedRanks = {originalRank, 0, -1, 1000, -1000, 123456789, -12346789};

    final ModulePoint moduleScore = ModulePoint.builder().points(17).moduleId(163367L).points(5)
        .rank(originalRank).id(291L).build();

    for (Integer rank : testedRanks) {
      final ModulePoint newModuleScore = moduleScore.withRank(rank);
      assertThat(newModuleScore.getRank(), is(rank));
    }
  }

  @Test
  public void withScore_NullScore_ThrowsNullPointerException() {
    final ModulePoint moduleScore =
        ModulePoint.builder().moduleId(1347635L).points(326).rank(15).id(12529L).build();
    assertThrows(NullPointerException.class, () -> moduleScore.withPoints(null));
  }

  @Test
  public void withScore_ValidScore_ChangesScore() {
    final Integer originalScore = 199;
    final Integer[] testedScores = {originalScore, 0, -1, 1000, -1000, 123456789, -12346789};

    final ModulePoint moduleScore =
        ModulePoint.builder().points(originalScore).moduleId(181653L).rank(15).id(2169L).build();

    for (Integer points : testedScores) {
      final ModulePoint newModuleScore = moduleScore.withPoints(points);
      assertThat(newModuleScore.getPoints(), is(points));
    }
  }
}
