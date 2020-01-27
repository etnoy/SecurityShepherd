package org.owasp.securityshepherd.test.model;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.User;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserTest {

	@Test
	public void whenCreatingUser_acceptValidData() {

		User.builder().id("myuserid").build();

		User.builder().id("anotheruserid").classId("aclassid").name("Anotherusername").password("password")
				.role("player").ssoId("anssoid").badLoginCount(1).suspendedUntil(new Timestamp(0))
				.email("me@example.com").loginType("saml").temporaryPassword(false).temporaryUsername(false).score(1000)
				.goldMedalCount(0).silverMedalCount(0).bronzeMedalCount(0).badSubmissionCount(0).build();

		User.builder().id("abc123").classId("newclass").name("A third name with nönlätiñchåracters")
				.password("hashedpassword").role("admin").ssoId("anotherssoid").badLoginCount(123)
				.suspendedUntil(new Timestamp(12345000)).email("").loginType("login").temporaryPassword(true)
				.temporaryUsername(false).score(-1337).goldMedalCount(999).silverMedalCount(999).bronzeMedalCount(9)
				.badSubmissionCount(999).build();

		User.builder().classId(null).build();

		User.builder().ssoId(null).build();

		User.builder().suspendedUntil(null).build();

		User.builder().id(null).classId(null).name(null).password(null).ssoId(null).email(null).build();

	}

	@Test
	public void build_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().id("").build());

	}

	@Test
	public void build_InvalidClassId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().classId("").build());

	}

	@Test
	public void build_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().name("").build());

	}

	@Test
	public void build_InvalidSsoId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().ssoId("").build());

	}

	@Test
	public void build_InvalidGoldMedalCount_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().goldMedalCount(-1).build());

	}

	@Test
	public void build_InvalidSilverMedalCount_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().silverMedalCount(-1).build());

	}

	@Test
	public void build_InvalidBronzeMedalCount_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().bronzeMedalCount(-1).build());

	}

	@Test
	public void setGoldMedalCount_InvalidGoldMedalCount_ThrowsIllegalArgumentException() {

		User invalidGoldMedalCountUser = User.builder().build();

		assertThrows(IllegalArgumentException.class, () -> invalidGoldMedalCountUser.setGoldMedalCount(-1));

	}

	@Test
	public void setSilverMedalCount_InvalidSilverMedalCount_ThrowsIllegalArgumentException() {

		User invalidSilverMedalCountUser = User.builder().build();

		assertThrows(IllegalArgumentException.class, () -> invalidSilverMedalCountUser.setSilverMedalCount(-1));

	}

	@Test
	public void setBronzeMedalCount_InvalidBronzeMedalCount_ThrowsIllegalArgumentException() {

		User invalidBronzeMedalCountUser = User.builder().build();

		assertThrows(IllegalArgumentException.class, () -> invalidBronzeMedalCountUser.setBronzeMedalCount(-1));

	}

	@Test
	public void build_InvalidBadLoginCount_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().badLoginCount(-1).build());

	}
	
	@Test
	public void setBadLoginCount_InvalidBadLoginCount_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().badLoginCount(-1).build());

	}

	@Test
	public void build_InvalidBadSubmissionCount_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().badSubmissionCount(-1).build());

	}

	@Test
	public void setInvalidBadSubmissionCount_InvalidBadSubmissionCount_ThrowsIllegalArgumentException() {

		User invalidBadSubmissionUser = User.builder().build();

		assertThrows(IllegalArgumentException.class, () -> invalidBadSubmissionUser.setBadSubmissionCount(-1));

	}

	@Test
	public void equals_equalIds_returnTrue() {

		String userId = "equalIds_userId";

		User user1 = User.builder().id(userId).classId("aclassid").name("Anotherusername").password("password")
				.role("player").ssoId("ssoid").suspendedUntil(new Timestamp(0)).email("").loginType("saml")
				.temporaryPassword(false).temporaryPassword(false).goldMedalCount(0).silverMedalCount(0)
				.bronzeMedalCount(0).badSubmissionCount(0).build();

		User user2 = User.builder().id(userId).classId("newclass").name("A third name with nönlätiñchåracters")
				.password("hashedpassword").role("admin").ssoId("anotherssoid").suspendedUntil(new Timestamp(12345))
				.email("me@example.com").loginType("login").temporaryPassword(true).temporaryPassword(true)
				.goldMedalCount(999).silverMedalCount(999).bronzeMedalCount(9).badSubmissionCount(999).build();

		User user3 = User.builder().id(userId).classId("someclass").name("Athirdusername").password("anotherhash")
				.role("player").ssoId("athirdssoid").badLoginCount(3).suspendedUntil(new Timestamp(12345000)).email("")
				.loginType("login").temporaryPassword(true).temporaryPassword(true).goldMedalCount(999)
				.silverMedalCount(999).bronzeMedalCount(9).badSubmissionCount(999).build();

		User user4 = User.builder().id(userId).build();

		assertEquals(user1, user2);
		assertEquals(user1, user3);
		assertEquals(user1, user4);

		assertEquals(user2, user3);
		assertEquals(user2, user4);

		assertEquals(user3, user4);

	}

	@Test
	public void equals_differentIds_returnFalse() {

		User user1 = User.builder().id("firstuserid").build();
		User user2 = User.builder().id("seconduserid").build();

		User user3 = User.builder().id("abc123").build();
		User user4 = User.builder().id("56789").build();

		assertNotEquals(user1, user2);
		assertNotEquals(user1, user3);
		assertNotEquals(user1, user4);

		assertNotEquals(user2, user3);
		assertNotEquals(user2, user4);

		assertNotEquals(user3, user4);

	}

	@Test
	public void build_ZeroArguments_DefaultValuesPresent() {
		User user = User.builder().build();

		assertNotNull(user.getId());
		assertEquals(null, user.getClassId());
		assertNotNull(user.getName());
		assertEquals(null, user.getPassword());
		assertEquals("player", user.getRole());
		assertEquals(null, user.getSsoId());
		assertEquals(0, user.getBadLoginCount());
		assertEquals(null, user.getSuspendedUntil());
		assertEquals(null, user.getEmail());
		assertEquals("login", user.getLoginType());
		assertEquals(false, user.isTemporaryPassword());
		assertEquals(false, user.isTemporaryUsername());
		assertEquals(0, user.getScore());
		assertEquals(0, user.getGoldMedalCount());
		assertEquals(0, user.getSilverMedalCount());
		assertEquals(0, user.getBronzeMedalCount());
		assertEquals(0, user.getBadSubmissionCount());
	}

	@Test
	public void build_AllArguments_SuppliedValuesPresent() {
		User user = User.builder().id("builder_AllArguments_id").classId("builder_AllArguments_classid")
				.name("builder_AllArguments_username").password("builder_AllArguments_password").role("admin")
				.ssoId("builder_AllArguments_ssoid").badLoginCount(123).suspendedUntil(new Timestamp(1003))
				.email("builder_AllArguments@example.com").loginType("saml").temporaryPassword(true)
				.temporaryUsername(true).score(199).goldMedalCount(10).silverMedalCount(11).bronzeMedalCount(12)
				.badSubmissionCount(13).build();

		assertEquals("builder_AllArguments_id", user.getId());
		assertEquals("builder_AllArguments_classid", user.getClassId());
		assertEquals("builder_AllArguments_username", user.getName());
		assertEquals("builder_AllArguments_password", user.getPassword());
		assertEquals("admin", user.getRole());
		assertEquals("builder_AllArguments_ssoid", user.getSsoId());
		assertEquals(123, user.getBadLoginCount());
		assertEquals(new Timestamp(1003), user.getSuspendedUntil());
		assertEquals("builder_AllArguments@example.com", user.getEmail());
		assertEquals("saml", user.getLoginType());
		assertEquals(true, user.isTemporaryPassword());
		assertEquals(true, user.isTemporaryUsername());
		assertEquals(199, user.getScore());
		assertEquals(10, user.getGoldMedalCount());
		assertEquals(11, user.getSilverMedalCount());
		assertEquals(12, user.getBronzeMedalCount());
		assertEquals(13, user.getBadSubmissionCount());
	}

}