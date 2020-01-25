package org.owasp.securityshepherd.test.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.dao.mapper.UserDaoImpl;
import org.owasp.securityshepherd.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserDaoImplTest {

	@Autowired
	private UserDaoImpl userDao;

	@Test
	public void addUser_ValidUser_canListUsers() {

		UserEntity testUser1 = new UserEntity("userid123", "classid456", "addValidUser1", "hashedpass", "player", null,
				0, new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);
		UserEntity testUser2 = new UserEntity("userid456", "classid456", "addValidUser2", "hashedpass2", "player", null,
				0, new Timestamp(99), "", "login", false, false, 0, 3, 0, 0, 0);
		UserEntity testUser3 = new UserEntity("anotheruser789", "anotherclass", "addValidUser3", "ahashedpass3",
				"player", null, 0, new Timestamp(99), "", "login", false, false, 0, 3, 0, 0, 0);

		userDao.addUser(testUser1);
		userDao.addUser(testUser2);
		userDao.addUser(testUser3);

		List<UserEntity> allUsers = userDao.listUsers();

		assertTrue(allUsers.contains(testUser1), "List of users should contain added users");
		assertTrue(allUsers.contains(testUser2), "List of users should contain added users");
		assertTrue(allUsers.contains(testUser3), "List of users should contain added users");

	}

	@Test
	public void addUser_duplicateUserId_ThrowsException() {

		UserEntity testUser = new UserEntity("userid123", "classid456", "duplicateUserId", "hashedpass", "player", null,
				0, new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);

		userDao.addUser(testUser);

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.addUser(testUser);
		});

	}

	@Test
	public void addUser_duplicateUserName_ThrowsException() {

		UserEntity duplicateUserName1 = new UserEntity("userid123", "classid456", "duplicateUsername", "hashedpass",
				"player", null, 0, new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);
		UserEntity duplicateUserName2 = new UserEntity("userid456", "classid456", "duplicateUsername", "hashedpass",
				"player", null, 0, new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);

		userDao.addUser(duplicateUserName1);

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.addUser(duplicateUserName2);
		});

	}

	@Test
	public void addUser_duplicateSsoName_ThrowsException() {

		UserEntity duplicateSsoName1 = new UserEntity("userid123", "classid456", "duplicateSsoName1", "hashedpass",
				"player", "duplicateSSO", 0, new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);
		UserEntity duplicateSsoName2 = new UserEntity("userid456", "classid456", "duplicateSsoName2", "hashedpass",
				"player", "duplicateSSO", 0, new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);

		userDao.addUser(duplicateSsoName1);

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.addUser(duplicateSsoName2);
		});

	}

	@Test
	public void getUserById_validId_CanFindUser() {

		UserEntity validUser = new UserEntity("wanttofindthisuserid", "classid456", "getUserById_validId", "hashedpass",
				"player", null, 0, new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);

		userDao.addUser(validUser);

		UserEntity returnedUser = userDao.getUserById("wanttofindthisuserid");

		assertTrue(returnedUser == validUser);

	}

	@Test
	public void getUserById_invalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.getUserById("");
		});

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.getUserById(
					"thisuseridisveryverylongandshouldnotbevalidandjusttomakesurewremakeitreallyreallylong");
		});

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.getUserById(" ");
		});

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.getUserById("\t");
		});

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.getUserById("\n");
		});

	}

	@Test
	public void getUserById_nonExistentId_ReturnsNull() {

		UserEntity returnedUser = userDao.getUserById("thisuserdoesnotexist");

		assertTrue(returnedUser == null);

	}

}