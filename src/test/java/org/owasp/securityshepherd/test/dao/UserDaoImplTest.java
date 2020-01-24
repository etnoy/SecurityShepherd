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
	public void whenAddingUser_checkReturnEquals() {

		UserEntity testUser1 = new UserEntity("userid123", "classid456", "addValidUser1", "hashedpass", "player", "", 0,
				new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);
		UserEntity testUser2 = new UserEntity("userid456", "classid456", "addValidUser2", "hashedpass2", "player", "", 0,
				new Timestamp(99), "", "login", false, false, 0, 3, 0, 0, 0);
		UserEntity testUser3 = new UserEntity("anotheruser789", "anotherclass", "addValidUser3", "ahashedpass3",
				"player", "", 0, new Timestamp(99), "", "login", false, false, 0, 3, 0, 0, 0);

		userDao.create(testUser1);
		userDao.create(testUser2);
		userDao.create(testUser3);

		List<UserEntity> allUsers = userDao.listUsers();

		assertTrue(allUsers.contains(testUser1), "List of users should contain added users");
		assertTrue(allUsers.contains(testUser2), "List of users should contain added users");
		assertTrue(allUsers.contains(testUser3), "List of users should contain added users");

	}

	@Test
	public void addUser_IdsMustBeUnique() {

		UserEntity testUser = new UserEntity("userid123", "classid456", "duplicateUserId", "hashedpass", "player", "", 0,
				new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);

		userDao.create(testUser);

		assertThrows(DuplicateKeyException.class, () -> {
			userDao.create(testUser);
		});

	}
	
	@Test
	public void addUser_UserNamesMustBeUnique() {

		UserEntity duplicateUserName1 = new UserEntity("userid123", "classid456", "duplicateUsername", "hashedpass", "player", "", 0,
				new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);
		UserEntity duplicateUserName2 = new UserEntity("userid456", "classid456", "duplicateUsername", "hashedpass", "player", "", 0,
				new Timestamp(0), "", "login", false, false, 0, 0, 0, 0, 0);

		userDao.create(duplicateUserName1);
		
		assertThrows(DuplicateKeyException.class, () -> {
			userDao.create(duplicateUserName2);
		});

	}

}