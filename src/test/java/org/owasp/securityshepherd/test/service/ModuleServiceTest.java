package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.service.ModuleService;
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

	@Test
	public void createModule_ValidData_Succeeds() {

		String name = "createPasswordModule_ValidData";

		Long moduleId = moduleService.create(name).getId();

		assertThat(moduleService.get(moduleId).getName(), is(equalTo(name)));

	}

	@Test
	public void createModule_DuplicateName_ThrowsException() {

		String name = "createPasswordModule_DuplicateModule";

		moduleService.create(name);

		assertThrows(DbActionExecutionException.class, () -> moduleService.create(name));

	}

	@Test
	public void setExactFlag_ValidFlag_SetsFlagToExact() {

		String name = "setExactFlag_ValidFlag";
		String exactFlag = "setExactFlag_ValidFlag_flag";

		Module returnedModule;

		returnedModule = moduleService.create(name);
		long moduleId = returnedModule.getId();

		assertThat(returnedModule.isFlagEnabled(), is(false));
		assertThat(returnedModule.isExactFlag(), is(false));

		moduleService.setExactFlag(moduleId, exactFlag);
		returnedModule = moduleService.get(moduleId);

		assertThat(returnedModule.isFlagEnabled(), is(true));
		assertThat(returnedModule.isExactFlag(), is(true));

		assertThat(returnedModule.getFlag(), is(equalTo(exactFlag)));

	}
	
	@Test
	public void setDynamicFlag_ValidFlag_SetsFlagToExact() {

		//TODO

	}

	@Test
	public void verifyFlag_ExactFlag_ReturnsTrue() {

		String name = "verifyFlag_ExactFlag";
		String exactFlag = "verifyFlag_ExactFlag_flag";

		Module returnedModule;

		returnedModule = moduleService.create(name);
		long moduleId = returnedModule.getId();

		moduleService.setExactFlag(moduleId, exactFlag);
		assertThat(moduleService.verifyFlag(moduleId, exactFlag), is(true));

	}
	
	@Test
	public void verifyFlag_DynamicFlag_ReturnsTrue() {

		//TODO

	}

	@Test
	public void verifyFlag_FlagNotSet_ThrowsException() {

		String name = "verifyFlag_FlagNotSet";
		String flag = "verifyFlag_FlagNotSet_flag";

		Module returnedModule;

		returnedModule = moduleService.create(name);
		long moduleId = returnedModule.getId();

		assertThrows(IllegalArgumentException.class, () -> moduleService.verifyFlag(moduleId, flag));

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

		String name = "setName_ValidName";
		String newName = "new_rename_ValidName";

		assertThat(moduleService.count(), is(0L));

		Long moduleId = moduleService.create(name).getId();

		assertThat(moduleService.count(), is(1L));

		Module returnedModule = moduleService.get(moduleId);
		assertThat(returnedModule.getId(), is(moduleId));
		assertThat(returnedModule.getName(), equalTo(name));
		assertThat(moduleService.count(), is(1L));

		moduleService.setName(moduleId, newName);

		returnedModule = moduleService.get(moduleId);
		assertThat(returnedModule.getId(), is(moduleId));
		assertThat(returnedModule.getName(), equalTo(newName));
		assertThat(moduleService.count(), is(1L));

	}

}