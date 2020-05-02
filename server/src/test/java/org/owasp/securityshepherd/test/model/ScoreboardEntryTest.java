/**
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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.scoring.ScoreboardEntry;
import org.owasp.securityshepherd.scoring.ScoreboardEntry.ScoreboardEntryBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("ScoreboardEntry unit test")
public class ScoreboardEntryTest {

  @Test
  public void build_RankNotGiven_ThrowsNullPointerException() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder().userId(1L).score(1L);
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.build());
  }

  @Test
  public void build_ScoreGiven_ThrowsNullPointerException() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder().rank(1L).userId(1L);
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.build());
  }

  @Test
  public void build_userIdNotGiven_ThrowsNullPointerException() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder().rank(1L).score(1L);
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.build());
  }

  @Test
  public void buildRank_NullRank_ThrowsNullPointerException() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder();
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.rank(null));
  }

  @Test
  public void buildRank_ValidRank_Builds() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder().userId(1L).score(1L)
        .goldMedals(0L).silverMedals(0L).bronzeMedals(0L);

    for (final long rank : TestUtils.LONGS) {
      final ScoreboardEntry scoreboard = scoreboardBuilder.rank(rank).build();

      assertThat(scoreboard, instanceOf(ScoreboardEntry.class));
      assertThat(scoreboard.getRank(), is(rank));
    }
  }

  @Test
  public void buildScore_NullScore_ThrowsNullPointerException() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder();
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.score(null));
  }

  @Test
  public void buildScore_ValidScore_Builds() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder().rank(1L).userId(1L)
        .goldMedals(0L).silverMedals(0L).bronzeMedals(0L);

    for (final long score : TestUtils.LONGS) {
      final ScoreboardEntry scoreboard = scoreboardBuilder.score(score).build();

      assertThat(scoreboard, instanceOf(ScoreboardEntry.class));
      assertThat(scoreboard.getScore(), is(score));
    }
  }

  @Test
  public void buildUserId_NullUserId_ThrowsNullPointerException() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder();
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.userId(null));
  }

  @Test
  public void buildUserId_ValidUserId_Builds() {
    final ScoreboardEntryBuilder scoreboardBuilder = ScoreboardEntry.builder().goldMedals(0L)
        .silverMedals(0L).bronzeMedals(0L).rank(1L).score(1L);

    for (final long userId : TestUtils.LONGS) {
      final ScoreboardEntry scoreboard = scoreboardBuilder.userId(userId).build();

      assertThat(scoreboard, instanceOf(ScoreboardEntry.class));
      assertThat(scoreboard.getUserId(), is(userId));
    }
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(ScoreboardEntry.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void scoreboardBuilderToString_ValidData_AsExpected() {
    final ScoreboardEntryBuilder builder =
        ScoreboardEntry.builder().rank(17L).userId(83L).score(1L);

    assertThat(builder.toString(), is(
        "ScoreboardEntry.ScoreboardEntryBuilder(rank=17, userId=83, score=1, goldMedals=null, silverMedals=null, bronzeMedals=null)"));
  }

  @Test
  public void toString_ValidData_AsExpected() {
    final ScoreboardEntry testScoreboard = ScoreboardEntry.builder().rank(17L).userId(83L)
        .goldMedals(0L).silverMedals(0L).bronzeMedals(0L).score(1L).build();

    assertThat(testScoreboard.toString(), is(
        "ScoreboardEntry(rank=17, userId=83, score=1, goldMedals=0, silverMedals=0, bronzeMedals=0)"));
  }

  @Test
  public void withRank_NullRank_ThrowsNullPointerException() {
    final ScoreboardEntry scoreboard = ScoreboardEntry.builder().rank(15L).userId(1L).score(15L)
        .goldMedals(0L).silverMedals(0L).bronzeMedals(0L).build();
    assertThrows(NullPointerException.class, () -> scoreboard.withRank(null));
  }

  @Test
  public void withRank_ValidRank_ChangesRank() {
    final ScoreboardEntry scoreboard = ScoreboardEntry.builder().rank(TestUtils.INITIAL_LONG)
        .goldMedals(0L).silverMedals(0L).bronzeMedals(0L).userId(163L).score(15L).build();

    for (final Long rank : TestUtils.LONGS) {
      final ScoreboardEntry newScoreboard = scoreboard.withRank(rank);

      assertThat(newScoreboard, is(instanceOf(ScoreboardEntry.class)));
      assertThat(newScoreboard.getRank(), is(rank));
    }
  }

  @Test
  public void withScore_NullScore_ThrowsNullPointerException() {
    final ScoreboardEntry scoreboard = ScoreboardEntry.builder().rank(1L).score(15L).userId(1L)
        .score(15L).goldMedals(0L).silverMedals(0L).bronzeMedals(0L).build();
    assertThrows(NullPointerException.class, () -> scoreboard.withScore(null));
  }

  @Test
  public void withScore_ValidScore_ChangesScore() {
    final ScoreboardEntry scoreboard = ScoreboardEntry.builder().score(TestUtils.INITIAL_LONG)
        .rank(1L).userId(163L).goldMedals(0L).silverMedals(0L).bronzeMedals(0L).build();

    for (final Long score : TestUtils.LONGS) {
      final ScoreboardEntry newScoreboard = scoreboard.withScore(score);

      assertThat(newScoreboard, is(instanceOf(ScoreboardEntry.class)));
      assertThat(newScoreboard.getScore(), is(score));
    }
  }

  @Test
  public void withUserId_NullUserId_ThrowsNullPointerException() {
    final ScoreboardEntry scoreboard = ScoreboardEntry.builder().rank(15L).userId(1L).goldMedals(0L)
        .silverMedals(0L).bronzeMedals(0L).score(15L).build();
    assertThrows(NullPointerException.class, () -> scoreboard.withUserId(null));
  }

  @Test
  public void withUserId_ValidUserId_ChangesUserId() {
    final ScoreboardEntry scoreboard = ScoreboardEntry.builder().rank(1L).goldMedals(0L)
        .silverMedals(0L).bronzeMedals(0L).userId(TestUtils.INITIAL_LONG).score(15L).build();

    for (final Long userId : TestUtils.LONGS) {
      final ScoreboardEntry newScoreboard = scoreboard.withUserId(userId);

      assertThat(newScoreboard, is(instanceOf(ScoreboardEntry.class)));
      assertThat(newScoreboard.getUserId(), is(userId));
    }
  }
}