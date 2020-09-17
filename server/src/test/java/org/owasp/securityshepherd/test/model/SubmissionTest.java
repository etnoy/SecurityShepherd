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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.scoring.Submission;
import org.owasp.securityshepherd.scoring.Submission.SubmissionBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("Submission unit test")
class SubmissionTest {
  @Test
  void build_ModuleIdNotGiven_ThrowsNullPointerException() {
    final SubmissionBuilder submissionBuilder =
        Submission.builder().userId(1L).time(LocalDateTime.MIN);
    assertThrows(NullPointerException.class, () -> submissionBuilder.build());
  }

  @Test
  void build_timeNotGiven_ThrowsNullPointerException() {
    final SubmissionBuilder submissionBuilder =
        Submission.builder().moduleId(1L).userId(1L).flag("TestFlag");
    assertThrows(NullPointerException.class, () -> submissionBuilder.build());
  }

  @Test
  void build_UserIdNotGiven_ThrowsNullPointerException() {
    final SubmissionBuilder submissionBuilder =
        Submission.builder().moduleId(1L).time(LocalDateTime.MIN);
    assertThrows(NullPointerException.class, () -> submissionBuilder.build());
  }

  @Test
  void buildFlag_ValidFlag_Builds() {
    final String originalFlag = "flag";
    final String[] flagsToTest = {originalFlag, "myflag", "", "anotherflag_123", "a", "12345", " "};

    final SubmissionBuilder submissionBuilder =
        Submission.builder().userId(123L).moduleId(456L).time(LocalDateTime.MIN);

    for (String flag : flagsToTest) {

      submissionBuilder.flag(flag);

      final Submission submission = submissionBuilder.build();

      assertThat(submission).isInstanceOf(Submission.class);
      assertThat(submission.getFlag()).isEqualTo(flag);
    }
  }

  @Test
  void buildId_ValidId_Builds() {
    final long[] idsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    final SubmissionBuilder submissionBuilder =
        Submission.builder().flag("flag").userId(1263L).moduleId(4756L).time(LocalDateTime.MIN);

    for (final long id : idsToTest) {
      submissionBuilder.id(id);

      final Submission submission = submissionBuilder.build();

      assertThat(submission).isInstanceOf(Submission.class);
      assertThat(submission.getId()).isEqualTo(id);
    }
  }

  @Test
  void buildIsValid_TrueOrFalse_MatchesBuild() {
    final SubmissionBuilder submissionBuilder =
        Submission.builder().userId(1231L).flag("flag").moduleId(811L).time(LocalDateTime.MIN);

    for (boolean isValid : TestUtils.BOOLEANS) {
      submissionBuilder.isValid(isValid);
      final Submission submission = submissionBuilder.build();

      assertThat(submission).isInstanceOf(Submission.class);
      assertThat(submission.isValid()).isEqualTo(isValid);
    }
  }

  @Test
  void buildModuleId_NullModuleId_ThrowsNullPointerException() {
    final SubmissionBuilder submissionBuilder = Submission.builder();
    assertThrows(NullPointerException.class, () -> submissionBuilder.moduleId(null));
  }

  @Test
  void buildModuleId_ValidModuleId_Builds() {
    final SubmissionBuilder submissionBuilder =
        Submission.builder().flag("flag").userId(456L).time(LocalDateTime.MIN);

    for (final long moduleId : TestUtils.LONGS) {
      submissionBuilder.moduleId(moduleId);

      final Submission submission = submissionBuilder.build();
      assertThat(submission).isInstanceOf(Submission.class);
      assertThat(submission.getModuleId()).isEqualTo(moduleId);
    }
  }

  @Test
  void buildTime_NullTime_ThrowsNullPointerException() {
    final SubmissionBuilder submissionBuilder = Submission.builder();
    assertThrows(NullPointerException.class, () -> submissionBuilder.time(null));
  }

