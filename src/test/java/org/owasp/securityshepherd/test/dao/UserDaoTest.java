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
	public void containsId_ExistingId_ReturnsTrue() {

		User containsIdExistingIdUser = User.builder().id("containsId_ExistingId").build();

		assertFalse(userDao.containsId("containsId_ExistingId"));

		userDao.create(containsIdExistingIdUser);

		assertTrue(userDao.containsId("containsId_ExistingId"));

		User containsIdExistingIdLongerIdUser = User.builder().id("containsId_ExistingId_LongerId").build();

		assertFalse(userDao.containsId("containsId_ExistingId_LongerId"));

		userDao.create(containsIdExistingIdLongerIdUser);

		assertTrue(userDao.containsId("containsId_ExistingId_LongerId"));

	}

	@Test
	public void containsId_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.containsId("");
		});

	}

	@Test
	public void containsId_NonExistentId_ReturnsFalse() {

		assertFalse(userDao.containsId("containsId_NonExistentId"));

	}

	@Test
	public void containsName_ExistingName_ReturnsTrue() {

		User containsNameExistingNameUser = User.builder().name("containsName_ExistingName").build();

		assertFalse(userDao.containsName("containsName_ExistingName"));

		userDao.create(containsNameExistingNameUser);

		assertTrue(userDao.containsName("containsName_ExistingName"));

		User containsNameExistingNameLongerNameUser = User.builder().name("containsName_ExistingName_LongerName")
				.build();

		assertFalse(userDao.containsName("containsName_ExistingName_LongerName"));

		userDao.create(containsNameExistingNameLongerNameUser);

		assertTrue(userDao.containsName("containsName_ExistingName_LongerName"));

	}

	@Test
	public void containsName_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.containsName("");
		});

	}

	@Test
	public void containsName_NonExistentName_ReturnsFalse() {

		assertFalse(userDao.containsName("containsName_NonExistentName"));

	}

	@Test
	public void count_KnownNumberOfUsers_ReturnsCorrectNumber() {

		userDao.deleteAll();
		assertEquals(0, userDao.count());

		userDao.create(User.builder().build());
		assertEquals(1, userDao.count());

		userDao.create(User.builder().build());
		assertEquals(2, userDao.count());

		userDao.create(User.builder().build());
		assertEquals(3, userDao.count());

		userDao.create(User.builder().build());
		assertEquals(4, userDao.count());

		userDao.create(User.builder().build());
		assertEquals(5, userDao.count());

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
	public void deleteAll_ExistingUsers_DeletesAll() {

		User deleteAll_DeletesAll_user1 = User.builder().id("deleteAll_DeletesAll_user1").build();
		User deleteAll_DeletesAll_user2 = User.builder().id("deleteAll_DeletesAll_user2").build();
		User deleteAll_DeletesAll_user3 = User.builder().id("deleteAll_DeletesAll_user3").build();
		User deleteAll_DeletesAll_user4 = User.builder().id("deleteAll_DeletesAll_user4").build();

		assertEquals(0, userDao.count());

		userDao.create(deleteAll_DeletesAll_user1);

		assertEquals(1, userDao.count());

		userDao.deleteAll();

		assertEquals(0, userDao.count());

		userDao.create(deleteAll_DeletesAll_user1);
		userDao.create(deleteAll_DeletesAll_user2);
		userDao.create(deleteAll_DeletesAll_user3);
		userDao.create(deleteAll_DeletesAll_user4);

		assertEquals(4, userDao.count());

		userDao.deleteAll();

		assertEquals(0, userDao.count());

	}

	@Test
	public void deleteAll_NoUsers_DoesNothing() {

		assertEquals(0, userDao.count());

		userDao.deleteAll();

		assertEquals(0, userDao.count());

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
	public void deleteByName_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.deleteByName("");
		});

	}

	@Test
	public void deleteByName_NonExistentName_ThrowsException() {

		assertThrows(EmptyResultDataAccessException.class, () -> {
			userDao.getByName("deleteByName_NonExistentName");
		});

	}

	@Test
	public void deleteByName_ValidName_DeletesUser() {

		String nameToDelete = "delete_valid_name";

		User delete_ValidName_User = User.builder().name(nameToDelete).build();

		userDao.create(delete_ValidName_User);

		userDao.deleteByName(nameToDelete);

		assertThrows(EmptyResultDataAccessException.class, () -> {
			userDao.getByName(nameToDelete);
		});

		assertFalse(userDao.containsName(nameToDelete));

	}

	@Test
	public void getAll_ReturnsUsers() {

		userDao.deleteAll();

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

		List<User> users = userDao.getAll();

		assertEquals(4, users.size());

		assertTrue(users.contains(getAll_ReturnsUsers_user1));
		assertTrue(users.contains(getAll_ReturnsUsers_user2));
		assertTrue(users.contains(getAll_ReturnsUsers_user3));
		assertTrue(users.contains(getAll_ReturnsUsers_user4));

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
	public void getById_ValidId_CanFindUser() {

		String idToFind = "getUserByIdvalidId";

		User getUserById_validId_User = User.builder().id(idToFind).build();

		userDao.create(getUserById_validId_User);

		User returnedUser = userDao.getById(idToFind);

		assertEquals(returnedUser, getUserById_validId_User);

	}

	@Test
	public void getByName_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.getByName("");
		});

	}

	@Test
	public void getByName_NonExistentName_ThrowsException() {

		assertThrows(EmptyResultDataAccessException.class, () -> {
			userDao.getByName("getUserByName_NonExistentName");
		});

	}

	@Test
	public void getByName_ValidName_CanFindUser() {

		String nameToFind = "getUserByNamevalidName";

		User getUserByName_validName_User = User.builder().name(nameToFind).build();

		userDao.create(getUserByName_validName_User);

		User returnedUser = userDao.getByName(nameToFind);

		assertEquals(returnedUser, getUserByName_validName_User);

		assertEquals(returnedUser.getName(), getUserByName_validName_User.getName());

	}

	@Test
	public void renameById_DuplicateName_ThrowsException() {

		String idToRename = "renameById_DupName_renameId";
		String idOfDuplicate = "renameById_DupName_duplicateId";

		String oldName = "renameById_DuplicateName_oldName";
		String duplicateName = "renameById_DuplicateName_duplicateName";

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
	public void renameByName_DuplicateName_ThrowsException() {

		String oldName = "changeNameById_DuplicateName_oldName";
		String duplicateName = "changeNameById_DuplicateName_duplicateName";

		User renameUser = User.builder().name(oldName).build();
		User duplicateUser = User.builder().name(duplicateName).build();

		userDao.create(renameUser);
		userDao.create(duplicateUser);

		int countBefore = userDao.count();

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.renameByName(oldName, duplicateName);
		});

		int countAfter = userDao.count();

		assertEquals(countBefore, countAfter);

	}

	@Test
	public void renameByName_InvalidOldName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.renameByName("", "renameById_InvalidId");
		});

	}

	@Test
	public void renameByName_InvalidNewName_ThrowsIllegalArgumentException() {

		User renameUser = User.builder().name("renameByName_InvalidNewName").build();

		userDao.create(renameUser);

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.renameByName("renameByName_InvalidNewName", "");
		});

	}

	@Test
	public void renameByName_NonExistentName_ThrowsException() {

		assertThrows(JdbcUpdateAffectedIncorrectNumberOfRowsException.class, () -> {
			userDao.renameByName("renameByName_NonExistentName_name", "renameByName_NonExistentName_username");
		});

	}

	@Test
	public void renameByName_ValidNames_ChangesName() {

		String oldName = "changeNameById_ValidIdAndName_oldName";
		String newName = "changeNameById_ValidIdAndName_newName";

		User insertedUser = User.builder().name(oldName).build();

		userDao.create(insertedUser);

		userDao.renameByName(oldName, newName);

		User returnedUser = userDao.getByName(newName);

		assertEquals(returnedUser, insertedUser);

		assertEquals(returnedUser.getName(), newName);

	}

}