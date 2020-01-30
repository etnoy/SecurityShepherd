package org.owasp.securityshepherd.test.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.repository.NameIdRepository;
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
public class UserRepositoryTest {

	@Autowired
	private NameIdRepository<User> userDao;

	@Test
	public void existsById_ExistingId_ReturnsTrue() {

		User existsByIdExistingIdUser = User.builder().id("existsById_ExistingId").build();

		assertFalse(userDao.existsById("existsById_ExistingId"));

		userDao.save(existsByIdExistingIdUser);

		assertTrue(userDao.existsById("existsById_ExistingId"));

		User existsByIdExistingIdLongerIdUser = User.builder().id("existsById_ExistingId_LongerId").build();

		assertFalse(userDao.existsById("existsById_ExistingId_LongerId"));

		userDao.save(existsByIdExistingIdLongerIdUser);

		assertTrue(userDao.existsById("existsById_ExistingId_LongerId"));

	}

	@Test
	public void existsById_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.existsById("");
		});

	}

	@Test
	public void existsById_NonExistentId_ReturnsFalse() {

		assertFalse(userDao.existsById("existsById_NonExistentId"));

	}

	@Test
	public void existsByName_ExistingName_ReturnsTrue() {

		User existsByNameExistingNameUser = User.builder().name("existsByName_ExistingName").build();

		assertFalse(userDao.existsByName("existsByName_ExistingName"));

		userDao.save(existsByNameExistingNameUser);

		assertTrue(userDao.existsByName("existsByName_ExistingName"));

		User existsByNameExistingNameLongerNameUser = User.builder().name("existsByName_ExistingName_LongerName")
				.build();

		assertFalse(userDao.existsByName("existsByName_ExistingName_LongerName"));

		userDao.save(existsByNameExistingNameLongerNameUser);

		assertTrue(userDao.existsByName("existsByName_ExistingName_LongerName"));

	}

	@Test
	public void existsByName_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.existsByName("");
		});

	}

	@Test
	public void existsByName_NonExistentName_ReturnsFalse() {

		assertFalse(userDao.existsByName("existsByName_NonExistentName"));

	}

	@Test
	public void count_KnownNumberOfUsers_ReturnsCorrectNumber() {

		userDao.deleteAll();
		assertEquals(0, userDao.count());

		userDao.save(User.builder().build());
		assertEquals(1, userDao.count());

		userDao.save(User.builder().build());
		assertEquals(2, userDao.count());

		userDao.save(User.builder().build());
		assertEquals(3, userDao.count());

		userDao.save(User.builder().build());
		assertEquals(4, userDao.count());

		userDao.save(User.builder().build());
		assertEquals(5, userDao.count());

	}

	@Test
	public void create_DuplicateUserId_ThrowsException() {

		User duplicateUserId1 = User.builder().id("duplicateUserId").name("duplicateUserId1").build();
		User duplicateUserId2 = User.builder().id("duplicateUserId").name("duplicateUserId2").build();

		userDao.save(duplicateUserId1);

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.save(duplicateUserId2);
		});

	}

	@Test
	public void create_DuplicateUserName_ThrowsException() {

		User duplicateUserName1 = User.builder().name("duplicateUserName").build();
		User duplicateUserName2 = User.builder().name("duplicateUserName").build();

		userDao.save(duplicateUserName1);

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.save(duplicateUserName2);
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

		userDao.save(validUser1);
		userDao.save(validUser2);
		userDao.save(validUser3);

		List<User> allUsers = (List<User>) userDao.findAll();

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

		userDao.save(deleteAll_DeletesAll_user1);

		assertEquals(1, userDao.count());

		userDao.deleteAll();

		assertEquals(0, userDao.count());

		userDao.save(deleteAll_DeletesAll_user1);
		userDao.save(deleteAll_DeletesAll_user2);
		userDao.save(deleteAll_DeletesAll_user3);
		userDao.save(deleteAll_DeletesAll_user4);

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
			userDao.findById("deleteById_NonExistentId");
		});

	}

	@Test
	public void deleteById_ValidId_DeletesUser() {

		String idToDelete = "delete_valid_id";

		User delete_ValidId_User = User.builder().id(idToDelete).build();

		userDao.save(delete_ValidId_User);

		userDao.deleteById(idToDelete);

		assertThrows(EmptyResultDataAccessException.class, () -> {
			userDao.findById(idToDelete);
		});

		assertFalse(userDao.existsById(idToDelete));

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
			userDao.findByName("deleteByName_NonExistentName");
		});

	}

	@Test
	public void deleteByName_ValidName_DeletesUser() {

		String nameToDelete = "delete_valid_name";

		User delete_ValidName_User = User.builder().name(nameToDelete).build();

		userDao.save(delete_ValidName_User);

		userDao.deleteByName(nameToDelete);

		assertThrows(EmptyResultDataAccessException.class, () -> {
			userDao.findByName(nameToDelete);
		});

		assertFalse(userDao.existsByName(nameToDelete));

	}

	@Test
	public void findAll_ReturnsUsers() {

		userDao.deleteAll();

		assertTrue(userDao.count() == 0);

		User findAll_ReturnsUsers_user1 = User.builder().id("findAll_ReturnsUsers_user1").build();
		User findAll_ReturnsUsers_user2 = User.builder().id("findAll_ReturnsUsers_user2").build();
		User findAll_ReturnsUsers_user3 = User.builder().id("findAll_ReturnsUsers_user3").build();
		User findAll_ReturnsUsers_user4 = User.builder().id("findAll_ReturnsUsers_user4").build();

		userDao.save(findAll_ReturnsUsers_user1);
		userDao.save(findAll_ReturnsUsers_user2);
		userDao.save(findAll_ReturnsUsers_user3);
		userDao.save(findAll_ReturnsUsers_user4);

		assertTrue(userDao.existsById(findAll_ReturnsUsers_user1.getId()));
		assertTrue(userDao.existsById(findAll_ReturnsUsers_user2.getId()));
		assertTrue(userDao.existsById(findAll_ReturnsUsers_user3.getId()));
		assertTrue(userDao.existsById(findAll_ReturnsUsers_user4.getId()));

		List<User> users = (List<User>) userDao.findAll();

		assertEquals(4, users.size());

		assertTrue(users.contains(findAll_ReturnsUsers_user1));
		assertTrue(users.contains(findAll_ReturnsUsers_user2));
		assertTrue(users.contains(findAll_ReturnsUsers_user3));
		assertTrue(users.contains(findAll_ReturnsUsers_user4));

	}

	@Test
	public void findById_InvalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.findById("");
		});

	}

	@Test
	public void findById_NonExistentId_ThrowsException() {

		assertThrows(EmptyResultDataAccessException.class, () -> {
			userDao.findById("getUserById_NonExistentId");
		});

	}

	@Test
	public void findById_ValidId_CanFindUser() {

		String idToFind = "getUserByIdvalidId";

		User getUserById_validId_User = User.builder().id(idToFind).build();

		userDao.save(getUserById_validId_User);

		Optional<User> returnedUser = userDao.findById(idToFind);

		assertTrue(returnedUser.isPresent());
		
		assertEquals(returnedUser.get(), getUserById_validId_User);

	}

	@Test
	public void findByName_InvalidName_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.findByName("");
		});

	}

	@Test
	public void findByName_NonExistentName_ThrowsException() {

		assertThrows(EmptyResultDataAccessException.class, () -> {
			userDao.findByName("getUserByName_NonExistentName");
		});

	}

	@Test
	public void findByName_ValidName_CanFindUser() {

		String nameToFind = "getUserByNamevalidName";

		User getUserByName_validName_User = User.builder().name(nameToFind).build();

		userDao.save(getUserByName_validName_User);

		Optional<User> returnedUser = userDao.findByName(nameToFind);
		
		assertTrue(returnedUser.isPresent());

		assertEquals(returnedUser.get(), getUserByName_validName_User);

		assertEquals(returnedUser.get().getName(), getUserByName_validName_User.getName());

	}

