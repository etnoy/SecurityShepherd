package org.owasp.securityshepherd.test.domainmodel;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.UserEntity;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserEntityTest {

	@Test
	public void whenCreatingUser_acceptValidData() {

		new UserEntity("myuserid", "someclassid", "Atestingusername", "", "player", "", 0, new Timestamp(0),
				"me@example.com", "login", false, false, 0, 1, 0, 0, 0);

		new UserEntity("anotheruserid", "anotherclassid", "Anotherusername", "passwordhash", "player", "saml", 0,
				new Timestamp(10), "another@example.com", "login", false, true, 99, 1, 2, 3, 5);

		new UserEntity("abc123", "newclass", "Athirdusername", "anotherhash", "admin", "", 3, new Timestamp(999),
				"user3@example.com", "login", true, false, 500, 100, 200, 300, 99);

		new UserEntity("def567", "anothernewclass", "Afourthusername", "anotherhash123", "admin", "login@sso", 3,
				new Timestamp(999), "strange.email@example.com", "saml", false, true, 600, 400, 900, 100, 1);

	}

	@Test
	public void createUser_invalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> new UserEntity("", "someclassid", "Atestingusername", "",
				"player", "", 0, new Timestamp(0), "me@example.com", "login", false, false, 0, 1, 0, 0, 0));

		assertThrows(IllegalArgumentException.class, () -> new UserEntity(" ", "someclassid", "Atestingusername", "",
				"player", "", 0, new Timestamp(0), "me@example.com", "login", false, false, 0, 1, 0, 0, 0));

		assertThrows(IllegalArgumentException.class,
				() -> new UserEntity(
						"thisuseridisveryverylongandshouldnotbevalidandjusttomakesurewremakeitreallyreallylong",
						"someclassid", "Atestingusername", "", "player", "", 0, new Timestamp(0), "me@example.com",
						"login", false, false, 0, 1, 0, 0, 0));

		assertThrows(IllegalArgumentException.class, () -> new UserEntity("\t", "someclassid", "Atestingusername", "",
				"player", "", 0, new Timestamp(0), "me@example.com", "login", false, false, 0, 1, 0, 0, 0));

	}

	@Test
	public void equals_equalIds_returnTrue() {

		String userId = "areallylonguserid";

		UserEntity user1 = new UserEntity(userId, "someclassid", "Atestingusername", "", "player", "", 0,
				new Timestamp(0), "me@example.com", "login", false, false, 0, 1, 0, 0, 0);

		UserEntity user2 = new UserEntity(userId, "anotherclassid", "Anotherusername", "passwordhash", "player", "saml",
				0, new Timestamp(10), "another@example.com", "login", false, true, 99, 1, 2, 3, 5);

		UserEntity user3 = new UserEntity(userId, "newclass", "Athirdusername", "anotherhash", "admin", "", 3,
				new Timestamp(999), "user3@example.com", "login", true, false, 500, 100, 200, 300, 99);

		UserEntity user4 = new UserEntity(userId, "anothernewclass", "Afourthusername", "anotherhash123", "admin",
				"login@sso", 3, new Timestamp(999), "strange.email@example.com", "saml", false, true, 600, 400, 900,
				100, 1);

		assertTrue(user1.equals(user2), "Users with identical userid should be equal");
		assertTrue(user1.equals(user3), "Users with identical userid should be equal");
		assertTrue(user1.equals(user4), "Users with identical userid should be equal");

		assertTrue(user2.equals(user3), "Users with identical userid should be equal");
		assertTrue(user2.equals(user4), "Users with identical userid should be equal");

		assertTrue(user3.equals(user4), "Users with identical userid should be equal");
	}

	@Test
	public void equals_differentds_returnFalse() {

		UserEntity user1 = new UserEntity("firstuserid", "someclassid", "Atestingusername", "", "player", "", 0,
				new Timestamp(0), "me@example.com", "login", false, false, 0, 1, 0, 0, 0);

		UserEntity user2 = new UserEntity("seconduserid", "anotherclassid", "Anotherusername", "passwordhash", "player",
				"saml", 0, new Timestamp(10), "another@example.com", "login", false, true, 99, 1, 2, 3, 5);

		UserEntity user3 = new UserEntity("useridwithnumbers123", "newclass", "Athirdusername", "anotherhash", "admin",
				"", 3, new Timestamp(999), "user3@example.com", "login", true, false, 500, 100, 200, 300, 99);

		UserEntity user4 = new UserEntity("1", "anothernewclass", "Afourthusername", "anotherhash123", "admin",
				"login@sso", 3, new Timestamp(999), "strange.email@example.com", "saml", false, true, 600, 400, 900,
				100, 1);

		assertTrue(!user1.equals(user2), "Users with different userid should not be equal");
		assertTrue(!user1.equals(user3), "Users with different userid should not be equal");
		assertTrue(!user1.equals(user4), "Users with different userid should not be equal");

		assertTrue(!user2.equals(user3), "Users with different userid should not be equal");
		assertTrue(!user2.equals(user4), "Users with different userid should not be equal");

		assertTrue(!user3.equals(user4), "Users with different userid should not be equal");

	}

	@Test
	public void builder_givenDefaultValues_defaultValuesPresent() {
		UserEntity user = new UserEntity().toBuilder().build();

		assertEquals(null, user.getClassId());
		assertEquals(null, user.getUserPass());
		assertEquals("player", user.getUserRole());
		assertEquals(null, user.getSsoName());
		assertEquals(0, user.getBadLoginCount());
		assertEquals(new Timestamp(0), user.getSuspendedUntil());
		assertEquals(null, user.getUserAddress());
		assertEquals("login", user.getLoginType());
		assertEquals(false, user.isTempPassword()); 
		assertEquals(false, user.isTempUsername());
		assertEquals(0, user.getGoldMedalCount());
		assertEquals(0, user.getSilverMedalCount());
		assertEquals(0, user.getBronzeMedalCount());
		assertEquals(0, user.getBadSubmissionCount());
	}

}