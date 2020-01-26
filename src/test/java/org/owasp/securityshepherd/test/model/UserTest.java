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
				.role("player").ssoId("anssoid").suspendedUntil(new Timestamp(0)).email("me@example.com")
				.loginType("saml").temporaryPassword(false).temporaryPassword(false).goldMedalCount(0)
				.silverMedalCount(0).bronzeMedalCount(0).badSubmissionCount(0).build();

		User.builder().id("abc123").classId("newclass").name("A third name with nönlätiñchåracters")
				.password("hashedpassword").role("admin").ssoId("anotherssoid").suspendedUntil(new Timestamp(12345000))
				.email("").loginType("login").temporaryPassword(true).temporaryPassword(true).goldMedalCount(999)
				.silverMedalCount(999).bronzeMedalCount(9).badSubmissionCount(999).build();

	}

	@Test
	public void createUser_invalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().id("").build());

	}

	@Test
	public void createUser_invalidClassId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().classId("").build());

	}

	@Test
	public void createUser_invalidSsoId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> User.builder().ssoId("").build());

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
				.role("player").ssoId("athirdssoid").suspendedUntil(new Timestamp(12345000)).email("")
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
	public void builder_ZeroArguments_DefaultValuesPresent() {
		User user = User.builder().build();

		assertNotNull(user.getId());
		assertEquals(null, user.getClassId());
		assertNotNull(user.getName());
		assertEquals(null, user.getPassword());
		assertEquals("player", user.getRole());
		assertEquals(null, user.getSsoId());
		assertEquals(0, user.getBadLoginCount());
		assertEquals(new Timestamp(0), user.getSuspendedUntil());
		assertEquals(null, user.getEmail());
		assertEquals("login", user.getLoginType());
		assertEquals(false, user.isTemporaryPassword());
		assertEquals(false, user.isTemporaryUsername());
		assertEquals(0, user.getGoldMedalCount());
		assertEquals(0, user.getSilverMedalCount());
		assertEquals(0, user.getBronzeMedalCount());
		assertEquals(0, user.getBadSubmissionCount());
	}

}