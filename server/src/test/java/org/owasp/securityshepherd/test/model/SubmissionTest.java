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

//@ExtendWith(SpringExtension.class)
//@SpringBootTest
@DisplayName("Submission unit test")
public class SubmissionTest {
  @Test
  public void build_NoArguments_ThrowsException() {
    assertThrows(NullPointerException.class, () -> Submission.builder().build());
  }

  @Test
  public void buildFlag_ValidFlag_Builds() {
    final String[] flagsToTest = {null, "myflag", "", "anotherflag_123", "a", "12345", " "};

    for (String flag : flagsToTest) {

      final SubmissionBuilder builder =
          Submission.builder().userId(123).moduleId(456).time(LocalDateTime.MIN);

      builder.flag(flag);

      assertThat(builder.build(), instanceOf(Submission.class));
      assertThat(builder.build().getFlag(), is(flag));
    }
  }

  @Test
  public void buildId_ValidId_Builds() {
    final int[] idsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (int id : idsToTest) {

      final SubmissionBuilder builder =
          Submission.builder().userId(123).moduleId(456).time(LocalDateTime.MIN);

      builder.id(id);

      assertThat(builder.build(), instanceOf(Submission.class));
      assertThat(builder.build().getId(), is(id));
    }
  }

  @Test
  public void buildIsValid_TrueOrFalse_MatchesBuild() {
    for (boolean isValid : TestUtils.BOOLEANS) {

      final SubmissionBuilder builder =
          Submission.builder().userId(123).moduleId(456).time(LocalDateTime.MIN);

      builder.isValid(isValid);

      assertThat(builder.build(), instanceOf(Submission.class));
      assertThat(builder.build().isValid(), is(isValid));
    }
  }

  @Test
  public void buildModuleId_ValidModuleId_Builds() {
    final int[] moduleIdsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (int moduleId : moduleIdsToTest) {

      final SubmissionBuilder builder = Submission.builder().userId(456).time(LocalDateTime.MIN);

      builder.moduleId(moduleId);

      assertThat(builder.build(), instanceOf(Submission.class));
      assertThat(builder.build().getModuleId(), is(moduleId));
    }
  }

  @Test
  public void buildTime_NullTime_ThrowsException() {
    assertThrows(NullPointerException.class,
        () -> Submission.builder().userId(123).moduleId(456).time(null));
  }

  @Test
  public void buildTime_ValidTime_Builds() {
    final int[] timesToTest = {0, 1, 2, 1000, 4000, 1581806000, 42};
    for (int time : timesToTest) {

      final SubmissionBuilder builder = Submission.builder().userId(123).moduleId(456);

      final LocalDateTime localTime =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());

      builder.time(localTime);

      assertThat(builder.build(), instanceOf(Submission.class));
      assertThat(builder.build().getTime(), is(localTime));
    }
  }

  @Test
  public void buildUserId_ValidUserId_Builds() {
    final int[] userIdsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (int userId : userIdsToTest) {
      final SubmissionBuilder builder = Submission.builder().moduleId(456).time(LocalDateTime.MIN);

      builder.userId(userId);

      assertThat(builder.build(), instanceOf(Submission.class));
      assertThat(builder.build().getUserId(), is(userId));
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
        "Submission.SubmissionBuilder(id=null, userId=0, moduleId=0, time=null, isValid=false, flag=null)"));
  }

  @Test
  public void toString_ValidData_AsExpected() {
    final Submission testSubmission =
        Submission.builder().moduleId(123).userId(6789).time(LocalDateTime.MIN).build();

    assertThat(testSubmission.toString(), is("Submission(id=null, userId=6789, moduleId=123, time="
        + LocalDateTime.MIN + ", isValid=false, flag=null)"));
  }

  @Test
  public void withFlag_ValidFlag_ChangesFlag() {
    final String[] testedFlags = {"abc123xyz789", null, "", "a", "Long Flag With Spaces", "12345"};

    final Submission testSubmission = Submission.builder().userId(123).moduleId(6789)
        .time(LocalDateTime.MIN).flag("abc123xyz789").build();

    for (String newFlag : testedFlags) {
      final Submission changedSubmission = testSubmission.withFlag(newFlag);
      assertThat(changedSubmission.getFlag(), is(newFlag));
    }
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final int originalId = 1;
    final Integer[] testedIds = {originalId, null, 0, -1, 1000, -1000, 123456789, -12346789};

    final Submission testSubmission =
        Submission.builder().userId(123).moduleId(6789).time(LocalDateTime.MIN).build();

    for (Integer newId : testedIds) {
      final Submission changedSubmission = testSubmission.withId(newId);
      assertThat(changedSubmission.getId(), is(newId));
    }
  }

  @Test
  public void withModuleId_ValidModuleId_ChangesModuleId() {
    final int originalId = 1;
    final int[] testedIds = {originalId, 0, -1, 1000, -1000, 123456789, -12346789};

    final Submission testSubmission = Submission.builder().moduleId(originalId).userId(6789)
        .time(LocalDateTime.MIN.plusDays(77)).build();

    for (int newId : testedIds) {

      final Submission changedSubmission = testSubmission.withModuleId(newId);
      assertThat(changedSubmission.getModuleId(), is(newId));
    }
  }

  @Test
  public void withTime_NullTime_ThrowsException() {
    assertThrows(NullPointerException.class, () -> Submission.builder().userId(123).moduleId(6789)
        .time(LocalDateTime.MIN).build().withTime(null));
  }

  @Test
  public void withTime_ValidTime_ChangesTime() {
    final LocalDateTime originalTime = LocalDateTime.MIN;

    final LocalDateTime[] timesToTest = {originalTime, originalTime.plusHours(1),
        originalTime.plusHours(2), originalTime.plusHours(100), originalTime.plusHours(1000),
        originalTime.plusHours(5), originalTime.plusYears(70), originalTime.plusHours(9)};

    final Submission testSubmission =
        Submission.builder().userId(123).moduleId(6789).time(originalTime).build();

    for (LocalDateTime time : timesToTest) {
      final Submission changedSubmission = testSubmission.withTime(time);

      assertThat(changedSubmission.getTime(), is(time));
    }
  }

  @Test
  public void withUserId_ValidUserId_ChangesUserId() {
    final int originalId = 1;
    final int[] testedIds = {originalId, 0, -1, 1000, -1000, 123456789, -12346789};

    final Submission testSubmission =
        Submission.builder().userId(originalId).moduleId(6789).time(LocalDateTime.MIN).build();

    for (int newId : testedIds) {
      final Submission changedSubmission = testSubmission.withUserId(newId);
      assertThat(changedSubmission.getUserId(), is(newId));
    }
  }

  @Test
  public void withValid_ValidBoolean_ChangesIsValid() {
    final Submission testSubmission =
        Submission.builder().userId(123).moduleId(6789).time(LocalDateTime.MIN).build();

    for (boolean isValid : TestUtils.BOOLEANS) {
      final Submission changedSubmission = testSubmission.withValid(isValid);
      assertThat(changedSubmission.isValid(), is(isValid));
    }
  }
}
