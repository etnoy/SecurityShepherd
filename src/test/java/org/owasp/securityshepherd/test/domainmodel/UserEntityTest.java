package org.owasp.securityshepherd.test.domainmodel;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	public void whenCreatingUser_rejectEmptyUserId() {

		assertThrows(IllegalArgumentException.class, () -> new UserEntity("", "someclassid", "Atestingusername", "",
				"player", "", 0, new Timestamp(0), "me@example.com", "login", false, false, 0, 1, 0, 0, 0));

	}

	@Test
	public void whenComparingUseres_equalIdsAreEqual() {

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
	public void testUsersNotEqual() {

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

}