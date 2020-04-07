package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.model.Submission.SubmissionBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("Submission unit test")
public class SubmissionTest {
  @Test
  public void build_ModuleIdNotGiven_ThrowsNullPointerException() {
    final SubmissionBuilder submissionBuilder =
        Submission.builder().userId(1L).time(LocalDateTime.MIN);
    assertThrows(NullPointerException.class, () -> submissionBuilder.build());
  }

  @Test
  public void build_timeNotGiven_ThrowsNullPointerException() {
    final SubmissionBuilder submissionBuilder =
        Submission.builder().moduleId(1L).userId(1L).flag("TestFlag");
    assertThrows(NullPointerException.class, () -> submissionBuilder.build());
  }

  @Test
  public void build_UserIdNotGiven_ThrowsNullPointerException() {
    final SubmissionBuilder submissionBuilder =
        Submission.builder().moduleId(1L).time(LocalDateTime.MIN);
    assertThrows(NullPointerException.class, () -> submissionBuilder.build());
  }

  @Test
  public void buildFlag_ValidFlag_Builds() {
    final String originalFlag = "flag";
    final String[] flagsToTest = {originalFlag, "myflag", "", "anotherflag_123", "a", "12345", " "};

    final SubmissionBuilder submissionBuilder =
        Submission.builder().userId(123L).moduleId(456L).time(LocalDateTime.MIN);

    for (String flag : flagsToTest) {

      submissionBuilder.flag(flag);

      final Submission submission = submissionBuilder.build();

      assertThat(submission, instanceOf(Submission.class));
      assertThat(submission.getFlag(), is(flag));
    }
  }

  @Test
  public void buildId_ValidId_Builds() {
    final long[] idsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    final SubmissionBuilder submissionBuilder =
        Submission.builder().flag("flag").userId(1263L).moduleId(4756L).time(LocalDateTime.MIN);

    for (final long id : idsToTest) {
      submissionBuilder.id(id);

      final Submission submission = submissionBuilder.build();

      assertThat(submission, instanceOf(Submission.class));
      assertThat(submission.getId(), is(id));
    }
  }

  @Test
  public void buildIsValid_TrueOrFalse_MatchesBuild() {
    final SubmissionBuilder submissionBuilder =
        Submission.builder().userId(1231L).flag("flag").moduleId(811L).time(LocalDateTime.MIN);

    for (boolean isValid : TestUtils.BOOLEANS) {
      submissionBuilder.isValid(isValid);
      final Submission submission = submissionBuilder.build();

      assertThat(submission, instanceOf(Submission.class));
      assertThat(submission.isValid(), is(isValid));
    }
  }

  @Test
  public void buildModuleId_NullModuleId_ThrowsNullPointerException() {
    final SubmissionBuilder submissionBuilder = Submission.builder();
    assertThrows(NullPointerException.class, () -> submissionBuilder.moduleId(null));
  }

  @Test
  public void buildModuleId_ValidModuleId_Builds() {
    final SubmissionBuilder submissionBuilder =
        Submission.builder().flag("flag").userId(456L).time(LocalDateTime.MIN);

    for (final long moduleId : TestUtils.LONGS) {
      submissionBuilder.moduleId(moduleId);

      final Submission submission = submissionBuilder.build();
      assertThat(submission, instanceOf(Submission.class));
      assertThat(submission.getModuleId(), is(moduleId));
    }
  }

  @Test
  public void buildTime_NullTime_ThrowsException() {
    final SubmissionBuilder submissionBuilder = Submission.builder();
    assertThrows(NullPointerException.class, () -> submissionBuilder.time(null));
  }

