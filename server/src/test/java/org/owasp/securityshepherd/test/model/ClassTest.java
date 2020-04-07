package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.model.ClassEntity;
import org.owasp.securityshepherd.model.ClassEntity.ClassBuilder;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("ClassEntity unit test")
public class ClassTest {
  @Test
  public void build_NullName_ThrowsNullPointerException() {
    final ClassBuilder classBuilder = ClassEntity.builder();
    assertThrows(NullPointerException.class, () -> classBuilder.name(null));
  }

  @Test
  public void build_NameNotGiven_ThrowsNullPointerException() {
    final ClassBuilder classBuilder = ClassEntity.builder().id(4);
    assertThrows(NullPointerException.class, () -> classBuilder.build());
  }

  @Test
  public void build_ValidData_Builds() {
    final ClassEntity testClass = ClassEntity.builder().id(3).name("TestClass").build();
    assertThat(testClass, is(instanceOf(ClassEntity.class)));
    assertThat(testClass.getId(), is(3));
  }

  @Test
  public void build_ValidName_Builds() {
    final String name = "className";
    final ClassEntity classEntity = ClassEntity.builder().name(name).build();
    assertThat(classEntity, is(instanceOf(ClassEntity.class)));
    assertThat(classEntity.getName(), is(name));
  }

  @Test
  public void classBuildertoString_ValidData_NotNull() {
    assertThat(ClassEntity.builder().name("TestClass").toString(),
        is("ClassEntity.ClassBuilder(id=null, name=TestClass)"));
    assertThat(ClassEntity.builder().id(379).name("AnotherTestClass").toString(),
        is("ClassEntity.ClassBuilder(id=379, name=AnotherTestClass)"));
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(ClassEntity.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void toString_ValidData_AsExpected() {
    assertThat(ClassEntity.builder().name("TestClass").build().toString(),
        is("ClassEntity(id=null, name=TestClass)"));
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final Integer originalId = 1;
    final Integer[] testedIds = {originalId, null, 0, -1, 1000, -1000, 123456789};
    final ClassEntity classEntity = ClassEntity.builder().id(originalId).name("TestClass").build();

    assertThat(classEntity.getId(), is(originalId));

    for (Integer id : testedIds) {
      final ClassEntity changedClass = classEntity.withId(id);
      assertThat(classEntity, is(instanceOf(ClassEntity.class)));
      assertThat(changedClass.getId(), is(id));
    }
  }

  @Test
  public void withName_NullName_ThrowsException() {
    final String name = "withName_NullName";
    final ClassEntity testClass = ClassEntity.builder().name(name).build();
    assertThrows(NullPointerException.class, () -> testClass.withName(null));
  }

  @Test
  public void withName_ValidName_ChangesName() {
    final String originalName = "TestClass";

    final ClassEntity classEntity = ClassEntity.builder().name(originalName).build();

    final String[] testedNames = {originalName, "", "newClass", "Long  With     Whitespace", "12345"};

    for (final String newName : testedNames) {
      final ClassEntity changedClass = classEntity.withName(newName);
      assertThat(changedClass, is(instanceOf(ClassEntity.class)));
      assertThat(changedClass.getName(), is(newName));
    }
  }
}
