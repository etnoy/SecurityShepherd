package org.owasp.securityshepherd.test.model;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.User;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserTest {

	@Test
	public void build_AllArguments_SuppliedValuesPresent() {
		User.UserBuilder buildAllArgumentsUserBuilder = User.builder().id("builder_AllArguments_id")
				.classId("builder_AllArguments_classid").name("builder_AllArguments_username")
				.password("builder_AllArguments_password").role("admin").suspendedUntil(new Timestamp(1003))
				.email("builder_AllArguments@example.com").loginType("saml").temporaryPassword(true)
				.temporaryUsername(true).score(199).goldMedals(10).silverMedals(11).bronzeMedals(12)
				.badSubmissionCount(13).badLoginCount(47);

		User buildAllArgumentsUser = buildAllArgumentsUserBuilder.build();

		assertEquals("builder_AllArguments_id", buildAllArgumentsUser.getId());
		assertEquals("builder_AllArguments_classid", buildAllArgumentsUser.getClassId());
		assertEquals("builder_AllArguments_username", buildAllArgumentsUser.getName());
		assertEquals("builder_AllArguments_password", buildAllArgumentsUser.getPassword());
		assertEquals("admin", buildAllArgumentsUser.getRole());
		assertEquals(new Timestamp(1003), buildAllArgumentsUser.getSuspendedUntil());
		assertEquals("builder_AllArguments@example.com", buildAllArgumentsUser.getEmail());
		assertEquals("saml", buildAllArgumentsUser.getLoginType());
		assertEquals(true, buildAllArgumentsUser.isTemporaryPassword());
		assertEquals(true, buildAllArgumentsUser.isTemporaryUsername());
		assertEquals(199, buildAllArgumentsUser.getScore());
		assertEquals(10, buildAllArgumentsUser.getGoldMedals());
		assertEquals(11, buildAllArgumentsUser.getSilverMedals());
		assertEquals(12, buildAllArgumentsUser.getBronzeMedals());
		assertEquals(13, buildAllArgumentsUser.getBadSubmissionCount());
		assertEquals(47, buildAllArgumentsUser.getBadLoginCount());

	}

	@Test
	public void build_InvalidBadLoginCount_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().badLoginCount(-1).build());

	}

	@Test
	public void build_InvalidBadSubmissionCount_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().badSubmissionCount(-1).build());

	}

	@Test
	public void build_InvalidBronzeMedals_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().bronzeMedals(-1).build());

	}

	@Test
	public void build_InvalidClassId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().classId("").build());

	}

	@Test
	public void build_InvalidGoldMedals_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().goldMedals(-1).build());

	}

	@Test
	public void build_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().id("").build());

	}

	@Test
	public void build_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().name("").build());

	}

	@Test
	public void build_InvalidRole_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().role("").build());

		assertThrows(IllegalArgumentException.class, () -> User.builder().role("undefinedRole").build());

		assertThrows(IllegalArgumentException.class, () -> User.builder().role("notARole").build());

		assertThrows(IllegalArgumentException.class, () -> User.builder().role("PLAYER").build());

		assertThrows(IllegalArgumentException.class, () -> User.builder().role("Player").build());

		assertThrows(IllegalArgumentException.class, () -> User.builder().role("ADMIN").build());

		assertThrows(IllegalArgumentException.class, () -> User.builder().role("Admin").build());

	}

	@Test
	public void build_InvalidSilverMedals_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().silverMedals(-1).build());

	}

	@Test
	public void build_NullArguments_DefaultValuesPresent() {
		User buildNullArgumentsUser = User.builder().id(null).classId(null).name(null).password(null).email(null)
				.build();

		assertNotNull(buildNullArgumentsUser.getId());
		assertEquals(null, buildNullArgumentsUser.getClassId());
		assertNotNull(buildNullArgumentsUser.getName());
		assertEquals(null, buildNullArgumentsUser.getPassword());
		assertEquals("player", buildNullArgumentsUser.getRole());
		assertEquals(null, buildNullArgumentsUser.getSuspendedUntil());
		assertEquals(null, buildNullArgumentsUser.getEmail());
		assertEquals("login", buildNullArgumentsUser.getLoginType());
		assertEquals(false, buildNullArgumentsUser.isTemporaryPassword());
		assertEquals(false, buildNullArgumentsUser.isTemporaryUsername());
		assertEquals(0, buildNullArgumentsUser.getScore());
		assertEquals(0, buildNullArgumentsUser.getGoldMedals());
		assertEquals(0, buildNullArgumentsUser.getSilverMedals());
		assertEquals(0, buildNullArgumentsUser.getBronzeMedals());
		assertEquals(0, buildNullArgumentsUser.getBadSubmissionCount());
		assertEquals(0, buildNullArgumentsUser.getBadLoginCount());

	}

	@Test
	public void build_ZeroArguments_DefaultValuesPresent() {
		User buildZeroArgumentsUser = User.builder().build();

		assertNotNull(buildZeroArgumentsUser.getId());
		assertEquals(null, buildZeroArgumentsUser.getClassId());
		assertNotNull(buildZeroArgumentsUser.getName());
		assertEquals(null, buildZeroArgumentsUser.getPassword());
		assertEquals("player", buildZeroArgumentsUser.getRole());
		assertEquals(null, buildZeroArgumentsUser.getSuspendedUntil());
		assertEquals(null, buildZeroArgumentsUser.getEmail());
		assertEquals("login", buildZeroArgumentsUser.getLoginType());
		assertEquals(false, buildZeroArgumentsUser.isTemporaryPassword());
		assertEquals(false, buildZeroArgumentsUser.isTemporaryUsername());
		assertEquals(0, buildZeroArgumentsUser.getScore());
		assertEquals(0, buildZeroArgumentsUser.getGoldMedals());
		assertEquals(0, buildZeroArgumentsUser.getSilverMedals());
		assertEquals(0, buildZeroArgumentsUser.getBronzeMedals());
		assertEquals(0, buildZeroArgumentsUser.getBadSubmissionCount());
		assertEquals(0, buildZeroArgumentsUser.getBadLoginCount());

	}

	@Test
	public void equals_AutomaticTesting() {
		EqualsVerifier.forClass(User.class).withOnlyTheseFields("id").suppress(Warning.STRICT_INHERITANCE).verify();
	}

	@Test
	public void setBadLoginCount_InvalidBadLoginCount_ThrowsIllegalArgumentException() {

		User invalidBadLoginCountUser = User.builder().build();

		assertThrows(IllegalArgumentException.class, () -> invalidBadLoginCountUser.setBadLoginCount(-1));

	}

	@Test
	public void setBadLoginCount_ValidBadLoginCount_ReturnsCorrectCount() {

		User validBadLoginCountUser = User.builder().build();

		validBadLoginCountUser.setBadLoginCount(10);

		assertEquals(10, validBadLoginCountUser.getBadLoginCount());

		validBadLoginCountUser.setBadLoginCount(1234);

		assertEquals(1234, validBadLoginCountUser.getBadLoginCount());

		validBadLoginCountUser.setBadLoginCount(0);

		assertEquals(0, validBadLoginCountUser.getBadLoginCount());
	}

	@Test
	public void setBadSubmissionCount_ValidBadLoginCount_ReturnsCorrectCount() {

		User validBadSubmissionCountUser = User.builder().build();

		validBadSubmissionCountUser.setBadSubmissionCount(10);

		assertEquals(10, validBadSubmissionCountUser.getBadSubmissionCount());

		validBadSubmissionCountUser.setBadSubmissionCount(1234);

		assertEquals(1234, validBadSubmissionCountUser.getBadSubmissionCount());

		validBadSubmissionCountUser.setBadSubmissionCount(0);

		assertEquals(0, validBadSubmissionCountUser.getBadSubmissionCount());
	}

	@Test
	public void setBronzeMedals_InvalidBronzeMedals_ThrowsIllegalArgumentException() {

		User invalidBronzeMedalsUser = User.builder().build();

		assertThrows(IllegalArgumentException.class, () -> invalidBronzeMedalsUser.setBronzeMedals(-1));

	}

	@Test
	public void setBronzeMedals_ValidData_ReturnsCorrectCount() {

		User validBronzeMedalsUser = User.builder().build();

		validBronzeMedalsUser.setBronzeMedals(10);

		assertEquals(10, validBronzeMedalsUser.getBronzeMedals());

		validBronzeMedalsUser.setBronzeMedals(1234);

		assertEquals(1234, validBronzeMedalsUser.getBronzeMedals());

		validBronzeMedalsUser.setBronzeMedals(0);

		assertEquals(0, validBronzeMedalsUser.getBronzeMedals());
	}

	@Test
	public void setClassId_InvalidClassId_ThrowsIllegalArgumentException() {

		User invalidClassIdUser = User.builder().build();

		assertThrows(IllegalArgumentException.class, () -> invalidClassIdUser.setClassId(""));

	}

	@Test
	public void setClassId_validClassId_SetsClassId() {

		User validClassIdUser = User.builder().classId("theOriginalClassId").build();

		validClassIdUser.setClassId("theNewClassId");

		assertEquals("theNewClassId", validClassIdUser.getClassId());

	}

	@Test
	public void setEmail_validEmail_SetsEmail() {

		User validEmailUser = User.builder().email("theOriginalEmail").build();

		validEmailUser.setEmail("theNewEmail");

		assertEquals("theNewEmail", validEmailUser.getEmail());

	}

	@Test
	public void setGoldMedals_InvalidGoldMedals_ThrowsIllegalArgumentException() {

		User invalidGoldMedalsUser = User.builder().build();

		assertThrows(IllegalArgumentException.class, () -> invalidGoldMedalsUser.setGoldMedals(-1));

	}

	@Test
	public void setGoldMedals_ValidData_ReturnsCorrectCount() {

		User validGoldMedalsUser = User.builder().build();

		validGoldMedalsUser.setGoldMedals(10);

		assertEquals(10, validGoldMedalsUser.getGoldMedals());

		validGoldMedalsUser.setGoldMedals(1234);

		assertEquals(1234, validGoldMedalsUser.getGoldMedals());

		validGoldMedalsUser.setGoldMedals(0);

		assertEquals(0, validGoldMedalsUser.getGoldMedals());

	}

	@Test
	public void setInvalidBadSubmissionCount_InvalidBadSubmissionCount_ThrowsIllegalArgumentException() {

		User invalidBadSubmissionCountUser = User.builder().build();

		assertThrows(IllegalArgumentException.class, () -> invalidBadSubmissionCountUser.setBadSubmissionCount(-1));

	}

	@Test
	public void setRole_InvalidRole_ThrowsIllegalArgumentException() {

		User invalidRoleUser = User.builder().build();

		assertThrows(IllegalArgumentException.class, () -> invalidRoleUser.setRole(""));

		assertThrows(IllegalArgumentException.class, () -> invalidRoleUser.setRole("undefinedRole"));

		assertThrows(IllegalArgumentException.class, () -> invalidRoleUser.setRole("notARole"));

		assertThrows(IllegalArgumentException.class, () -> invalidRoleUser.setRole("PLAYER"));

		assertThrows(IllegalArgumentException.class, () -> invalidRoleUser.setRole("Player"));

		assertThrows(IllegalArgumentException.class, () -> invalidRoleUser.setRole("ADMIN"));

		assertThrows(IllegalArgumentException.class, () -> invalidRoleUser.setRole("Admin"));

	}

	@Test
	public void setRole_ValidRole_SetsRole() {

		User validRoleUser = User.builder().build();

		validRoleUser.setRole("admin");

		assertEquals("admin", validRoleUser.getRole());

		validRoleUser.setRole("player");

		assertEquals("player", validRoleUser.getRole());

	}

	@Test
	public void setScore_ValidScore_SetsScore() {

		User validScoreUser = User.builder().build();

		validScoreUser.setScore(1000);

		assertEquals(1000, validScoreUser.getScore());

		validScoreUser.setScore(1000000);

		assertEquals(1000000, validScoreUser.getScore());

		validScoreUser.setScore(-1000);

		assertEquals(-1000, validScoreUser.getScore());

		validScoreUser.setScore(-1000000);

		assertEquals(-1000000, validScoreUser.getScore());

		validScoreUser.setScore(0);

		assertEquals(0, validScoreUser.getScore());

	}

	@Test
	public void setSilverMedals_InvalidSilverMedals_ThrowsIllegalArgumentException() {

		User invalidSilverMedalsUser = User.builder().build();

		assertThrows(IllegalArgumentException.class, () -> invalidSilverMedalsUser.setSilverMedals(-1));

	}

	@Test
	public void setSilverMedals_ValidData_ReturnsCorrectCount() {

		User validSilverMedalsUser = User.builder().build();

		validSilverMedalsUser.setSilverMedals(10);

		assertEquals(10, validSilverMedalsUser.getSilverMedals());

		validSilverMedalsUser.setSilverMedals(1234);

		assertEquals(1234, validSilverMedalsUser.getSilverMedals());

		validSilverMedalsUser.setSilverMedals(0);

		assertEquals(0, validSilverMedalsUser.getSilverMedals());

	}

	@Test
	public void setSuspendedUntil_ValidTimestamp_SetsTimestamp() {

		User validSuspensionUser = User.builder().build();

		validSuspensionUser.setSuspendedUntil(new Timestamp(1000));

		assertEquals(new Timestamp(1000), validSuspensionUser.getSuspendedUntil());

		validSuspensionUser.setSuspendedUntil(new Timestamp(1000000));

		assertEquals(new Timestamp(1000000), validSuspensionUser.getSuspendedUntil());

		validSuspensionUser.setSuspendedUntil(new Timestamp(0));

		assertEquals(new Timestamp(0), validSuspensionUser.getSuspendedUntil());

	}

	@Test
	public void setTemporaryPassword_ValidData_SetsData() {

		User validTemporaryPasswordUser = User.builder().build();

		validTemporaryPasswordUser.setTemporaryPassword(true);

		assertTrue(validTemporaryPasswordUser.isTemporaryPassword());

		validTemporaryPasswordUser.setTemporaryPassword(false);

		assertFalse(validTemporaryPasswordUser.isTemporaryPassword());

	}

	@Test
	public void setTemporaryUsername_ValidData_SetsData() {

		User validTemporaryUsernameUser = User.builder().build();

		validTemporaryUsernameUser.setTemporaryUsername(true);

		assertTrue(validTemporaryUsernameUser.isTemporaryUsername());

		validTemporaryUsernameUser.setTemporaryUsername(false);

		assertFalse(validTemporaryUsernameUser.isTemporaryUsername());

	}

	@Test
	public void toString_ValidData_NotNull() {

		assertNotNull(User.builder().build().toString());

	}

	@Test
	public void userBuildertoString_ValidData_NotNull() {

		assertNotNull(User.builder().toString());

	}

}