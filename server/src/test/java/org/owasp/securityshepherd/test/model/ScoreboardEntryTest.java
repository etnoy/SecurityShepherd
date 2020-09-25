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
import org.owasp.securityshepherd.scoring.ScoreboardEntry;
import org.owasp.securityshepherd.scoring.ScoreboardEntry.ScoreboardEntryBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;

@DisplayName("ScoreboardEntry unit test")
class ScoreboardEntryTest {

  @Test
  void build_RankNotGiven_ThrowsNullPointerException() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder().userId(1L).score(1L);
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.build());
  }

  @Test
  void build_ScoreGiven_ThrowsNullPointerException() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder().rank(1L).userId(1L);
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.build());
  }

  @Test
  void build_userIdNotGiven_ThrowsNullPointerException() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder().rank(1L).score(1L);
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.build());
  }

  @Test
  void buildRank_NullRank_ThrowsNullPointerException() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder();
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.rank(null));
  }

  @Test
  void buildRank_ValidRank_Builds() {
    final ScoreboardEntryBuilder scoreboardBuilder =
        ScoreboardEntry.builder()
            .userId(1L)
            .score(1L)
            .goldMedals(0L)
            .silverMedals(0L)
            .bronzeMedals(0L);

    for (final long rank : TestUtils.LONGS) {
      final ScoreboardEntry scoreboard = scoreboardBuilder.rank(rank).build();

      assertThat(scoreboard).isInstanceOf(ScoreboardEntry.class);
      assertThat(scoreboard.getRank()).isEqualTo(rank);
    }
  }

  @Test
  void buildScore_NullScore_ThrowsNullPointerException() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder();
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.score(null));
  }

  @Test
  void buildScore_ValidScore_Builds() {
    final ScoreboardEntryBuilder scoreboardBuilder =
        ScoreboardEntry.builder()
            .rank(1L)
            .userId(1L)
            .goldMedals(0L)
            .silverMedals(0L)
            .bronzeMedals(0L);

    for (final long score : TestUtils.LONGS) {
      final ScoreboardEntry scoreboard = scoreboardBuilder.score(score).build();

      assertThat(scoreboard).isInstanceOf(ScoreboardEntry.class);
      assertThat(scoreboard.getScore()).isEqualTo(score);
    }
  }

  @Test
  void buildUserId_NullUserId_ThrowsNullPointerException() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder();
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.userId(null));
  }

  @Test
  void buildUserId_ValidUserId_Builds() {
    final ScoreboardEntryBuilder scoreboardBuilder =
        ScoreboardEntry.builder()
            .goldMedals(0L)
            .silverMedals(0L)
            .bronzeMedals(0L)
            .rank(1L)
            .score(1L);

    for (final long userId : TestUtils.LONGS) {
      final ScoreboardEntry scoreboard = scoreboardBuilder.userId(userId).build();

      assertThat(scoreboard).isInstanceOf(ScoreboardEntry.class);
      assertThat(scoreboard.getUserId()).isEqualTo(userId);
    }
  }

  @Test
  void equals_AutomaticTesting() {
    EqualsVerifier.forClass(ScoreboardEntry.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  void scoreboardBuilderToString_ValidData_AsExpected() {
    final ScoreboardEntryBuilder builder =
        ScoreboardEntry.builder().rank(17L).userId(83L).score(1L);

    assertThat(builder)
        .hasToString(
            "ScoreboardEntry.ScoreboardEntryBuilder(rank=17, "
                + "userId=83, score=1, goldMedals=null, "
                + "silverMedals=null, bronzeMedals=null)");
  }

  @Test
  void toString_ValidData_AsExpected() {
    final ScoreboardEntry testScoreboard =
        ScoreboardEntry.builder()
            .rank(17L)
            .userId(83L)
            .goldMedals(0L)
            .silverMedals(0L)
            .bronzeMedals(0L)
            .score(1L)
            .build();

    assertThat(testScoreboard)
        .hasToString(
            "ScoreboardEntry(rank=17, userId=83, score=1, "
                + "goldMedals=0, silverMedals=0, bronzeMedals=0)");
  }

  @Test
  void withRank_NullRank_ThrowsNullPointerException() {
    final ScoreboardEntry scoreboard =
        ScoreboardEntry.builder()
            .rank(15L)
            .userId(1L)
            .score(15L)
            .goldMedals(0L)
            .silverMedals(0L)
            .bronzeMedals(0L)
            .build();
    assertThrows(NullPointerException.class, () -> scoreboard.withRank(null));
  }

  @Test
  void withRank_ValidRank_ChangesRank() {
    final ScoreboardEntry scoreboard =
        ScoreboardEntry.builder()
            .rank(TestUtils.INITIAL_LONG)
            .goldMedals(0L)
            .silverMedals(0L)
            .bronzeMedals(0L)
            .userId(163L)
            .score(15L)
            .build();

    for (final Long rank : TestUtils.LONGS) {
      final ScoreboardEntry newScoreboard = scoreboard.withRank(rank);

      assertThat(newScoreboard).isInstanceOf(ScoreboardEntry.class);
      assertThat(newScoreboard.getRank()).isEqualTo(rank);
    }
  }

  @Test
  void withScore_NullScore_ThrowsNullPointerException() {
    final ScoreboardEntry scoreboard =
        ScoreboardEntry.builder()
            .rank(1L)
            .score(15L)
            .userId(1L)
            .score(15L)
            .goldMedals(0L)
            .silverMedals(0L)
            .bronzeMedals(0L)
            .build();
    assertThrows(NullPointerException.class, () -> scoreboard.withScore(null));
  }

  @Test
  void withScore_ValidScore_ChangesScore() {
    final ScoreboardEntry scoreboard =
        ScoreboardEntry.builder()
            .score(TestUtils.INITIAL_LONG)
            .rank(1L)
            .userId(163L)
            .goldMedals(0L)
            .silverMedals(0L)
            .bronzeMedals(0L)
            .build();

    for (final Long score : TestUtils.LONGS) {
      final ScoreboardEntry newScoreboard = scoreboard.withScore(score);

      assertThat(newScoreboard).isInstanceOf(ScoreboardEntry.class);
      assertThat(newScoreboard.getScore()).isEqualTo(score);
    }
  }

  @Test
  void withUserId_NullUserId_ThrowsNullPointerException() {
    final ScoreboardEntry scoreboard =
        ScoreboardEntry.builder()
            .rank(15L)
            .userId(1L)
            .goldMedals(0L)
            .silverMedals(0L)
            .bronzeMedals(0L)
            .score(15L)
            .build();
    assertThrows(NullPointerException.class, () -> scoreboard.withUserId(null));
  }

  @Test
  void withUserId_ValidUserId_ChangesUserId() {
    final ScoreboardEntry scoreboard =
        ScoreboardEntry.builder()
            .rank(1L)
            .goldMedals(0L)
            .silverMedals(0L)
            .bronzeMedals(0L)
            .userId(TestUtils.INITIAL_LONG)
            .score(15L)
            .build();

    for (final Long userId : TestUtils.LONGS) {
      final ScoreboardEntry newScoreboard = scoreboard.withUserId(userId);

      assertThat(newScoreboard).isInstanceOf(ScoreboardEntry.class);
      assertThat(newScoreboard.getUserId()).isEqualTo(userId);
    }
  }
}
