package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLIntegrityConstraintViolationException;

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