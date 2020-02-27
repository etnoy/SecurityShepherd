package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateClassNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.persistence.model.ClassEntity;
import org.owasp.securityshepherd.repository.ClassRepository;
import org.owasp.securityshepherd.service.ClassService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ClassServiceTest {

	private ClassService classService;

	@Mock
	private ClassRepository classRepository;

	@BeforeEach
	private void setUp() {
		// Print more verbose errors if something goes wrong
		Hooks.onOperatorDebug();

		classService = new ClassService(classRepository);
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
	public void getById_ValidClassId_CallsRepository() throws Exception {

		final ClassEntity mockClass = mock(ClassEntity.class);

		final String mockName = "TestClass";
		final int mockId = 123;

		when(classRepository.existsById(mockId)).thenReturn(Mono.just(true));

		when(mockClass.getName()).thenReturn(mockName);
		when(classRepository.findById(mockId)).thenReturn(Mono.just(mockClass));

		StepVerifier.create(classService.getById(mockId)).assertNext(classEntity -> {

			assertThat(classEntity.getName(), is(mockName));

			verify(classRepository, times(1)).findById(mockId);

		}).expectComplete().verify();

	}

	@Test
	public void getById_InvalidClassId_ThrowsException() throws Exception {

		StepVerifier.create(classService.getById(-1)).expectError(InvalidClassIdException.class).verify();
		StepVerifier.create(classService.getById(0)).expectError(InvalidClassIdException.class).verify();

	}

	@Test
	public void setName_InvalidClassId_ThrowsException() throws Exception {

		final String newName = "newName";

		assertThrows(InvalidClassIdException.class, () -> classService.setName(-1, newName));
		assertThrows(InvalidClassIdException.class, () -> classService.setName(-1000, newName));
		assertThrows(InvalidClassIdException.class, () -> classService.setName(0, newName));

	}

	@Test
	public void setName_ValidName_SetsName() throws Exception {

		final ClassEntity mockClass = mock(ClassEntity.class);
		final ClassEntity mockClassWithName = mock(ClassEntity.class);

		final String newName = "newTestClass";

		final int mockId = 123;

		when(classRepository.findById(mockId)).thenReturn(Mono.just(mockClass));
		when(classRepository.existsById(mockId)).thenReturn(Mono.just(true));

		when(classRepository.findByName(newName)).thenReturn(Mono.empty());
		when(mockClass.withName(newName)).thenReturn(mockClassWithName);
		when(mockClassWithName.getName()).thenReturn(newName);

		when(classRepository.save(mockClassWithName)).thenReturn(Mono.just(mockClassWithName));

		StepVerifier.create(classService.setName(mockId, newName)).assertNext(classEntity -> {

			assertThat(classEntity.getName(), is(newName));

			verify(classRepository, times(1)).findById(mockId);
			verify(classRepository, times(1)).findByName(newName);

		}).expectComplete().verify();

	}

	@Test
	public void setName_DuplicateName_ThrowsException() throws Exception {

		final ClassEntity mockClass = mock(ClassEntity.class);
		final String newName = "newTestClass";

		final int mockId = 123;

		when(classRepository.existsById(mockId)).thenReturn(Mono.just(true));

		when(classRepository.findById(mockId)).thenReturn(Mono.just(mockClass));
		when(classRepository.findByName(newName)).thenReturn(Mono.just(mockClass));

		StepVerifier.create(classService.setName(mockId, newName)).expectError(DuplicateClassNameException.class)
				.verify();

	}

	@Test
	public void setName_NullName_ThrowsException() throws Exception {

		assertThrows(IllegalArgumentException.class, () -> classService.setName(1, null));

	}

	@Test
	public void setName_NonExistentId_ThrowsException() throws Exception {

		final ClassEntity mockClass = mock(ClassEntity.class);
		final String newName = "newTestClass";

		final int mockId = 1234567;

		when(classRepository.existsById(mockId)).thenReturn(Mono.just(false));

		when(classRepository.findById(mockId)).thenReturn(Mono.just(mockClass));
		when(classRepository.findByName(newName)).thenReturn(Mono.just(mockClass));

		StepVerifier.create(classService.setName(mockId, newName)).expectError(ClassIdNotFoundException.class).verify();

	}

}