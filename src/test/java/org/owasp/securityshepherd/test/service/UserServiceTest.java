package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@Transactional
@SpringBootTest
public class UserServiceTest {

	@Autowired
	private UserService userService;

	@Test
	public void createPasswordUser_ValidData_Succeeds() {

		String displayName = "createPasswordUser_ValidData";
		String loginName = "_createPasswordUser_ValidData_";

		// String "createPasswordUser_ValidData" bcrypted
		String hashedPassword = "$2y$04$2zPOzxj77Ul5amFcsnsyjenMBGpRgEApYsJXyK76dcX2wK7asi7.6";

		Long userId = userService.createPasswordUser(displayName, loginName, hashedPassword).getId();

		assertThat(userService.get(userId).getDisplayName(), is(equalTo(displayName)));
		assertThat(userService.get(userId).getAuth().getPassword().getLoginName(), is(equalTo(loginName)));
		assertThat(userService.get(userId).getAuth().getPassword().getHashedPassword(), is(equalTo(hashedPassword)));

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
	public void createPasswordUser_EmptyArgument_ThrowsException() {

		String displayName = "createPasswordUser_NullArgument";
		String loginName = "_createPasswordUser_NullArgument_";
		String password = "aprettyweakpassword_with_null";

		assertThrows(IllegalArgumentException.class, () -> userService.createPasswordUser("", loginName, password));
		assertThrows(IllegalArgumentException.class, () -> userService.createPasswordUser(displayName, "", password));
		assertThrows(IllegalArgumentException.class, () -> userService.createPasswordUser(displayName, loginName, ""));

	}

	@Test
	public void setDisplayName_ValidName_Succeeds() {

		String userName = "setDisplayName_ValidName";
		String newUserName = "new_rename_ValidName";
		Long userId = userService.create(userName).getId();

		assertThat(userService.count(), is(1L));

		User returnedUser = userService.get(userId);
		assertThat(returnedUser.getId(), is(userId));
		assertThat(returnedUser.getDisplayName(), equalTo(userName));
		assertThat(userService.count(), is(1L));

		userService.setDisplayName(userId, newUserName);

		returnedUser = userService.get(userId);
		assertThat(returnedUser.getId(), is(userId));
		assertThat(returnedUser.getDisplayName(), equalTo(newUserName));
		assertThat(userService.count(), is(1L));

	}

	@Test
	public void setClass_ValidClass_Succeeds() {

		String userName = "setClass_ValidClass_user";

		Long userId = userService.create(userName).getId();

		userService.setClassId(userId, 1L);

		User returnedUser = userService.get(userId);
		assertThat(returnedUser.getClassId(), is(1L));

	}

}