  @Test
  public void buildTime_ValidTime_Builds() {
    final long[] timesToTest = {0, 1, 2, 1000, 4000, 1581806000, 42};
    for (final long time : timesToTest) {
      final SubmissionBuilder submissionBuilder =
          Submission.builder().userId(1234L).moduleId(4556L);
      final LocalDateTime localTime =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());
      submissionBuilder.time(localTime);
      submissionBuilder.flag("flag");

      assertThat(submissionBuilder.build(), instanceOf(Submission.class));
      assertThat(submissionBuilder.build().getTime(), is(localTime));
    }
  }

  @Test
  public void buildUserId_NullUserId_ThrowsNullPointerException() {
    final SubmissionBuilder submissionBuilder = Submission.builder();
    assertThrows(NullPointerException.class, () -> submissionBuilder.userId(null));
  }

  @Test
  public void buildUserId_ValidUserId_Builds() {
    final long[] userIdsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    final SubmissionBuilder builder =
        Submission.builder().flag("flag").moduleId(4561L).time(LocalDateTime.MIN);

    for (final long userId : userIdsToTest) {
      final Submission submission = builder.userId(userId).build();

      assertThat(submission, instanceOf(Submission.class));
      assertThat(submission.getUserId(), is(userId));
    }
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(Submission.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void submissionBuilderToString_ValidData_AsExpected() {
    final SubmissionBuilder builder = Submission.builder();
    assertThat(builder.toString(), is(
        "Submission.SubmissionBuilder(id=null, userId=null, moduleId=null, time=null, isValid=false, flag=null)"));
  }

  @Test
  public void toString_ValidData_AsExpected() {
    final Submission testSubmission = Submission.builder().moduleId(11234L).flag("flag")
        .userId(67898L).time(LocalDateTime.MIN).build();

    assertThat(testSubmission.toString(),
        is("Submission(id=null, userId=67898, moduleId=11234, time=" + LocalDateTime.MIN
            + ", isValid=false, flag=flag)"));
  }

  @Test
  public void withFlag_ValidFlag_ChangesFlag() {
    final String originalFlag = "flag";
    final String[] testedFlags =
        {originalFlag, "abc123xyz789", "", "a", "Long Flag With Spaces", "12345"};

    final Submission testSubmission = Submission.builder().flag(originalFlag).userId(1345L)
        .moduleId(64627489L).time(LocalDateTime.MIN).build();

    for (final String newFlag : testedFlags) {
      final Submission changedSubmission = testSubmission.withFlag(newFlag);
      assertThat(changedSubmission.getFlag(), is(newFlag));
    }
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final long originalId = 1;
    final Long[] testedIds = {originalId, null, 0L, -1L, 1000L, -1000L, 123456789L, -12346789L};

    final Submission testSubmission = Submission.builder().userId(12393L).moduleId(4689L)
        .flag("flag").time(LocalDateTime.MIN).build();

    for (final Long newSubmissionId : testedIds) {
      final Submission changedSubmission = testSubmission.withId(newSubmissionId);
      assertThat(changedSubmission.getId(), is(newSubmissionId));
    }
  }

  @Test
  public void withModuleId_NullModuleId_ThrowsNullPointerException() {
    final Submission submission = Submission.builder().userId(1L).flag("flag")
        .time(LocalDateTime.MIN).moduleId(TestUtils.INITIAL_LONG).build();
    assertThrows(NullPointerException.class, () -> submission.withModuleId(null));
  }

  @Test
  public void withModuleId_ValidModuleId_ChangesModuleId() {
    final long originalId = 1;
    final long[] testedIds = {originalId, 0, -1, 1000, -1000, 123456789, -12346789};

    final Submission testSubmission = Submission.builder().moduleId(originalId).flag("flag")
        .userId(6736L).time(LocalDateTime.MIN.plusDays(77)).build();

    for (final long newId : testedIds) {
      final Submission changedSubmission = testSubmission.withModuleId(newId);
      assertThat(changedSubmission.getModuleId(), is(newId));
    }
  }

  @Test
  public void withTime_NullTime_ThrowsException() {
    assertThrows(NullPointerException.class, () -> Submission.builder().userId(143723L)
        .moduleId(7189L).time(LocalDateTime.MIN).build().withTime(null));
  }

  @Test
  public void withTime_ValidTime_ChangesTime() {
    final LocalDateTime originalTime = LocalDateTime.MIN;

    final LocalDateTime[] timesToTest = {originalTime, originalTime.plusHours(1),
        originalTime.plusHours(2), originalTime.plusHours(100), originalTime.plusHours(1000),
        originalTime.plusHours(5), originalTime.plusYears(70), originalTime.plusHours(9)};

    final Submission testSubmission =
        Submission.builder().userId(4L).moduleId(716789L).flag("flag").time(originalTime).build();

    for (LocalDateTime time : timesToTest) {
      final Submission changedSubmission = testSubmission.withTime(time);

      assertThat(changedSubmission.getTime(), is(time));
    }
  }

  @Test
  public void withUserId_NullUserId_ThrowsNullPointerException() {
    final Submission submission = Submission.builder().userId(TestUtils.INITIAL_LONG).flag("flag")
        .time(LocalDateTime.MIN).moduleId(1L).build();
    assertThrows(NullPointerException.class, () -> submission.withUserId(null));
  }

  @Test
  public void withUserId_ValidUserId_ChangesUserId() {
    final Submission submission = Submission.builder().userId(TestUtils.INITIAL_LONG).flag("flag")
        .time(LocalDateTime.MIN).moduleId(1L).build();

    for (final Long userId : TestUtils.LONGS) {
      final Submission newSubmission = submission.withUserId(userId);
      assertThat(newSubmission, is(instanceOf(Submission.class)));
      assertThat(newSubmission.getUserId(), is(userId));
    }
  }

  @Test
  public void withValid_ValidBoolean_ChangesIsValid() {
    final Submission testSubmission = Submission.builder().userId(15123L).moduleId(6789L)
        .flag("flag").time(LocalDateTime.MIN).build();

    for (final boolean isValid : TestUtils.BOOLEANS) {
      final Submission changedSubmission = testSubmission.withValid(isValid);
      assertThat(changedSubmission.isValid(), is(isValid));
    }
  }
}
