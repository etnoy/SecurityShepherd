package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateClassNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.model.ClassEntity;
import org.owasp.securityshepherd.repository.ClassRepository;
import org.owasp.securityshepherd.service.ClassService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("ClassService unit test")
public class ClassServiceTest {

  private ClassService classService;

  @Mock
  private ClassRepository classRepository;

  @Test
  @DisplayName("Return the correct number of class entities in the repository")
  public void count_FiniteNumberOfClasses_ReturnsCount() throws Exception {
    final long mockedClassCount = 156L;

    when(classRepository.count()).thenReturn(Mono.just(mockedClassCount));

    StepVerifier.create(classService.count()).expectNext(mockedClassCount).expectComplete()
        .verify();
    verify(classRepository).count();
  }

  @Test
  @DisplayName("Throw DuplicateClassNameException when creating class entity with name that already exists")
  public void create_DuplicateName_ThrowsException() {
    final String mockClassName = "TestClass";
    final ClassEntity mockClass = mock(ClassEntity.class);

    when(classRepository.findByName(mockClassName)).thenReturn(Mono.just(mockClass));

    StepVerifier.create(classService.create(mockClassName))
        .expectError(DuplicateClassNameException.class).verify();
  }

  @Test
  @DisplayName("Throw IllegalArgumentException when creating class entity with empty name")
  public void create_EmptyArgument_ThrowsException() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> classService.create(""));
  }

  @Test
  @DisplayName("Throw NullPointerException when creating class entity with null name")
  public void create_NullArgument_ThrowsException() throws Exception {
    assertThrows(NullPointerException.class, () -> classService.create(null));
  }

  @Test
  @DisplayName("Return a valid class entity when creating")
  public void create_ValidData_CreatesClass() throws Exception {
    final String mockClassName = "TestClass";
    final int mockClassId = 838;

    when(classRepository.findByName(mockClassName)).thenReturn(Mono.empty());

    when(classRepository.save(any(ClassEntity.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, ClassEntity.class).withId(mockClassId)));

    StepVerifier.create(classService.create(mockClassName)).assertNext(createdClass -> {
      assertThat(createdClass, is(instanceOf(ClassEntity.class)));
      assertThat(createdClass.getName(), is(mockClassName));
    }).expectComplete().verify();

    verify(classRepository).findByName(mockClassName);
    verify(classRepository).save(any(ClassEntity.class));
  }

  @Test
  public void deleteAll_NoArgument_CallsRepository() {
    when(classRepository.deleteAll()).thenReturn(Mono.empty());
    StepVerifier.create(classService.deleteAll()).expectComplete().verify();
    verify(classRepository, times(1)).deleteAll();
  }

  @Test
  @DisplayName("Return true when checking if an existing class id exists")
  public void existsById_ExistingClassId_ReturnsTrue() throws Exception {
    final int mockClassId = 440;

    when(classRepository.existsById(mockClassId)).thenReturn(Mono.just(true));

    StepVerifier.create(classService.existsById(mockClassId)).expectNext(true).expectComplete()
        .verify();
    verify(classRepository).existsById(mockClassId);
  }

  @Test
  @DisplayName("Return false when checking if a nonexistent class id exists")
  public void existsById_NonExistentClassId_ReturnsFalse() throws Exception {
    final int mockClassId = 920;

    when(classRepository.existsById(mockClassId)).thenReturn(Mono.just(false));

    StepVerifier.create(classService.existsById(mockClassId)).expectNext(false).expectComplete()
        .verify();

    verify(classRepository).existsById(mockClassId);
  }

  @Test
  @DisplayName("Throw InvalidClassIdException when trying to retrieve a class entity that does not exist")
  public void getById_InvalidClassId_ThrowsException() throws Exception {
    StepVerifier.create(classService.getById(-1)).expectError(InvalidClassIdException.class)
        .verify();
    StepVerifier.create(classService.getById(0)).expectError(InvalidClassIdException.class)
        .verify();
  }

  @Test
  @DisplayName("Return the correct class entity when retrieving an class id")
  public void getById_ValidClassId_CallsRepository() throws Exception {
    final ClassEntity mockClass = mock(ClassEntity.class);

    final String mockName = "TestClass";
    final int mockId = 123;

    when(classRepository.existsById(mockId)).thenReturn(Mono.just(true));

    when(mockClass.getName()).thenReturn(mockName);
    when(classRepository.findById(mockId)).thenReturn(Mono.just(mockClass));

    StepVerifier.create(classService.getById(mockId)).assertNext(classEntity -> {
      assertThat(classEntity.getName(), is(mockName));
    }).expectComplete().verify();

    verify(classRepository).findById(mockId);
  }

  @Test
  @DisplayName("Throw exception when setting a class entity name to a name that already exists")
  public void setName_DuplicateName_ThrowsException() throws Exception {
    final ClassEntity mockClass = mock(ClassEntity.class);
    final String newName = "newTestClass";

    final int mockId = 123;

    when(classRepository.existsById(mockId)).thenReturn(Mono.just(true));

    when(classRepository.findById(mockId)).thenReturn(Mono.just(mockClass));
    when(classRepository.findByName(newName)).thenReturn(Mono.just(mockClass));

    StepVerifier.create(classService.setName(mockId, newName))
        .expectError(DuplicateClassNameException.class).verify();
  }

  @Test
  @DisplayName("Throw exception when setting name of an invalid class id")
  public void setName_InvalidClassId_ThrowsException() throws Exception {
    final String newName = "newName";

    assertThrows(InvalidClassIdException.class, () -> classService.setName(-1, newName));
    assertThrows(InvalidClassIdException.class, () -> classService.setName(-1000, newName));
    assertThrows(InvalidClassIdException.class, () -> classService.setName(0, newName));
  }

  @Test
  @DisplayName("Throw exception when setting name of a nonexistent class id")
  public void setName_NonExistentId_ThrowsException() throws Exception {
    final ClassEntity mockClass = mock(ClassEntity.class);
    final String newName = "newTestClass";

    final int mockId = 1234567;

    when(classRepository.existsById(mockId)).thenReturn(Mono.just(false));

    when(classRepository.findById(mockId)).thenReturn(Mono.just(mockClass));
    when(classRepository.findByName(newName)).thenReturn(Mono.just(mockClass));

    StepVerifier.create(classService.setName(mockId, newName))
        .expectError(ClassIdNotFoundException.class).verify();
  }

  @Test
  @DisplayName("Throw exception when setting the name of a class entity to null")
  public void setName_NullName_ThrowsException() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> classService.setName(1, null));
  }

  @Test
  @DisplayName("Can set name of class entity")
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
    }).expectComplete().verify();
    verify(classRepository).findById(mockId);
    verify(classRepository).findByName(newName);
  }

  @BeforeEach
  private void setUp() {
    // Print more verbose errors if something goes wrong
    Hooks.onOperatorDebug();

    classService = new ClassService(classRepository);
  }

}
