package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.ModuleScore;
import org.owasp.securityshepherd.model.ModuleScore.ModuleScoreBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("ModuleScore unit test")
public class ModuleScoreTest {

  @Test
  public void build_ModuleIdNotGiven_ThrowsException() {
    assertThrows(NullPointerException.class, () -> ModuleScore.builder().rank(1).score(2).build());
  }

  @Test
  public void build_NoArguments_ThrowsException() {
    assertThrows(NullPointerException.class, () -> ModuleScore.builder().build());
  }

  @Test
  public void build_RankNotGiven_ThrowsException() {
    assertThrows(NullPointerException.class,
        () -> ModuleScore.builder().moduleId(1).score(2).build());
  }

  @Test
  public void build_ScoreNotGiven_ThrowsException() {
    assertThrows(NullPointerException.class,
        () -> ModuleScore.builder().moduleId(1).rank(2).build());
  }

  @Test
  public void buildId_ValidId_Builds() {
    final Integer[] idsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (final Integer id : idsToTest) {
      final ModuleScoreBuilder builder = ModuleScore.builder().moduleId(456).rank(123).score(1);

      builder.id(id);

      assertThat(builder.build(), instanceOf(ModuleScore.class));
      assertThat(builder.build().getId(), is(id));
    }
  }

  @Test
  public void buildModuleId_NullModuleId_ThrowsException() {
    assertThrows(NullPointerException.class, () -> ModuleScore.builder().moduleId(null));
  }

  @Test
  public void buildModuleId_ValidModuleId_Builds() {
    final Integer[] moduleIdsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (final Integer moduleId : moduleIdsToTest) {
      final ModuleScoreBuilder builder =
          ModuleScore.builder().moduleId(moduleId).rank(123).score(1);
      builder.moduleId(moduleId);

      assertThat(builder.build(), instanceOf(ModuleScore.class));
      assertThat(builder.build().getModuleId(), is(moduleId));
    }
  }

  @Test
  public void buildRank_NullRank_ThrowsException() {
    assertThrows(NullPointerException.class, () -> ModuleScore.builder().rank(null));
  }

  @Test
  public void buildRank_ValidRank_Builds() {
    final Integer[] ranksToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (final Integer rank : ranksToTest) {
      final ModuleScoreBuilder builder = ModuleScore.builder().moduleId(123).score(1);
      builder.rank(rank);

      assertThat(builder.build(), instanceOf(ModuleScore.class));
      assertThat(builder.build().getRank(), is(rank));
    }
  }

  @Test
  public void buildScore_NullScore_ThrowsException() {
    assertThrows(NullPointerException.class, () -> ModuleScore.builder().score(null));
  }

  @Test
  public void buildScore_ValidScore_Builds() {
    final Integer[] scoresToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (final Integer score : scoresToTest) {
      final ModuleScoreBuilder builder = ModuleScore.builder().moduleId(123).rank(1);
      builder.score(score);

      assertThat(builder.build(), instanceOf(ModuleScore.class));
      assertThat(builder.build().getScore(), is(score));
    }
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(ModuleScore.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void moduleScoreBuilderToString_ValidData_AsExpected() {
    final ModuleScoreBuilder builder = ModuleScore.builder().id(17).moduleId(83).rank(1).score(54);

    assertThat(builder.toString(),
        is("ModuleScore.ModuleScoreBuilder(id=17, moduleId=83, rank=1, score=54)"));
  }

  @Test
  public void toString_ValidData_AsExpected() {
    final ModuleScore testModuleScore =
        ModuleScore.builder().id(1337).moduleId(123).rank(6789).score(987).build();

    assertThat(testModuleScore.toString(),
        is("ModuleScore(id=1337, moduleId=123, rank=6789, score=987)"));
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final Integer originalId = 1;
    final Integer[] testedIds = {originalId, 0, null, -1, 1000, -1000, 123456789, -12346789};

    final ModuleScore moduleScore =
        ModuleScore.builder().id(originalId).moduleId(163).rank(15).score(29).build();

    for (Integer id : testedIds) {
      final ModuleScore newModuleScore = moduleScore.withId(id);
      assertThat(newModuleScore.getId(), is(id));
    }
  }

  @Test
  public void withModuleId_NullModuleId_ThrowsNullPointerException() {
    final ModuleScore moduleScore =
        ModuleScore.builder().moduleId(15).score(326).score(5).rank(15).id(29).build();
    assertThrows(NullPointerException.class, () -> moduleScore.withModuleId(null));
  }

  @Test
  public void withModuleId_ValidModuleId_ChangesModuleId() {
    final Integer originalModuleId = 1;
    final Integer[] testedModuleIds = {originalModuleId, 0, -1, 1000, -1000, 123456789, -12346789};

    final ModuleScore moduleScore =
        ModuleScore.builder().moduleId(originalModuleId).score(79).rank(15).id(29).build();

    for (Integer moduleId : testedModuleIds) {
      final ModuleScore newModuleScore = moduleScore.withModuleId(moduleId);
      assertThat(newModuleScore.getModuleId(), is(moduleId));
    }
  }

  @Test
  public void withRank_NullRank_ThrowsNullPointerException() {
    final ModuleScore moduleScore =
        ModuleScore.builder().moduleId(15).score(326).score(5).rank(15).id(29).build();
    assertThrows(NullPointerException.class, () -> moduleScore.withRank(null));
  }

  @Test
  public void withRank_ValidRank_ChangesRank() {
    final Integer originalRank = 1;
    final Integer[] testedRanks = {originalRank, 0, -1, 1000, -1000, 123456789, -12346789};

    final ModuleScore moduleScore =
        ModuleScore.builder().score(17).moduleId(163).score(5).rank(originalRank).id(29).build();

    for (Integer rank : testedRanks) {
      final ModuleScore newModuleScore = moduleScore.withRank(rank);
      assertThat(newModuleScore.getRank(), is(rank));
    }
  }

  @Test
  public void withScore_NullScore_ThrowsNullPointerException() {
    final ModuleScore moduleScore =
        ModuleScore.builder().moduleId(15).score(326).score(5).rank(15).id(29).build();
    assertThrows(NullPointerException.class, () -> moduleScore.withScore(null));
  }

  @Test
  public void withScore_ValidScore_ChangesScore() {
    final Integer originalScore = 199;
    final Integer[] testedScores = {originalScore, 0, -1, 1000, -1000, 123456789, -12346789};

    final ModuleScore moduleScore =
        ModuleScore.builder().score(originalScore).moduleId(163).rank(15).id(29).build();

    for (Integer score : testedScores) {
      final ModuleScore newModuleScore = moduleScore.withScore(score);
      assertThat(newModuleScore.getScore(), is(score));
    }
  }
}