//	@Test
//	public void renameById_DuplicateName_ThrowsException() {
//
//		String idToRename = "renameById_DupName_renameId";
//		String idOfDuplicate = "renameById_DupName_duplicateId";
//
//		String oldName = "renameById_DuplicateName_oldName";
//		String duplicateName = "renameById_DuplicateName_duplicateName";
//
//		User renameUser = User.builder().id(idToRename).name(oldName).build();
//		User duplicateUser = User.builder().id(idOfDuplicate).name(duplicateName).build();
//
//		userDao.save(renameUser);
//		userDao.save(duplicateUser);
//
//		long countBefore = userDao.count();
//
//		assertThrows(DuplicateKeyException.class, () -> {
//			userDao.renameById(idToRename, duplicateName);
//		});
//
//		long countAfter = userDao.count();
//
//		assertEquals(countBefore, countAfter);
//
//	}
//
//	@Test
//	public void renameById_InvalidId_ThrowsIllegalArgumentException() {
//
//		assertThrows(IllegalArgumentException.class, () -> {
//			userDao.renameById("", "renameById_InvalidId");
//		});
//
//	}
//
//	@Test
//	public void renameById_InvalidName_ThrowsIllegalArgumentException() {
//
//		assertThrows(IllegalArgumentException.class, () -> {
//			userDao.renameById("renameById_InvalidName", "");
//		});
//
//	}
//
//	@Test
//	public void renameById_NonExistentId_ThrowsException() {
//
//		assertThrows(JdbcUpdateAffectedIncorrectNumberOfRowsException.class, () -> {
//			userDao.renameById("renameById_NonExistentId_id", "renameById_NonExistentId_username");
//		});
//
//	}
//
//	@Test
//	public void renameById_ValidIdAndName_ChangesName() {
//
//		String idToRename = "changeNameById_ValidIdAndName";
//
//		String oldName = "changeNameById_ValidIdAndName_oldName";
//		String newName = "changeNameById_ValidIdAndName_newName";
//
//		User insertedUser = User.builder().id(idToRename).name(oldName).build();
//
//		userDao.save(insertedUser);
//
//		userDao.renameById(idToRename, newName);
//
//		User returnedUser = userDao.findById(idToRename);
//
//		assertEquals(returnedUser, insertedUser);
//
//		assertEquals(returnedUser.getName(), newName);
//
//	}
//
//	@Test
//	public void renameByName_DuplicateName_ThrowsException() {
//
//		String oldName = "changeNameById_DuplicateName_oldName";
//		String duplicateName = "changeNameById_DuplicateName_duplicateName";
//
//		User renameUser = User.builder().name(oldName).build();
//		User duplicateUser = User.builder().name(duplicateName).build();
//
//		userDao.save(renameUser);
//		userDao.save(duplicateUser);
//
//		long countBefore = userDao.count();
//
//		assertThrows(DuplicateKeyException.class, () -> {
//			userDao.renameByName(oldName, duplicateName);
//		});
//
//		long countAfter = userDao.count();
//
//		assertEquals(countBefore, countAfter);
//
//	}
//
//	@Test
//	public void renameByName_InvalidOldName_ThrowsIllegalArgumentException() {
//
//		assertThrows(IllegalArgumentException.class, () -> {
//			userDao.renameByName("", "renameById_InvalidId");
//		});
//
//	}
//
//	@Test
//	public void renameByName_InvalidNewName_ThrowsIllegalArgumentException() {
//
//		User renameUser = User.builder().name("renameByName_InvalidNewName").build();
//
//		userDao.save(renameUser);
//
//		assertThrows(IllegalArgumentException.class, () -> {
//			userDao.renameByName("renameByName_InvalidNewName", "");
//		});
//
//	}
//
//	@Test
//	public void renameByName_NonExistentName_ThrowsException() {
//
//		assertThrows(JdbcUpdateAffectedIncorrectNumberOfRowsException.class, () -> {
//			userDao.renameByName("renameByName_NonExistentName_name", "renameByName_NonExistentName_username");
//		});
//
//	}
//
//	@Test
//	public void renameByName_ValidNames_ChangesName() {
//
//		String oldName = "changeNameById_ValidIdAndName_oldName";
//		String newName = "changeNameById_ValidIdAndName_newName";
//
//		User insertedUser = User.builder().name(oldName).build();
//
//		userDao.save(insertedUser);
//
//		userDao.renameByName(oldName, newName);
//
//		User returnedUser = userDao.findByName(newName);
//
//		assertEquals(returnedUser, insertedUser);
//
//		assertEquals(returnedUser.getName(), newName);
//
//	}

}