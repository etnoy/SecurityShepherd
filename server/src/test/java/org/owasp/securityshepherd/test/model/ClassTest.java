package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.model.ClassEntity;
import org.owasp.securityshepherd.model.ClassEntity.ClassBuilder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

//@ExtendWith(SpringExtension.class)
//@SpringBootTest
@DisplayName("Class unit test")
public class ClassTest {
  @Test
  public void build_NullName_ThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> ClassEntity.builder().name(null));
  }

  @Test
  public void build_NameNotGiven_ThrowsNullPointerException() {
    final ClassBuilder classBuilder = ClassEntity.builder().id(4);
    assertThrows(NullPointerException.class, () -> classBuilder.build());
  }

  @Test
  public void build_ValidId_Builds() {
    final ClassEntity classEntity = ClassEntity.builder().id(3).name("TestClass").build();

    assertThat(classEntity, is(instanceOf(ClassEntity.class)));
    assertThat(classEntity.getId(), is(3));
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
    assertThat(ClassEntity.builder().id(3).name("TestClass").toString(),
        is("ClassEntity.ClassBuilder(id=3, name=TestClass)"));
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(ClassEntity.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void toString_ValidData_AsExpected() {
    assertThat(ClassEntity.builder().name("TestClass").build().toString(),
        is("ClassEntity(id=0, name=TestClass)"));
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final int originalId = 1;
    final int[] testedIds = {originalId, 0, -1, 1000, -1000, 123456789};

    final ClassEntity classEntity = ClassEntity.builder().id(originalId).name("Test Class").build();

    assertThat(classEntity.getId(), is(originalId));

    for (int id : testedIds) {
      assertThat(classEntity.withId(id).getId(), is(id));
    }
  }

  @Test
  public void withName_NullName_ThrowsException() {
    final String name = "withName_NullName";

    final ClassEntity testClass = ClassEntity.builder().name(name).build();

    assertThat(testClass.getName(), is(name));

    assertThrows(NullPointerException.class, () -> testClass.withName(null));
  }

  @Test
  public void withName_ValidName_ChangesName() {
    final String name = "Test Class";

    final ClassEntity testClass = ClassEntity.builder().name(name).build();

    assertThat(testClass.getName(), is(name));

    final String[] testedNames = {name, "", "newClass", "Long  With     Whitespace", "12345"};

    for (final String newName : testedNames) {
      assertThat(testClass.withName(newName).getName(), is(newName));
    }
  }
}
