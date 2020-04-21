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
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.model.Correction;
import org.owasp.securityshepherd.model.Correction.CorrectionBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("Correction unit test")
public class CorrectionTest {

  @Test
  public void build_AmountNotGiven_ThrowsNullPointerException() {
    final CorrectionBuilder correctionBuilder =
        Correction.builder().time(LocalDateTime.MIN).userId(1L);
    assertThrows(NullPointerException.class, () -> correctionBuilder.build());
  }

  @Test
  public void build_TimeNotGiven_ThrowsNullPointerException() {
    final CorrectionBuilder correctionBuilder = Correction.builder().userId(1L).amount(1L);
    assertThrows(NullPointerException.class, () -> correctionBuilder.build());
  }

  @Test
  public void build_userIdNotGiven_ThrowsNullPointerException() {
    final CorrectionBuilder correctionBuilder =
        Correction.builder().amount(1L).time(LocalDateTime.MIN);
    assertThrows(NullPointerException.class, () -> correctionBuilder.build());
  }

  @Test
  public void buildAmount_NullAmount_ThrowsNullPointerException() {
    final CorrectionBuilder correctionBuilder = Correction.builder();
    assertThrows(NullPointerException.class, () -> correctionBuilder.amount(null));
  }

  @Test
  public void buildAmount_ValidAmount_BuildsCorrection() {
    final CorrectionBuilder correctionBuilder =
        Correction.builder().userId(1L).time(LocalDateTime.MIN);

    for (final long amount : TestUtils.LONGS) {
      final Correction correction = correctionBuilder.amount(amount).build();

      assertThat(correction, instanceOf(Correction.class));
      assertThat(correction.getAmount(), is(amount));
    }
  }

  @Test
  public void buildDescription_ValidDescription_BuildsCorrection() {
    final CorrectionBuilder correctionBuilder =
        Correction.builder().time(LocalDateTime.MIN).userId(1L).amount(1L);

    for (final String description : TestUtils.STRINGS_WITH_NULL) {
      final Correction correction = correctionBuilder.description(description).build();

      assertThat(correction, is(instanceOf(Correction.class)));
      assertThat(correction.getDescription(), is(description));
    }
  }

  @Test
  public void buildId_ValidId_BuildsCorrection() {
    final CorrectionBuilder correctionBuilder =
        Correction.builder().amount(1L).userId(1L).time(LocalDateTime.MIN);

    for (final Long id : TestUtils.LONGS_WITH_NULL) {
      final Correction correction = correctionBuilder.id(id).build();

      assertThat(correction, instanceOf(Correction.class));
      assertThat(correction.getId(), is(id));
    }
  }

  @Test
  public void buildTime_NullTIme_ThrowsNullPointerException() {
    final CorrectionBuilder correctionBuilder = Correction.builder();
    assertThrows(NullPointerException.class, () -> correctionBuilder.time(null));
  }

  @Test
  public void buildUserId_NullUserId_ThrowsNullPointerException() {
    final CorrectionBuilder correctionBuilder = Correction.builder();
    assertThrows(NullPointerException.class, () -> correctionBuilder.userId(null));
  }

  @Test
  public void buildUserId_ValidUserId_BuildsCorrection() {
    final CorrectionBuilder correctionBuilder =
        Correction.builder().time(LocalDateTime.MIN).amount(1L);

    for (final long userId : TestUtils.LONGS) {
      final Correction correction = correctionBuilder.userId(userId).build();

      assertThat(correction, instanceOf(Correction.class));
      assertThat(correction.getUserId(), is(userId));
    }
  }

  @Test
  public void correctionBuilderToString_ValidData_AsExpected() {
    final CorrectionBuilder builder =
        Correction.builder().time(LocalDateTime.MIN).amount(1L).userId(83L);

    assertThat(builder.toString(), is(
        "Correction.CorrectionBuilder(id=null, userId=83, amount=1, time=-999999999-01-01T00:00, description=null)"));
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(Correction.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void toString_ValidData_AsExpected() {
    final Correction testCorrection =
        Correction.builder().time(LocalDateTime.MIN).amount(1L).userId(83L).build();

    assertThat(testCorrection.toString(), is(
        "Correction(id=null, userId=83, amount=1, time=-999999999-01-01T00:00, description=null)"));
  }

  @Test
  public void withAmount_NullAmount_ThrowsNullPointerException() {
    final Correction correction =
        Correction.builder().time(LocalDateTime.MIN).amount(1L).userId(1L).build();
    assertThrows(NullPointerException.class, () -> correction.withAmount(null));
  }

  @Test
  public void withAmount_ValidAmount_ChangesAmount() {
    final Correction correction =
        Correction.builder().time(LocalDateTime.MIN).amount(1L).userId(1L).build();

    for (final Long amount : TestUtils.LONGS) {
      final Correction newCorrection = correction.withAmount(amount);

      assertThat(newCorrection, is(instanceOf(Correction.class)));
      assertThat(newCorrection.getAmount(), is(amount));
    }
  }

  @Test
  public void withDescription_ValidDescription_ChangesUserId() {
    final Correction correction = Correction.builder().time(LocalDateTime.MIN).userId(1L).amount(1L)
        .description(TestUtils.INITIAL_STRING).build();

    for (final String description : TestUtils.STRINGS_WITH_NULL) {
      final Correction newCorrection = correction.withDescription(description);

      assertThat(newCorrection, is(instanceOf(Correction.class)));
      assertThat(newCorrection.getDescription(), is(description));
    }
  }

  @Test
  public void withId_ValidId_ChangesUserId() {
    final Correction correction =
        Correction.builder().time(LocalDateTime.MIN).userId(1L).amount(1L).build();

    for (final Long id : TestUtils.LONGS_WITH_NULL) {
      final Correction newCorrection = correction.withId(id);

      assertThat(newCorrection, is(instanceOf(Correction.class)));
      assertThat(newCorrection.getId(), is(id));
    }
  }

  @Test
  public void withTime_NullTime_ThrowsNullPointerException() {
    final Correction correction =
        Correction.builder().time(LocalDateTime.MIN).userId(1L).amount(1L).build();
    assertThrows(NullPointerException.class, () -> correction.withTime(null));
  }

  @Test
  public void withTime_ValidTime_ChangesTime() {
    final Correction testCorrection = Correction.builder().time(LocalDateTime.MIN).userId(1L)
        .amount(1L).time(TestUtils.INITIAL_LOCALDATETIME).build();

    for (LocalDateTime time : TestUtils.LOCALDATETIMES) {
      final Correction changedCorrection = testCorrection.withTime(time);

      assertThat(changedCorrection.getTime(), is(time));
    }
  }

  @Test
  public void withUserId_NullUserId_ThrowsNullPointerException() {
    final Correction correction =
        Correction.builder().time(LocalDateTime.MIN).userId(1L).amount(1L).build();
    assertThrows(NullPointerException.class, () -> correction.withUserId(null));
  }

  @Test
  public void withUserId_ValidUserId_ChangesUserId() {
    final Correction correction = Correction.builder().time(LocalDateTime.MIN)
        .userId(TestUtils.INITIAL_LONG).amount(1L).build();

    for (final Long userId : TestUtils.LONGS) {
      final Correction newCorrection = correction.withUserId(userId);

      assertThat(newCorrection, is(instanceOf(Correction.class)));
      assertThat(newCorrection.getUserId(), is(userId));
    }
  }
}
