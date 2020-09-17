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
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.scoring.Correction;
import org.owasp.securityshepherd.scoring.Correction.CorrectionBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("Correction unit test")
class CorrectionTest {

  @Test
  void build_AmountNotGiven_ThrowsNullPointerException() {
    final CorrectionBuilder correctionBuilder =
        Correction.builder().time(LocalDateTime.MIN).userId(1L);
    assertThrows(NullPointerException.class, () -> correctionBuilder.build());
  }

  @Test
  void build_TimeNotGiven_ThrowsNullPointerException() {
    final CorrectionBuilder correctionBuilder = Correction.builder().userId(1L).amount(1L);
    assertThrows(NullPointerException.class, () -> correctionBuilder.build());
  }

  @Test
  void build_userIdNotGiven_ThrowsNullPointerException() {
    final CorrectionBuilder correctionBuilder =
        Correction.builder().amount(1L).time(LocalDateTime.MIN);
    assertThrows(NullPointerException.class, () -> correctionBuilder.build());
  }

  @Test
  void buildAmount_NullAmount_ThrowsNullPointerException() {
    final CorrectionBuilder correctionBuilder = Correction.builder();
    assertThrows(NullPointerException.class, () -> correctionBuilder.amount(null));
  }

  @Test
  void buildAmount_ValidAmount_BuildsCorrection() {
    final CorrectionBuilder correctionBuilder =
        Correction.builder().userId(1L).time(LocalDateTime.MIN);

    for (final long amount : TestUtils.LONGS) {
      final Correction correction = correctionBuilder.amount(amount).build();

      assertThat(correction).isInstanceOf(Correction.class);
      assertThat(correction.getAmount()).isEqualTo(amount);
    }
  }

  @Test
  void buildDescription_ValidDescription_BuildsCorrection() {
    final CorrectionBuilder correctionBuilder =
        Correction.builder().time(LocalDateTime.MIN).userId(1L).amount(1L);

    for (final String description : TestUtils.STRINGS_WITH_NULL) {
      final Correction correction = correctionBuilder.description(description).build();

      assertThat(correction).isInstanceOf(Correction.class);
      assertThat(correction.getDescription()).isEqualTo(description);
    }
  }

  @Test
  void buildId_ValidId_BuildsCorrection() {
    final CorrectionBuilder correctionBuilder =
        Correction.builder().amount(1L).userId(1L).time(LocalDateTime.MIN);

    for (final Long id : TestUtils.LONGS_WITH_NULL) {
      final Correction correction = correctionBuilder.id(id).build();

      assertThat(correction).isInstanceOf(Correction.class);
      assertThat(correction.getId()).isEqualTo(id);
    }
  }

  @Test
  void buildTime_NullTIme_ThrowsNullPointerException() {
    final CorrectionBuilder correctionBuilder = Correction.builder();
    assertThrows(NullPointerException.class, () -> correctionBuilder.time(null));
  }

  @Test
  void buildUserId_NullUserId_ThrowsNullPointerException() {
    final CorrectionBuilder correctionBuilder = Correction.builder();
    assertThrows(NullPointerException.class, () -> correctionBuilder.userId(null));
  }

  @Test
  void buildUserId_ValidUserId_BuildsCorrection() {
    final CorrectionBuilder correctionBuilder =
        Correction.builder().time(LocalDateTime.MIN).amount(1L);

    for (final long userId : TestUtils.LONGS) {
      final Correction correction = correctionBuilder.userId(userId).build();

      assertThat(correction).isInstanceOf(Correction.class);
      assertThat(correction.getUserId()).isEqualTo(userId);
    }
  }

  @Test
  void correctionBuilderToString_ValidData_AsExpected() {
    final CorrectionBuilder builder =
        Correction.builder().time(LocalDateTime.MIN).amount(1L).userId(83L);

    assertThat(builder.toString())
        .hasToString(
            "Correction.CorrectionBuilder(id=null, userId=83, amount=1, "
                + "time=-999999999-01-01T00:00, description=null)");
  }

