/**
 * This file is part of Security Shepherd.
 *
 * <p>Security Shepherd is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with Security
 * Shepherd. If not, see <http://www.gnu.org/licenses/>.
 */
package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateClassNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.model.ClassEntity;
import org.owasp.securityshepherd.service.ClassService;
import org.owasp.securityshepherd.user.ClassRepository;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClassService unit test")
public class ClassServiceTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private ClassService classService;

  @Mock private ClassRepository classRepository;

  @Test
  @DisplayName("count() call the count function of ClassRepository and return its same value")
  public void count_FiniteNumberOfClasses_ReturnsCount() {
    final long mockedClassCount = 156L;

    when(classRepository.count()).thenReturn(Mono.just(mockedClassCount));

    StepVerifier.create(classService.count())
        .expectNext(mockedClassCount)
        .expectComplete()
        .verify();
    verify(classRepository, times(1)).count();
  }

  @Test
  @DisplayName(
      "create() should return DuplicateClassNameException when the given name is already taken")
  public void create_DuplicateName_ReturnsDuplicateClassNameException() {
    final String mockClassName = "TestClass";
    final ClassEntity mockClass = mock(ClassEntity.class);

    when(classRepository.findByName(mockClassName)).thenReturn(Mono.just(mockClass));

    StepVerifier.create(classService.create(mockClassName))
        .expectError(DuplicateClassNameException.class)
        .verify();
  }

  @Test
  @DisplayName("create() should return IllegalArgumentException when called with an empty name")
  public void create_EmptyArgument_ReturnsIllegalArgumentException() {
    StepVerifier.create(classService.create(""))
        .expectError(IllegalArgumentException.class)
        .verify();
  }

  @Test
  @DisplayName("create() should return NullPointerException when called with null name")
  public void create_NullArgument_ReturnsNullPointerException() {
    StepVerifier.create(classService.create(null)).expectError(NullPointerException.class).verify();
  }

  @Test
  @DisplayName("create() should return a class")
  public void create_ValidData_CreatesClass() {
    final String mockClassName = "TestClass";
    final int mockClassId = 838;

    when(classRepository.findByName(mockClassName)).thenReturn(Mono.empty());

    when(classRepository.save(any(ClassEntity.class)))
        .thenAnswer(user -> Mono.just(user.getArgument(0, ClassEntity.class).withId(mockClassId)));

    StepVerifier.create(classService.create(mockClassName))
        .assertNext(
            createdClass -> {
              assertThat(createdClass, is(instanceOf(ClassEntity.class)));
              assertThat(createdClass.getName(), is(mockClassName));
            })
        .expectComplete()
        .verify();

    verify(classRepository, times(1)).findByName(mockClassName);
    verify(classRepository, times(1)).save(any(ClassEntity.class));
  }

  @Test
  @DisplayName("Checking if an existing class id exists should return true")
  public void existsById_ExistingClassId_ReturnsTrue() {
    final long mockClassId = 440;

    when(classRepository.existsById(mockClassId)).thenReturn(Mono.just(true));

    StepVerifier.create(classService.existsById(mockClassId))
        .expectNext(true)
        .expectComplete()
        .verify();
    verify(classRepository, times(1)).existsById(mockClassId);
  }

  @Test
  @DisplayName("Checking if a non-existent class exists should return false")
  public void existsById_NonExistentClassId_ReturnsFalse() {
    final long mockClassId = 920;

    when(classRepository.existsById(mockClassId)).thenReturn(Mono.just(false));

    StepVerifier.create(classService.existsById(mockClassId))
        .expectNext(false)
        .expectComplete()
        .verify();

    verify(classRepository, times(1)).existsById(mockClassId);
  }

  @Test
  @DisplayName("Getting an invalid class id should return InvalidClassIdException")
  public void getById_InvalidClassId_ReturnsInvalidClassIdException() {
    StepVerifier.create(classService.getById(-1))
        .expectError(InvalidClassIdException.class)
        .verify();
    StepVerifier.create(classService.getById(0))
        .expectError(InvalidClassIdException.class)
        .verify();
  }

  @Test
  @DisplayName("Getting a class id should return the correct class")
  public void getById_ValidClassId_CallsRepository() {
    final ClassEntity mockClass = mock(ClassEntity.class);

    final String mockName = "TestClass";
    final long mockId = 123;

    when(classRepository.existsById(mockId)).thenReturn(Mono.just(true));

    when(mockClass.getName()).thenReturn(mockName);
    when(classRepository.findById(mockId)).thenReturn(Mono.just(mockClass));

    StepVerifier.create(classService.getById(mockId))
        .assertNext(
            classEntity -> {
              assertThat(classEntity.getName(), is(mockName));
            })
        .expectComplete()
        .verify();

    verify(classRepository, times(1)).findById(mockId);
  }

  @Test
  @DisplayName(
      "Setting a class name to a name that is taken should return DuplicateClassNameException")
  public void setName_DuplicateName_ReturnsDuplicateClassNameException() {
    final ClassEntity mockClass = mock(ClassEntity.class);
    final String newName = "newTestClass";

    final long mockClassId = 123;

    when(classRepository.existsById(mockClassId)).thenReturn(Mono.just(true));

    when(classRepository.findById(mockClassId)).thenReturn(Mono.just(mockClass));
    when(classRepository.findByName(newName)).thenReturn(Mono.just(mockClass));

    StepVerifier.create(classService.setName(mockClassId, newName))
        .expectError(DuplicateClassNameException.class)
        .verify();
  }

  @Test
  @DisplayName("Setting a class name to an invalid name should return InvalidClassIdException")
  public void setName_InvalidClassId_ReturnsInvalidClassIdException() {
    final String newName = "newName";
    final long[] idsToTest = {0, -1, -1000, -99999};

    for (final long id : idsToTest) {
      StepVerifier.create(classService.setName(id, newName))
          .expectError(InvalidClassIdException.class)
          .verify();
    }
  }

  @Test
  @DisplayName(
      "Setting the name of a class that does not exist should return ClassIdNotFoundException")
  public void setName_NonExistentId_ReturnsClassIdNotFoundException() {
    final String newName = "newTestClass";

    final long mockId = 1234567;

    when(classRepository.existsById(mockId)).thenReturn(Mono.just(false));

    StepVerifier.create(classService.setName(mockId, newName))
        .expectError(ClassIdNotFoundException.class)
        .verify();
  }

  @Test
  @DisplayName("Setting the name of a class to null should return IllegalArgumentException")
  public void setName_NullName_ReturnsIllegalArgumentException() {
    StepVerifier.create(classService.setName(1, null))
        .expectError(IllegalArgumentException.class)
        .verify();
  }

  @Test
  @DisplayName("Setting the name of a class should change the class name")
  public void setName_ValidName_SetsName() {
    final ClassEntity mockClass = mock(ClassEntity.class);
    final ClassEntity mockClassWithName = mock(ClassEntity.class);

    final String newName = "newTestClass";

    final long mockId = 123;

    when(classRepository.findById(mockId)).thenReturn(Mono.just(mockClass));
    when(classRepository.existsById(mockId)).thenReturn(Mono.just(true));

    when(classRepository.findByName(newName)).thenReturn(Mono.empty());
    when(mockClass.withName(newName)).thenReturn(mockClassWithName);
    when(mockClassWithName.getName()).thenReturn(newName);

    when(classRepository.save(mockClassWithName)).thenReturn(Mono.just(mockClassWithName));

    StepVerifier.create(classService.setName(mockId, newName))
        .assertNext(
            classEntity -> {
              assertThat(classEntity.getName(), is(newName));
            })
        .expectComplete()
        .verify();
    verify(classRepository, times(1)).findById(mockId);
    verify(classRepository, times(1)).findByName(newName);
  }

  @BeforeEach
  private void setUp() {
    classService = new ClassService(classRepository);
  }
}
