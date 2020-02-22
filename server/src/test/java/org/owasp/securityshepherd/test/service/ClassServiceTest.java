package org.owasp.securityshepherd.test.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.persistence.model.ClassEntity;
import org.owasp.securityshepherd.proxy.ClassRepositoryProxy;
import org.owasp.securityshepherd.service.ClassService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class ClassServiceTest {

	private ClassService classService;

	@Mock
	private ClassRepositoryProxy classRepositoryProxy;
	
	@BeforeEach
	private void setUp() {
		classService = new ClassService(classRepositoryProxy);
	}
	
	@Test
	public void create_EmptyArgument_ThrowsException() throws Exception {

		assertThrows(IllegalArgumentException.class, () -> classService.create(""));

	}

	@Test
	public void create_NullArgument_ThrowsException() throws Exception {

		assertThrows(NullPointerException.class, () -> classService.create(null));

	}

	@Test
	public void get_ValidClassId_CallsRepository() throws Exception {

		ClassEntity testClass = mock(ClassEntity.class);
		when(classRepositoryProxy.findById(123)).thenReturn(Optional.of(testClass));

		classService.get(123);

		verify(classRepositoryProxy, times(1)).findById(123);

	}

	@Test
	public void get_InvalidClassId_ThrowsException() throws Exception {

		assertThrows(InvalidClassIdException.class, () -> classService.get(-1));
		assertThrows(InvalidClassIdException.class, () -> classService.get(-1000));
		assertThrows(InvalidClassIdException.class, () -> classService.get(0));

	}

	@Test
	public void setName_ValidName_CallsRepository() throws Exception {

		ClassEntity testClass = mock(ClassEntity.class);
		when(classRepositoryProxy.findById(12)).thenReturn(Optional.of(testClass));
		when(testClass.withName("newClassName")).thenReturn(testClass);
		when(classRepositoryProxy.save(any(ClassEntity.class))).thenReturn(testClass);

		classService.setName(12, "newClassName");

		InOrder order = inOrder(testClass, classRepositoryProxy);

		order.verify(testClass, times(1)).withName("newClassName");
		order.verify(classRepositoryProxy, times(1)).save(testClass);

	}

	@Test
	public void setName_NullName_ThrowsException() throws Exception {

		assertThrows(IllegalArgumentException.class, () -> classService.setName(1, null));

	}

	@Test
	public void setName_NonExistentId_ThrowsException() throws Exception {

		assertThrows(ClassIdNotFoundException.class, () -> classService.setName(1234567890, "newName"));

	}

}