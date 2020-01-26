package org.owasp.securityshepherd.test.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.dao.mapper.UserDaoImpl;
import org.owasp.securityshepherd.model.User;
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
	public void addUser_ValidUser_ContainedInAllUsers() {

		User validUser1 = User.builder().id("validuser1").name("A simple username").build();

		User validUser2 = User.builder().id("validuser2").classId("aclassid").name("Anotherusername")
				.password("password").role("player").ssoId("anssoid").suspendedUntil(new Timestamp(0))
				.email("me@example.com").loginType("saml").temporaryPassword(false).temporaryPassword(false)
				.goldMedalCount(0).silverMedalCount(0).bronzeMedalCount(0).badSubmissionCount(0).build();

		User validUser3 = User.builder().id("validuser3").classId("newclass").name("nönlätiñchåracters")
				.password("hashedpassword").role("admin").ssoId("anotherssoid").suspendedUntil(new Timestamp(12345000))
				.email("").loginType("login").temporaryPassword(true).temporaryPassword(true).goldMedalCount(999)
				.silverMedalCount(999).bronzeMedalCount(9).badSubmissionCount(999).build();

		userDao.addUser(validUser1);
		userDao.addUser(validUser2);
		userDao.addUser(validUser3);

		List<User> allUsers = userDao.listUsers();

		assertTrue(allUsers.contains(validUser1), "List of users should contain added users");
		assertTrue(allUsers.contains(validUser2), "List of users should contain added users");
		assertTrue(allUsers.contains(validUser3), "List of users should contain added users");

	}

	@Test
	public void addUser_DuplicateUserId_ThrowsException() {

		User duplicateUserId1 = User.builder().id("duplicateUserId").name("duplicateUserId1").build();
		User duplicateUserId2 = User.builder().id("duplicateUserId").name("duplicateUserId2").build();

		userDao.addUser(duplicateUserId1);

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.addUser(duplicateUserId2);
		});

	}

	@Test
	public void addUser_DuplicateUserName_ThrowsException() {

		User duplicateUserName1 = User.builder().name("duplicateUserName").build();
		User duplicateUserName2 = User.builder().name("duplicateUserName").build();

		userDao.addUser(duplicateUserName1);

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.addUser(duplicateUserName2);
		});

	}

	@Test
	public void addUser_duplicateSsoId_ThrowsException() {

		User duplicateSsoId1 = User.builder().name("duplicateSsoId1").ssoId("duplicateSsoId").build();
		User duplicateSsoId2 = User.builder().name("duplicateSsoId2").ssoId("duplicateSsoId").build();

		userDao.addUser(duplicateSsoId1);

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.addUser(duplicateSsoId2);
		});

	}

	@Test
	public void getUserById_validId_CanFindUser() {

		String idToFind = "getUserByIdvalidId";

		User getUserById_validId_User = User.builder().id(idToFind).build();

		userDao.addUser(getUserById_validId_User);

		User returnedUser = userDao.getUserById(idToFind);
		
		System.out.println("Returned user: " + returnedUser);

		assertEquals(returnedUser, getUserById_validId_User);

	}

	@Test
	public void getUserById_invalidId_ThrowsIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			userDao.getUserById("");
		});

	}

	@Test
	public void getUserById_nonExistentId_ReturnsNull() {

		User returnedUser = userDao.getUserById("thisuserdoesnotexist");

		assertTrue(returnedUser == null);

	}

}