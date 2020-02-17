package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.exception.InvalidEntityIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.service.ClassService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class UserServiceTest {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ClassService classService;

	@Test
	public void create_EmptyArgument_ThrowsException() {

		assertThrows(IllegalArgumentException.class, () -> userService.create(""));

	}

	@Test
	public void create_NullArgument_ThrowsException() {

		assertThrows(NullPointerException.class, () -> userService.create(null));

	}

	@Test
	public void createPasswordUser_DuplicateDisplayName_ThrowsException() {

		String displayName = "createPasswordUser_DuplicateUser";
		String loginName1 = "_createPasswordUser_DuplicateUser1_";
		String loginName2 = "_createPasswordUser_DuplicateUser2_";

		// String "createPasswordUser_ValidData" bcrypted
		String hashedPassword = "$2y$04$2zPOzxj77Ul5amFcsnsyjenMBGpRgEApYsJXyK76dcX2wK7asi7.6";

		userService.createPasswordUser(displayName, loginName1, hashedPassword);

		assertThrows(DbActionExecutionException.class,
				() -> userService.createPasswordUser(displayName, loginName2, hashedPassword));

	}

	@Test
	public void createPasswordUser_DuplicateLoginName_ThrowsException() {

		String displayName1 = "createPasswordUser_DuplicateLoginName1";
		String displayName2 = "createPasswordUser_DuplicateLoginName2";

		String loginName = "_createPasswordUser_DuplicateLoginName_";

		// String "createPasswordUser_ValidData" bcrypted
		String hashedPassword = "$2y$04$2zPOzxj77Ul5amFcsnsyjenMBGpRgEApYsJXyK76dcX2wK7asi7.6";

		userService.createPasswordUser(displayName1, loginName, hashedPassword);

		assertThrows(DbActionExecutionException.class,
				() -> userService.createPasswordUser(displayName2, loginName, hashedPassword));

	}

	@Test
	public void createPasswordUser_EmptyArgument_ThrowsException() {

		String displayName = "createPasswordUser_NullArgument";
		String loginName = "_createPasswordUser_NullArgument_";
		String password = "aprettyweakpassword_with_null";

		assertThrows(IllegalArgumentException.class, () -> userService.createPasswordUser("", loginName, password));
		assertThrows(IllegalArgumentException.class, () -> userService.createPasswordUser(displayName, "", password));
		assertThrows(IllegalArgumentException.class, () -> userService.createPasswordUser(displayName, loginName, ""));

	}

	@Test
	public void createPasswordUser_NullArgument_ThrowsException() {

		String displayName = "createPasswordUser_NullArgument";
		String loginName = "_createPasswordUser_NullArgument_";
		String password = "aprettyweakpassword_with_null";

		assertThrows(NullPointerException.class, () -> userService.createPasswordUser(null, loginName, password));
		assertThrows(NullPointerException.class, () -> userService.createPasswordUser(displayName, null, password));
		assertThrows(NullPointerException.class, () -> userService.createPasswordUser(displayName, loginName, null));

	}

	@Test
	public void createPasswordUser_ValidData_Succeeds() throws InvalidUserIdException {

		String displayName = "createPasswordUser_ValidData";
		String loginName = "_createPasswordUser_ValidData_";

		// String "createPasswordUser_ValidData" bcrypted
		String hashedPassword = "$2y$04$2zPOzxj77Ul5amFcsnsyjenMBGpRgEApYsJXyK76dcX2wK7asi7.6";

		int userId = userService.createPasswordUser(displayName, loginName, hashedPassword).getId();

		assertThat(userService.get(userId).get().getDisplayName(), is(displayName));
		assertThat(userService.get(userId).get().getAuth().getPassword().getLoginName(), is(loginName));
		assertThat(userService.get(userId).get().getAuth().getPassword().getHashedPassword(), is(hashedPassword));

	}

	@Test
	public void get_ExistingUserId_ReturnsUser() throws InvalidUserIdException {

		final User testUser1 = userService.create("TestUser1");
		final User testUser2 = userService.create("TestUser2");
		final User testUser3 = userService.create("TestUser3");

		assertThat(userService.get(testUser1.getId()).get(), is(testUser1));
		assertThat(userService.get(testUser2.getId()).get(), is(testUser2));
		assertThat(userService.get(testUser3.getId()).get(), is(testUser3));

	}

	@Test
	public void get_InvalidUserId_ThrowsException() {

		assertThrows(InvalidUserIdException.class, () -> userService.get(-1));
		assertThrows(InvalidUserIdException.class, () -> userService.get(-1000));
		assertThrows(InvalidUserIdException.class, () -> userService.get(0));

	}

	@Test
	public void get_NonExistentUserId_NotPresent() throws InvalidUserIdException {

		assertThat(userService.count(), is(0L));
		assertThat(userService.get(1).isPresent(), is(false));
		assertThat(userService.get(1000).isPresent(), is(false));

	}

	@Test
	public void getKey_KeyExists_UsesExistingKey() throws UserIdNotFoundException, InvalidUserIdException {

		String userName = "getKey_KeyExists_UsesExistingKey";

		int userId = userService.create(userName).getId();

		assertThat(userService.get(userId).get().getKey(), is(nullValue()));

		byte[] userKey = userService.getKey(userId);

		assertThat(userKey.length, is(16));

		assertThat(userService.getKey(userId), is(userKey));

		userService.setDisplayName(userId, userName + "_new");

		assertThat(userService.getKey(userId), is(userKey));

	}

	@Test
	public void getKey_InvalideUserId_ThrowsException() {

		assertThrows(InvalidUserIdException.class, () -> userService.getKey(-1));
		assertThrows(InvalidUserIdException.class, () -> userService.getKey(-1000));
		assertThrows(InvalidUserIdException.class, () -> userService.getKey(0));

	}

	@Test
	public void getKey_NoKeyExists_GeneratesKey() throws UserIdNotFoundException, InvalidEntityIdException {

		String userName = "getKey_NoKeyExists_GeneratesKey";

		int userId = userService.create(userName).getId();

		assertThat(userService.get(userId).get().getKey(), is(nullValue()));

		byte[] userKey = userService.getKey(userId);

		assertThat(userKey.length, is(16));

	}

	@Test
	public void setClass_ValidClass_Succeeds() throws UserIdNotFoundException, InvalidUserIdException, ClassIdNotFoundException, InvalidClassIdException {

		int userId = userService.create("TestUser").getId();

		int classId = classService.create("TestClass").getId();
		
		userService.setClassId(userId, classId);

		User returnedUser = userService.get(userId).get();
		assertThat(returnedUser.getClassId(), is(classId));

	}

	@Test
	public void setDisplayName_ValidName_Succeeds() throws UserIdNotFoundException, InvalidUserIdException {

		String userName = "setDisplayName_ValidName";
		String newUserName = "new_rename_ValidName";
		int userId = userService.create(userName).getId();

		assertThat(userService.count(), is(1L));

		User returnedUser = userService.get(userId).get();
		assertThat(returnedUser.getId(), is(userId));
		assertThat(returnedUser.getDisplayName(), is(userName));
		assertThat(userService.count(), is(1L));

		userService.setDisplayName(userId, newUserName);

		returnedUser = userService.get(userId).get();
		assertThat(returnedUser.getId(), is(userId));
		assertThat(returnedUser.getDisplayName(), is(newUserName));
		assertThat(userService.count(), is(1L));

	}

}