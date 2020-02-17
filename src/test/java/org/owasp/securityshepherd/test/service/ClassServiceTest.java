package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.model.ClassEntity;
import org.owasp.securityshepherd.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
public class ClassServiceTest {

	@Autowired
	private ClassService classService;

	@Test
	public void create_EmptyArgument_ThrowsException() {

		assertThrows(IllegalArgumentException.class, () -> classService.create(""));

	}

	@Test
	public void create_NullArgument_ThrowsException() {

		assertThrows(NullPointerException.class, () -> classService.create(null));

	}

	@Test
	public void get_ExistingClassId_ReturnsClass() {

		final ClassEntity testClass1 = classService.create("TestClass1");
		final ClassEntity testClass2 = classService.create("TestClass2");
		final ClassEntity testClass3 = classService.create("TestClass3");

		assertThat(classService.get(testClass1.getId()).get(), is(testClass1));
		assertThat(classService.get(testClass2.getId()).get(), is(testClass2));
		assertThat(classService.get(testClass3.getId()).get(), is(testClass3));

	}

	@Test
	public void get_NegativeClassId_ThrowsException() {

		assertThrows(IllegalArgumentException.class, () -> classService.get(-1));
		assertThrows(IllegalArgumentException.class, () -> classService.get(-1000));

	}

	@Test
	public void get_NonExistentClassId_NotPresent() {

		assertThat(classService.count(), is(0L));
		assertThat(classService.get(1).isPresent(), is(false));
		assertThat(classService.get(1000).isPresent(), is(false));

	}

	@Test
	public void get_ZeroClassId_ThrowsException() {

		assertThrows(IllegalArgumentException.class, () -> classService.get(0));
	}



	@Test
	public void setName_ValidName_Succeeds() throws ClassIdNotFoundException {

		String className = "setDisplayName_ValidName";
		String newClassName = "new_rename_ValidName";
		int classId = classService.create(className).getId();

		assertThat(classService.count(), is(1L));

		ClassEntity returnedClass = classService.get(classId).get();
		assertThat(returnedClass.getId(), is(classId));
		assertThat(returnedClass.getName(), is(className));
		assertThat(classService.count(), is(1L));

		classService.setName(classId, newClassName);

		returnedClass = classService.get(classId).get();
		assertThat(returnedClass.getId(), is(classId));
		assertThat(returnedClass.getName(), is(newClassName));
		assertThat(classService.count(), is(1L));

	}

}