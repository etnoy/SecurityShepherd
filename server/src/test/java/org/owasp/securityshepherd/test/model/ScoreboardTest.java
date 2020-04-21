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
import org.owasp.securityshepherd.model.Scoreboard;
import org.owasp.securityshepherd.model.Scoreboard.ScoreboardBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("Scoreboard unit test")
public class ScoreboardTest {

  @Test
  public void build_RankNotGiven_ThrowsNullPointerException() {
    final ScoreboardBuilder scoreboardBuilder = Scoreboard.builder().userId(1L).score(1L);
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.build());
  }

  @Test
  public void build_ScoreGiven_ThrowsNullPointerException() {
    final ScoreboardBuilder scoreboardBuilder = Scoreboard.builder().rank(1L).userId(1L);
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.build());
  }

  @Test
  public void build_userIdNotGiven_ThrowsNullPointerException() {
    final ScoreboardBuilder scoreboardBuilder = Scoreboard.builder().rank(1L).score(1L);
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.build());
  }

  @Test
  public void buildRank_NullRank_ThrowsNullPointerException() {
    final ScoreboardBuilder scoreboardBuilder = Scoreboard.builder();
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.rank(null));
  }

  @Test
  public void buildRank_ValidRank_Builds() {
    final ScoreboardBuilder scoreboardBuilder = Scoreboard.builder().userId(1L).score(1L);

    for (final long rank : TestUtils.LONGS) {
      final Scoreboard scoreboard = scoreboardBuilder.rank(rank).build();

      assertThat(scoreboard, instanceOf(Scoreboard.class));
      assertThat(scoreboard.getRank(), is(rank));
    }
  }

  @Test
  public void buildScore_NullScore_ThrowsNullPointerException() {
    final ScoreboardBuilder scoreboardBuilder = Scoreboard.builder();
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.score(null));
  }

  @Test
  public void buildScore_ValidScore_Builds() {
    final ScoreboardBuilder scoreboardBuilder = Scoreboard.builder().rank(1L).userId(1L);

    for (final long score : TestUtils.LONGS) {
      final Scoreboard scoreboard = scoreboardBuilder.score(score).build();

      assertThat(scoreboard, instanceOf(Scoreboard.class));
      assertThat(scoreboard.getScore(), is(score));
    }
  }

  @Test
  public void buildUserId_NullUserId_ThrowsNullPointerException() {
    final ScoreboardBuilder scoreboardBuilder = Scoreboard.builder();
    assertThrows(NullPointerException.class, () -> scoreboardBuilder.userId(null));
  }

  @Test
  public void buildUserId_ValidUserId_Builds() {
    final ScoreboardBuilder scoreboardBuilder = Scoreboard.builder().rank(1L).score(1L);

    for (final long userId : TestUtils.LONGS) {
      final Scoreboard scoreboard = scoreboardBuilder.userId(userId).build();

      assertThat(scoreboard, instanceOf(Scoreboard.class));
      assertThat(scoreboard.getUserId(), is(userId));
    }
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(Scoreboard.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void scoreboardBuilderToString_ValidData_AsExpected() {
    final ScoreboardBuilder builder = Scoreboard.builder().rank(17L).userId(83L).score(1L);

    assertThat(builder.toString(), is("Scoreboard.ScoreboardBuilder(rank=17, userId=83, score=1)"));
  }

  @Test
  public void toString_ValidData_AsExpected() {
    final Scoreboard testScoreboard = Scoreboard.builder().rank(17L).userId(83L).score(1L).build();

    assertThat(testScoreboard.toString(), is("Scoreboard(rank=17, userId=83, score=1)"));
  }

  @Test
  public void withRank_NullRank_ThrowsNullPointerException() {
    final Scoreboard scoreboard = Scoreboard.builder().rank(15L).userId(1L).score(15L).build();
    assertThrows(NullPointerException.class, () -> scoreboard.withRank(null));
  }

  @Test
  public void withRank_ValidRank_ChangesRank() {
    final Scoreboard scoreboard =
        Scoreboard.builder().rank(TestUtils.INITIAL_LONG).userId(163L).score(15L).build();

    for (final Long rank : TestUtils.LONGS) {
      final Scoreboard newScoreboard = scoreboard.withRank(rank);

      assertThat(newScoreboard, is(instanceOf(Scoreboard.class)));
      assertThat(newScoreboard.getRank(), is(rank));
    }
  }

  @Test
  public void withScore_NullScore_ThrowsNullPointerException() {
    final Scoreboard scoreboard =
        Scoreboard.builder().rank(1L).score(15L).userId(1L).score(15L).build();
    assertThrows(NullPointerException.class, () -> scoreboard.withScore(null));
  }

  @Test
  public void withScore_ValidScore_ChangesScore() {
    final Scoreboard scoreboard =
        Scoreboard.builder().score(TestUtils.INITIAL_LONG).rank(1L).userId(163L).build();

    for (final Long score : TestUtils.LONGS) {
      final Scoreboard newScoreboard = scoreboard.withScore(score);

      assertThat(newScoreboard, is(instanceOf(Scoreboard.class)));
      assertThat(newScoreboard.getScore(), is(score));
    }
  }

  @Test
  public void withUserId_NullUserId_ThrowsNullPointerException() {
    final Scoreboard scoreboard = Scoreboard.builder().rank(15L).userId(1L).score(15L).build();
    assertThrows(NullPointerException.class, () -> scoreboard.withUserId(null));
  }

  @Test
  public void withUserId_ValidUserId_ChangesUserId() {
    final Scoreboard scoreboard =
        Scoreboard.builder().rank(1L).userId(TestUtils.INITIAL_LONG).score(15L).build();

    for (final Long userId : TestUtils.LONGS) {
      final Scoreboard newScoreboard = scoreboard.withUserId(userId);

      assertThat(newScoreboard, is(instanceOf(Scoreboard.class)));
      assertThat(newScoreboard.getUserId(), is(userId));
    }
  }
}