  @Test
  void equals_AutomaticTesting() {
    EqualsVerifier.forClass(Correction.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  void toString_ValidData_AsExpected() {
    final Correction testCorrection =
        Correction.builder().time(LocalDateTime.MIN).amount(1L).userId(83L).build();

    assertThat(testCorrection.toString())
        .hasToString(
            "Correction(id=null, userId=83, amount=1, "
            + "time=-999999999-01-01T00:00, description=null)");
  }

  @Test
  void withAmount_NullAmount_ThrowsNullPointerException() {
    final Correction correction =
        Correction.builder().time(LocalDateTime.MIN).amount(1L).userId(1L).build();
    assertThrows(NullPointerException.class, () -> correction.withAmount(null));
  }

  @Test
  void withAmount_ValidAmount_ChangesAmount() {
    final Correction correction =
        Correction.builder().time(LocalDateTime.MIN).amount(1L).userId(1L).build();

    for (final Long amount : TestUtils.LONGS) {
      final Correction newCorrection = correction.withAmount(amount);

      assertThat(newCorrection).isInstanceOf(Correction.class);
      assertThat(newCorrection.getAmount()).isEqualTo(amount);
    }
  }

  @Test
  void withDescription_ValidDescription_ChangesUserId() {
    final Correction correction =
        Correction.builder()
            .time(LocalDateTime.MIN)
            .userId(1L)
            .amount(1L)
            .description(TestUtils.INITIAL_STRING)
            .build();

    for (final String description : TestUtils.STRINGS_WITH_NULL) {
      final Correction newCorrection = correction.withDescription(description);

      assertThat(newCorrection).isInstanceOf(Correction.class);
      assertThat(newCorrection.getDescription()).isEqualTo(description);
    }
  }

  @Test
  void withId_ValidId_ChangesUserId() {
    final Correction correction =
        Correction.builder().time(LocalDateTime.MIN).userId(1L).amount(1L).build();

    for (final Long id : TestUtils.LONGS_WITH_NULL) {
      final Correction newCorrection = correction.withId(id);

      assertThat(newCorrection).isInstanceOf(Correction.class);
      assertThat(newCorrection.getId()).isEqualTo(id);
    }
  }

  @Test
  void withTime_NullTime_ThrowsNullPointerException() {
    final Correction correction =
        Correction.builder().time(LocalDateTime.MIN).userId(1L).amount(1L).build();
    assertThrows(NullPointerException.class, () -> correction.withTime(null));
  }

  @Test
  void withTime_ValidTime_ChangesTime() {
    final Correction testCorrection =
        Correction.builder()
            .time(LocalDateTime.MIN)
            .userId(1L)
            .amount(1L)
            .time(TestUtils.INITIAL_LOCALDATETIME)
            .build();

    for (LocalDateTime time : TestUtils.LOCALDATETIMES) {
      final Correction changedCorrection = testCorrection.withTime(time);

      assertThat(changedCorrection.getTime()).isEqualTo(time);
    }
  }

  @Test
  void withUserId_NullUserId_ThrowsNullPointerException() {
    final Correction correction =
        Correction.builder().time(LocalDateTime.MIN).userId(1L).amount(1L).build();
    assertThrows(NullPointerException.class, () -> correction.withUserId(null));
  }

  @Test
  void withUserId_ValidUserId_ChangesUserId() {
    final Correction correction =
        Correction.builder()
            .time(LocalDateTime.MIN)
            .userId(TestUtils.INITIAL_LONG)
            .amount(1L)
            .build();

    for (final Long userId : TestUtils.LONGS) {
      final Correction newCorrection = correction.withUserId(userId);

      assertThat(newCorrection).isInstanceOf(Correction.class);
      assertThat(newCorrection.getUserId()).isEqualTo(userId);
    }
  }
}
