package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.emptyString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.exception.EntityIdException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleIdNotFoundException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.service.ModuleService;
import org.owasp.securityshepherd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class ModuleServiceTest {

	@Autowired
	private ModuleService moduleService;

	@Autowired
	private UserService userService;

	@Test
	public void createModule_ValidData_Succeeds() {

		final String name = "createPasswordModule_ValidData";

		final int moduleId = moduleService.create(name).getId();

		assertThat(moduleService.get(moduleId).get().getName(), is((name)));

	}

	@Test
	public void createModule_DuplicateName_ThrowsException() {

		final String name = "createPasswordModule_DuplicateModule";

		moduleService.create(name);

		assertThrows(DbActionExecutionException.class, () -> moduleService.create(name));

	}

	@Test
	public void setExactFlag_ValidFlag_SetsFlagToExact() throws EntityIdException {

		final String name = "setExactFlag_ValidFlag";
		final String exactFlag = "setExactFlag_ValidFlag_flag";

		Module returnedModule;

		returnedModule = moduleService.create(name);
		final int moduleId = returnedModule.getId();

		assertThat(returnedModule.isFlagEnabled(), is(false));
		assertThat(returnedModule.isExactFlag(), is(false));

		moduleService.setExactFlag(moduleId, exactFlag);
		returnedModule = moduleService.get(moduleId).get();

		assertThat(returnedModule.isFlagEnabled(), is(true));
		assertThat(returnedModule.isExactFlag(), is(true));

		assertThat(returnedModule.getFlag(), is(exactFlag));

	}

	@Test
	public void setExactFlag_ZeroModuleId_ThrowsException() {


	}

	@Test
	public void setExactFlag_InvalidModuleId_ThrowsException() {

		assertThrows(InvalidModuleIdException.class, () -> moduleService.setExactFlag(0, "flag"));
		assertThrows(InvalidModuleIdException.class, () -> moduleService.setExactFlag(-1, "flag"));
		assertThrows(InvalidModuleIdException.class, () -> moduleService.setExactFlag(-9999, "flag"));

	}

	@Test
	public void setExactFlag_EmptyExactFlag_ThrowsException() {

		final int moduleId = userService.create("TestUser").getId();

		// TODO: better exception
		assertThrows(IllegalArgumentException.class, () -> moduleService.setExactFlag(moduleId, ""));

	}

	@Test
	public void setExactFlag_NullExactFlag_ThrowsException() {

		final int moduleId = userService.create("TestUser").getId();

		// TODO: better exception
		assertThrows(NullPointerException.class, () -> moduleService.setExactFlag(moduleId, null));

	}

	@Test
	public void setDynamicFlag_NoPreviousFlag_GeneratesNewFlag() {

		Module returnedModule = moduleService.create("TestModule");
		final int moduleId = returnedModule.getId();

		assertThat(returnedModule.isFlagEnabled(), is(false));
		assertThat(returnedModule.isExactFlag(), is(false));
		assertThat(returnedModule.getFlag(), is(nullValue()));

		moduleService.setDynamicFlag(moduleId);
		returnedModule = moduleService.get(moduleId).get();

		assertThat(returnedModule.isFlagEnabled(), is(true));
		assertThat(returnedModule.isExactFlag(), is(false));

		assertThat(returnedModule.getFlag(), is(notNullValue()));
		assertThat(returnedModule.getFlag(), instanceOf(String.class));
		assertThat(returnedModule.getFlag(), not(is(emptyString())));

	}

	@Test
	public void setDynamicFlag_FlagPreviouslySet_KeepsFlag() {

		Module returnedModule = moduleService.create("TestModule");
		final int moduleId = returnedModule.getId();

		moduleService.setDynamicFlag(moduleId);
		returnedModule = moduleService.get(moduleId).get();

		final String dynamicFlag = returnedModule.getFlag();

		moduleService.setDynamicFlag(moduleId);

		assertThat(returnedModule.getFlag(), is(dynamicFlag));

	}

	@Test
	public void setDynamicFlag_ZeroModuleId_ThrowsException() {

		// TODO: better exception
		assertThrows(IllegalArgumentException.class, () -> moduleService.setDynamicFlag(0));

	}

	@Test
	public void setDynamicFlag_NegativeModuleId_ThrowsException() {

		// TODO: better exception
		assertThrows(IllegalArgumentException.class, () -> moduleService.setDynamicFlag(-1));
		assertThrows(IllegalArgumentException.class, () -> moduleService.setDynamicFlag(-9999));

	}

	@Test
	public void verifyFlag_ValidExactFlag_ReturnsTrue() throws EntityIdException {

		final String name = "verifyFlag_ValidExactFlag";
		final String moduleName = name + "_module";
		final String userName = name + "_user";
		final String exactFlag = name + "_flag";

		final int moduleId = moduleService.create(moduleName).getId();
		final int userId = userService.create(userName).getId();

		moduleService.setExactFlag(moduleId, exactFlag);
		assertThat(moduleService.verifyFlag(userId, moduleId, exactFlag), is(true));

	}

	@Test
	public void verifyFlag_ValidExactUpperLowerCaseFlag_ReturnsTrue() throws EntityIdException {

		final String name = "verifyFlag_ValidExactUpperLowerCaseFlag";
		final String moduleName = name + "_module";
		final String userName = name + "_user";
		final String exactFlag = name + "_flag";

		final int moduleId = moduleService.create(moduleName).getId();
		final int userId = userService.create(userName).getId();

		moduleService.setExactFlag(moduleId, exactFlag);

		assertThat(moduleService.verifyFlag(userId, moduleId, exactFlag.toLowerCase()), is(true));
		assertThat(moduleService.verifyFlag(userId, moduleId, exactFlag.toUpperCase()), is(true));

	}

	@Test
	public void verifyFlag_InvalidExactFlag_ReturnsFalse() throws EntityIdException {

		final String name = "verifyFlag_InvalidExactFlag";
		final String moduleName = name + "_module";
		final String userName = name + "_user";
		final String exactFlag = name + "_flag";

		final int moduleId = moduleService.create(moduleName).getId();
		final int userId = userService.create(userName).getId();

		moduleService.setExactFlag(moduleId, exactFlag);

		assertThat(moduleService.verifyFlag(userId, moduleId, exactFlag + "1"), is(false));
		assertThat(moduleService.verifyFlag(userId, moduleId, "1"), is(false));
		assertThat(moduleService.verifyFlag(userId, moduleId, ""), is(false));

	}

	@Test
	public void verifyFlag_NullFlag_ReturnsFalse() throws EntityIdException {

		final int moduleId = moduleService.create("TestModule").getId();
		final int userId = userService.create("TestUser").getId();

		moduleService.setExactFlag(moduleId, "flag");

		assertThat(moduleService.verifyFlag(userId, moduleId, null), is(false));

	}

	@Test
	public void getDynamicFlag_FlagNotSet_ThrowsException() {

		final Module testModule = moduleService.create("TestModule");
		final int moduleId = testModule.getId();
		final int userId = userService.create("TestUser").getId();

		assertThat(testModule.isFlagEnabled(), is(false));

		assertThrows(IllegalArgumentException.class, () -> moduleService.getDynamicFlag(userId, moduleId));

	}

	@Test
	public void getDynamicFlag_FlagSet_ReturnsFlag() throws ModuleIdNotFoundException, UserIdNotFoundException, InvalidUserIdException {

		final Module testModule = moduleService.create("TestModule");
		final int moduleId = testModule.getId();
		final int userId = userService.create("TestUser").getId();

		moduleService.setDynamicFlag(moduleId);

		final String returnedFlag = moduleService.getDynamicFlag(userId, moduleId);

		assertThat(returnedFlag, is(notNullValue()));
		assertThat(returnedFlag, is(not(emptyString())));

	}

	@Test
	public void verifyFlag_ValidDynamicFlag_ReturnsTrue() throws ModuleIdNotFoundException, UserIdNotFoundException, InvalidUserIdException {

		final int moduleId = moduleService.create("TestModule").getId();
		final int userId = userService.create("TestUser").getId();

		moduleService.setDynamicFlag(moduleId);
		final String flag = moduleService.getDynamicFlag(userId, moduleId);

		assertThat(moduleService.verifyFlag(userId, moduleId, flag), is(true));

	}

	@Test
	public void verifyFlag_InvalidDynamicFlag_ReturnsFalse() throws ModuleIdNotFoundException, UserIdNotFoundException, InvalidUserIdException {

		final int moduleId = moduleService.create("TestModule").getId();
		final int userId = userService.create("TestUser").getId();

		moduleService.setDynamicFlag(moduleId);
		final String flag = moduleService.getDynamicFlag(userId, moduleId);

		assertThat(moduleService.verifyFlag(userId, moduleId, flag + "a"), is(false));
		assertThat(moduleService.verifyFlag(userId, moduleId, "a"), is(false));
		assertThat(moduleService.verifyFlag(userId, moduleId, ""), is(false));
		assertThat(moduleService.verifyFlag(userId, moduleId, "123456789"), is(false));
		assertThat(moduleService.verifyFlag(userId, moduleId, null), is(false));

	}

	@Test
	public void verifyFlag_FlagNotSet_ThrowsException() {

		final Module testedModule = moduleService.create("TestModule");
		final int moduleId = testedModule.getId();
		final int userId = userService.create("TestUser").getId();

		assertThat(testedModule.isFlagEnabled(), is(false));

		// TODO: better exception
		assertThrows(IllegalArgumentException.class, () -> moduleService.verifyFlag(userId, moduleId, "flag"));

	}

	@Test
	public void create_NullArgument_ThrowsException() {

		assertThrows(NullPointerException.class, () -> moduleService.create(null));

	}

	@Test
	public void create_EmptyArgument_ThrowsException() {

		assertThrows(IllegalArgumentException.class, () -> moduleService.create(""));

	}

	@Test
	public void setName_ValidName_Succeeds() {

		final String name = "setName_ValidName";
		final String newName = "new_rename_ValidName";

		assertThat(moduleService.count(), is(0L));

		final int moduleId = moduleService.create(name).getId();

		assertThat(moduleService.count(), is(1L));

		Module returnedModule = moduleService.get(moduleId).get();
		assertThat(returnedModule.getId(), is(moduleId));
		assertThat(returnedModule.getName(), is(name));
		assertThat(moduleService.count(), is(1L));

		moduleService.setName(moduleId, newName);

		returnedModule = moduleService.get(moduleId).get();
		assertThat(returnedModule.getId(), is(moduleId));
		assertThat(returnedModule.getName(), is(newName));
		assertThat(moduleService.count(), is(1L));

	}

	@Test
	public void get_NonExistentModuleId_NotPresent() {

		assertThat(moduleService.count(), is(0L));
		assertThat(moduleService.get(1).isPresent(), is(false));
		assertThat(moduleService.get(1000).isPresent(), is(false));

	}

	@Test
	public void get_ZeroUserId_ThrowsException() {

		assertThrows(IllegalArgumentException.class, () -> moduleService.get(0));
	}

	@Test
	public void get_NegativeUserId_ThrowsException() {

		assertThrows(IllegalArgumentException.class, () -> moduleService.get(-1));
		assertThrows(IllegalArgumentException.class, () -> moduleService.get(-1000));

	}

}