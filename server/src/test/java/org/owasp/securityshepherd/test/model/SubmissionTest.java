package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.persistence.model.Submission;
import org.owasp.securityshepherd.persistence.model.Submission.SubmissionBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SubmissionTest {

	private static final boolean[] BOOLEANS = { false, true };

	@Test
	public void build_NoArguments_ThrowsException() {

		assertThrows(NullPointerException.class, () -> Submission.builder().build());

	}

	@Test
	public void buildFlag_ValidFlag_Builds() {

		final String[] flagsToTest = { null, "myflag", "", "anotherflag_123", "a", "12345", " " };

		for (String flag : flagsToTest) {

			final SubmissionBuilder builder = Submission.builder().userId(123).moduleId(456).time(new Timestamp(0));

			builder.flag(flag);

			assertThat(builder.build(), instanceOf(Submission.class));
			assertThat(builder.build().getFlag(), is(flag));

		}

	}

	@Test
	public void buildId_ValidId_Builds() {

		final int[] idsToTest = { 0, 1, -1, 1000, -1000, 1234567, -1234567, 42 };

		for (int id : idsToTest) {

			final SubmissionBuilder builder = Submission.builder().userId(123).moduleId(456).time(new Timestamp(0));

			builder.id(id);

			assertThat(builder.build(), instanceOf(Submission.class));
			assertThat(builder.build().getId(), is(id));

		}

	}

	@Test
	public void buildIsValid_TrueOrFalse_MatchesBuild() {

		for (boolean isValid : BOOLEANS) {

			final SubmissionBuilder builder = Submission.builder().userId(123).moduleId(456).time(new Timestamp(0));

			builder.isValid(isValid);

			assertThat(builder.build(), instanceOf(Submission.class));
			assertThat(builder.build().isValid(), is(isValid));

		}

	}

	@Test
	public void buildModuleId_ValidModuleId_Builds() {

		final int[] moduleIdsToTest = { 0, 1, -1, 1000, -1000, 1234567, -1234567, 42 };

		for (int moduleId : moduleIdsToTest) {

			final SubmissionBuilder builder = Submission.builder().userId(456).time(new Timestamp(0));

			builder.moduleId(moduleId);

			assertThat(builder.build(), instanceOf(Submission.class));
			assertThat(builder.build().getModuleId(), is(moduleId));

		}

	}

	@Test
	public void buildTime_NullTime_ThrowsException() {

		assertThrows(NullPointerException.class, () -> Submission.builder().userId(123).moduleId(456).time(null));

	}

	@Test
	public void buildTime_ValidTime_Builds() {

		final int[] timesToTest = { 0, 1, 2, 1000, 4000, 1581806000, 42 };

		for (int time : timesToTest) {

			final SubmissionBuilder builder = Submission.builder().userId(123).moduleId(456);

			builder.time(new Timestamp(time));

			assertThat(builder.build(), instanceOf(Submission.class));
			assertThat(builder.build().getTime(), is(new Timestamp(time)));

		}

	}

	@Test
	public void buildUserId_ValidUserId_Builds() {

		final int[] userIdsToTest = { 0, 1, -1, 1000, -1000, 1234567, -1234567, 42 };

		for (int userId : userIdsToTest) {

			final SubmissionBuilder builder = Submission.builder().moduleId(456).time(new Timestamp(0));

			builder.userId(userId);

			assertThat(builder.build(), instanceOf(Submission.class));
			assertThat(builder.build().getUserId(), is(userId));

		}

	}

	@Test
	public void equals_AutomaticTesting() {
		EqualsVerifier.forClass(Submission.class).verify();
	}

	@Test
	public void submissionBuilderToString_ValidData_AsExpected() {

		final SubmissionBuilder builder = Submission.builder();

		assertThat(builder.toString(),
				is("Submission.SubmissionBuilder(id=0, userId=0, moduleId=0, time=null, isValid=false, flag=null)"));

	}

	@Test
	public void toString_ValidData_AsExpected() {
		final Submission testSubmission = Submission.builder().moduleId(123).userId(6789).time(new Timestamp(0))
				.build();

		assertThat(testSubmission.toString(), is("Submission(id=0, userId=6789, moduleId=123, time=" + new Timestamp(0)
				+ ", isValid=false, flag=null)"));

	}

	@Test
	public void withFlag_ValidFlag_ChangesFlag() {

		final String[] testedFlags = { "abc123xyz789", null, "", "a", "Long Flag With Spaces", "12345" };

		final Submission testSubmission = Submission.builder().userId(123).moduleId(6789).time(new Timestamp(0))
				.flag("abc123xyz789").build();

		for (String newFlag : testedFlags) {

			final Submission changedSubmission = testSubmission.withFlag(newFlag);
			assertThat(changedSubmission.getFlag(), is(newFlag));

		}

	}

	@Test
	public void withId_ValidId_ChangesId() {

		final int originalId = 1;
		final int[] testedIds = { originalId, 0, -1, 1000, -1000, 123456789, -12346789 };

		final Submission testSubmission = Submission.builder().userId(123).moduleId(6789).time(new Timestamp(0))
				.build();

		for (int newId : testedIds) {

			final Submission changedSubmission = testSubmission.withId(newId);
			assertThat(changedSubmission.getId(), is(newId));

		}

	}

	@Test
	public void withModuleId_ValidModuleId_ChangesModuleId() {

		final int originalId = 1;
		final int[] testedIds = { originalId, 0, -1, 1000, -1000, 123456789, -12346789 };

		final Submission testSubmission = Submission.builder().moduleId(originalId).userId(6789).time(new Timestamp(0))
				.build();

		for (int newId : testedIds) {

			final Submission changedSubmission = testSubmission.withModuleId(newId);
			assertThat(changedSubmission.getModuleId(), is(newId));

		}

	}

	@Test
	public void withTime_NullTime_ThrowsException() {

		assertThrows(NullPointerException.class,
				() -> Submission.builder().userId(123).moduleId(6789).time(new Timestamp(0)).build().withTime(null));

	}

	@Test
	public void withTime_ValidTime_ChangesTime() {

		final Timestamp originalTime = new Timestamp(0);

		final Timestamp[] timesToTest = { originalTime, new Timestamp(1), new Timestamp(2), new Timestamp(1000),
				new Timestamp(4000), new Timestamp(1581806000), new Timestamp(42) };

		final Submission testSubmission = Submission.builder().userId(123).moduleId(6789).time(originalTime).build();

		for (Timestamp time : timesToTest) {

			final Submission changedSubmission = testSubmission.withTime(time);

			assertThat(changedSubmission.getTime(), is(time));

		}

	}

	@Test
	public void withUserId_ValidUserId_ChangesUserId() {

		final int originalId = 1;
		final int[] testedIds = { originalId, 0, -1, 1000, -1000, 123456789, -12346789 };

		final Submission testSubmission = Submission.builder().userId(originalId).moduleId(6789).time(new Timestamp(0))
				.build();

		for (int newId : testedIds) {

			final Submission changedSubmission = testSubmission.withUserId(newId);
			assertThat(changedSubmission.getUserId(), is(newId));

		}

	}

	@Test
	public void withValid_ValidBoolean_ChangesIsValid() {

		final Submission testSubmission = Submission.builder().userId(123).moduleId(6789).time(new Timestamp(0))
				.build();

		for (boolean isValid : BOOLEANS) {

			final Submission changedSubmission = testSubmission.withValid(isValid);
			assertThat(changedSubmission.isValid(), is(isValid));

		}

	}

}