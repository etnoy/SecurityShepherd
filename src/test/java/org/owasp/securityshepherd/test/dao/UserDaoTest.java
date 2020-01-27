package org.owasp.securityshepherd.test.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.dao.model.UserDao;
import org.owasp.securityshepherd.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserDaoTest {

	@Autowired
	private UserDao userDao;

	@Test
	public void create_ValidUser_ContainedInAllUsers() {

		User validUser1 = User.builder().id("validuser1").name("A simple username").build();

		User validUser2 = User.builder().id("validuser2").classId("aclassid").name("Anotherusername")
				.password("password").role("player").suspendedUntil(new Timestamp(0)).email("me@example.com")
				.loginType("saml").temporaryPassword(false).temporaryPassword(false).goldMedals(0).silverMedals(0)
				.bronzeMedals(0).badSubmissionCount(0).build();

		User validUser3 = User.builder().id("validuser3").classId("newclass").name("nönlätiñchåracters")
				.password("hashedpassword").role("admin").suspendedUntil(new Timestamp(12345000)).email("")
				.loginType("login").temporaryPassword(true).temporaryPassword(true).goldMedals(999).silverMedals(999)
				.bronzeMedals(9).badSubmissionCount(999).build();

		userDao.create(validUser1);
		userDao.create(validUser2);
		userDao.create(validUser3);

		List<User> allUsers = userDao.getAll();

		assertTrue(allUsers.contains(validUser1), "List of users should contain added users");
		assertTrue(allUsers.contains(validUser2), "List of users should contain added users");
		assertTrue(allUsers.contains(validUser3), "List of users should contain added users");

	}

	@Test
	public void create_DuplicateUserId_ThrowsException() {

		User duplicateUserId1 = User.builder().id("duplicateUserId").name("duplicateUserId1").build();
		User duplicateUserId2 = User.builder().id("duplicateUserId").name("duplicateUserId2").build();

		userDao.create(duplicateUserId1);

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.create(duplicateUserId2);
		});

	}

	@Test
	public void create_DuplicateUserName_ThrowsException() {

		User duplicateUserName1 = User.builder().name("duplicateUserName").build();
		User duplicateUserName2 = User.builder().name("duplicateUserName").build();

		userDao.create(duplicateUserName1);

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.create(duplicateUserName2);
		});

	}

	@Test
	public void getById_ValidId_CanFindUser() {

		String idToFind = "getUserByIdvalidId";

		User getUserById_validId_User = User.builder().id(idToFind).build();

		userDao.create(getUserById_validId_User);

		User returnedUser = userDao.getById(idToFind);

		assertEquals(returnedUser, getUserById_validId_User);

	}

	@Test
	public void getById_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.getById("");
		});

	}

	@Test
	public void getById_NonExistentId_ThrowsException() {

		assertThrows(EmptyResultDataAccessException.class, () -> {
			userDao.getById("getUserById_NonExistentId");
		});

	}

	@Test
	public void deleteById_ValidId_DeletesUser() {

		String idToDelete = "delete_valid_id";

		User delete_ValidId_User = User.builder().id(idToDelete).build();

		userDao.create(delete_ValidId_User);

		userDao.deleteById(idToDelete);

		assertThrows(EmptyResultDataAccessException.class, () -> {
			userDao.getById(idToDelete);
		});

		assertFalse(userDao.containsId(idToDelete));

	}

	@Test
	public void deleteById_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.deleteById("");
		});

	}

	@Test
	public void deleteById_NonExistentId_ThrowsException() {

		assertThrows(EmptyResultDataAccessException.class, () -> {
			userDao.getById("deleteById_NonExistentId");
		});

	}

	@Test
	public void renameById_ValidIdAndName_ChangesName() {

		String idToRename = "changeNameById_ValidIdAndName";

		String oldName = "changeNameById_ValidIdAndName_oldName";
		String newName = "changeNameById_ValidIdAndName_newName";

		User insertedUser = User.builder().id(idToRename).name(oldName).build();

		userDao.create(insertedUser);

		userDao.renameById(idToRename, newName);

		User returnedUser = userDao.getById(idToRename);

		assertEquals(returnedUser, insertedUser);

		assertEquals(returnedUser.getName(), newName);

	}

	@Test
	public void renameById_DuplicateName_ThrowsException() {

		String idToRename = "changeNameById_DuplicateName_renameId";
		String idOfDuplicate = "changeNameById_DuplicateName_duplicateId";

		String oldName = "changeNameById_DuplicateName_oldName";
		String duplicateName = "changeNameById_DuplicateName_duplicateName";

		User renameUser = User.builder().id(idToRename).name(oldName).build();
		User duplicateUser = User.builder().id(idOfDuplicate).name(duplicateName).build();

		userDao.create(renameUser);
		userDao.create(duplicateUser);

		int countBefore = userDao.count();

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.renameById(idToRename, duplicateName);
		});

		int countAfter = userDao.count();

		assertEquals(countBefore, countAfter);

	}

	@Test
	public void renameById_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.renameById("", "renameById_InvalidId");
		});

	}

	@Test
	public void renameById_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.renameById("renameById_InvalidName", "");
		});

	}

	@Test
	public void renameById_NonExistentId_ThrowsException() {

		assertThrows(JdbcUpdateAffectedIncorrectNumberOfRowsException.class, () -> {
			userDao.renameById("renameById_NonExistentId_id", "renameById_NonExistentId_username");
		});

	}

	@Test
	public void getAll_ReturnsUsers() {

		assertTrue(userDao.count() == 0);

		User getAll_ReturnsUsers_user1 = User.builder().id("getAll_ReturnsUsers_user1").build();
		User getAll_ReturnsUsers_user2 = User.builder().id("getAll_ReturnsUsers_user2").build();
		User getAll_ReturnsUsers_user3 = User.builder().id("getAll_ReturnsUsers_user3").build();
		User getAll_ReturnsUsers_user4 = User.builder().id("getAll_ReturnsUsers_user4").build();

		userDao.create(getAll_ReturnsUsers_user1);
		userDao.create(getAll_ReturnsUsers_user2);
		userDao.create(getAll_ReturnsUsers_user3);
		userDao.create(getAll_ReturnsUsers_user4);

		assertTrue(userDao.containsId(getAll_ReturnsUsers_user1.getId()));
		assertTrue(userDao.containsId(getAll_ReturnsUsers_user2.getId()));
		assertTrue(userDao.containsId(getAll_ReturnsUsers_user3.getId()));
		assertTrue(userDao.containsId(getAll_ReturnsUsers_user4.getId()));

	}

	@Test
	public void containsId_InvalidId_ThrowsIllegalArgumentException() {

	}

	@Test
	public void containsId_ExistingId_ReturnsTrue() {

	}

	@Test
	public void containsId_NonExistentId_ReturnsFalse() {

	}

	@Test
	public void deleteAll_DeletesAll() {

	}

	@Test
	public void count_ReturnsCorrectNumber() {

	}

}