  @Test
  void buildTime_ValidTime_Builds() {
    final long[] timesToTest = {0, 1, 2, 1000, 4000, 1581806000, 42};
    for (final long time : timesToTest) {
      final SubmissionBuilder submissionBuilder =
          Submission.builder().userId(1234L).moduleId(4556L);
      final LocalDateTime localTime =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());
      submissionBuilder.time(localTime);
      submissionBuilder.flag("flag");

      assertThat(submissionBuilder.build()).isInstanceOf(Submission.class);
      assertThat(submissionBuilder.build().getTime()).isEqualTo(localTime);
    }
  }

  @Test
  void buildUserId_NullUserId_ThrowsNullPointerException() {
    final SubmissionBuilder submissionBuilder = Submission.builder();
    assertThrows(NullPointerException.class, () -> submissionBuilder.userId(null));
  }

  @Test
  void buildUserId_ValidUserId_Builds() {
    final long[] userIdsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    final SubmissionBuilder builder =
        Submission.builder().flag("flag").moduleId(4561L).time(LocalDateTime.MIN);

    for (final long userId : userIdsToTest) {
      final Submission submission = builder.userId(userId).build();

      assertThat(submission).isInstanceOf(Submission.class);
      assertThat(submission.getUserId()).isEqualTo(userId);
    }
  }

  @Test
  void equals_AutomaticTesting() {
    EqualsVerifier.forClass(Submission.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  void submissionBuilderToString_ValidData_AsExpected() {
    final SubmissionBuilder builder = Submission.builder();
    assertThat(builder)
        .hasToString(
            "Submission.SubmissionBuilder(id=null, userId=null, "
                + "moduleId=null, time=null, isValid=false, flag=null)");
  }

  @Test
  void toString_ValidData_AsExpected() {
    final Submission testSubmission =
        Submission.builder()
            .moduleId(11234L)
            .flag("flag")
            .userId(67898L)
            .time(LocalDateTime.MIN)
            .build();

    assertThat(testSubmission)
        .hasToString(
            "Submission(id=null, userId=67898, moduleId=11234, time="
                + LocalDateTime.MIN
                + ", isValid=false, flag=flag)");
  }

  @Test
  void withFlag_ValidFlag_ChangesFlag() {
    final String originalFlag = "flag";
    final String[] testedFlags = {
      originalFlag, "abc123xyz789", "", "a", "Long Flag With Spaces", "12345"
    };

    final Submission testSubmission =
        Submission.builder()
            .flag(originalFlag)
            .userId(1345L)
            .moduleId(64627489L)
            .time(LocalDateTime.MIN)
            .build();

    for (final String newFlag : testedFlags) {
      final Submission changedSubmission = testSubmission.withFlag(newFlag);
      assertThat(changedSubmission.getFlag()).isEqualTo(newFlag);
    }
  }

  @Test
  void withId_ValidId_ChangesId() {
    final long originalId = 1;
    final Long[] testedIds = {originalId, null, 0L, -1L, 1000L, -1000L, 123456789L, -12346789L};

    final Submission testSubmission =
        Submission.builder()
            .userId(12393L)
            .moduleId(4689L)
            .flag("flag")
            .time(LocalDateTime.MIN)
            .build();

    for (final Long newSubmissionId : testedIds) {
      final Submission changedSubmission = testSubmission.withId(newSubmissionId);
      assertThat(changedSubmission.getId()).isEqualTo(newSubmissionId);
    }
  }

  @Test
  void withModuleId_NullModuleId_ThrowsNullPointerException() {
    final Submission submission =
        Submission.builder()
            .userId(1L)
            .flag("flag")
            .time(LocalDateTime.MIN)
            .moduleId(TestUtils.INITIAL_LONG)
            .build();
    assertThrows(NullPointerException.class, () -> submission.withModuleId(null));
  }

  @Test
  void withModuleId_ValidModuleId_ChangesModuleId() {
    final long originalId = 1;
    final long[] testedIds = {originalId, 0, -1, 1000, -1000, 123456789, -12346789};

    final Submission testSubmission =
        Submission.builder()
            .moduleId(originalId)
            .flag("flag")
            .userId(6736L)
            .time(LocalDateTime.MIN.plusDays(77))
            .build();

    for (final long newId : testedIds) {
      final Submission changedSubmission = testSubmission.withModuleId(newId);
      assertThat(changedSubmission.getModuleId()).isEqualTo(newId);
    }
  }

  @Test
  void withTime_NullTime_ThrowsNullPointerException() {
    final Submission submission =
        Submission.builder()
            .userId(TestUtils.INITIAL_LONG)
            .flag("flag")
            .time(LocalDateTime.MIN)
            .moduleId(1L)
            .build();
    assertThrows(NullPointerException.class, () -> submission.withTime(null));
  }

  @Test
  void withTime_ValidTime_ChangesTime() {
    final Submission testSubmission =
        Submission.builder()
            .userId(4L)
            .moduleId(716789L)
            .flag("flag")
            .time(TestUtils.INITIAL_LOCALDATETIME)
            .build();

    for (LocalDateTime time : TestUtils.LOCALDATETIMES) {
      final Submission changedSubmission = testSubmission.withTime(time);

      assertThat(changedSubmission.getTime()).isEqualTo(time);
    }
  }

  @Test
  void withUserId_NullUserId_ThrowsNullPointerException() {
    final Submission submission =
        Submission.builder()
            .userId(TestUtils.INITIAL_LONG)
            .flag("flag")
            .time(LocalDateTime.MIN)
            .moduleId(1L)
            .build();
    assertThrows(NullPointerException.class, () -> submission.withUserId(null));
  }

  @Test
  void withUserId_ValidUserId_ChangesUserId() {
    final Submission submission =
        Submission.builder()
            .userId(TestUtils.INITIAL_LONG)
            .flag("flag")
            .time(LocalDateTime.MIN)
            .moduleId(1L)
            .build();

    for (final Long userId : TestUtils.LONGS) {
      final Submission newSubmission = submission.withUserId(userId);
      assertThat(newSubmission).isInstanceOf(Submission.class);
      assertThat(newSubmission.getUserId()).isEqualTo(userId);
    }
  }

  @Test
  void withValid_ValidBoolean_ChangesIsValid() {
    final Submission testSubmission =
        Submission.builder()
            .userId(15123L)
            .moduleId(6789L)
            .flag("flag")
            .time(LocalDateTime.MIN)
            .build();

    for (final boolean isValid : TestUtils.BOOLEANS) {
      final Submission changedSubmission = testSubmission.withValid(isValid);
      assertThat(changedSubmission.isValid()).isEqualTo(isValid);
    }
  }
}
