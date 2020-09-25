/*
 * This file is part of Security Shepherd.
 * 
 * Security Shepherd is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Security Shepherd.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.owasp.securityshepherd.test.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.model.ClassEntity;
import org.owasp.securityshepherd.model.ClassEntity.ClassBuilder;

@DisplayName("ClassEntity unit test")
class ClassTest {
  @Test
  void build_NullName_ThrowsNullPointerException() {
    final ClassBuilder classBuilder = ClassEntity.builder();
    assertThrows(NullPointerException.class, () -> classBuilder.name(null));
  }

  @Test
  void build_NameNotGiven_ThrowsNullPointerException() {
    final ClassBuilder classBuilder = ClassEntity.builder().id(4);
    assertThrows(NullPointerException.class, () -> classBuilder.build());
  }

  @Test
  void build_ValidData_Builds() {
    final ClassEntity testClass = ClassEntity.builder().id(3).name("TestClass").build();
    assertThat(testClass).isInstanceOf(ClassEntity.class);
    assertThat(testClass.getId()).isEqualTo(3);
  }

  @Test
  void build_ValidName_Builds() {
    final String name = "className";
    final ClassEntity classEntity = ClassEntity.builder().name(name).build();
    assertThat(classEntity).isInstanceOf(ClassEntity.class);
    assertThat(classEntity.getName()).isEqualTo(name);
  }

  @Test
  void classBuildertoString_ValidData_NotNull() {
    assertThat(ClassEntity.builder().name("TestClass"))
        .hasToString("ClassEntity.ClassBuilder(id=null, name=TestClass)");
    assertThat(ClassEntity.builder().id(379).name("AnotherTestClass"))
        .hasToString("ClassEntity.ClassBuilder(id=379, name=AnotherTestClass)");
  }

  @Test
  void equals_AutomaticTesting() {
    EqualsVerifier.forClass(ClassEntity.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  void toString_ValidData_AsExpected() {
    assertThat(ClassEntity.builder().name("TestClass").build())
        .hasToString("ClassEntity(id=null, name=TestClass)");
  }

  @Test
  void withId_ValidId_ChangesId() {
    final Integer originalId = 1;
    final Integer[] testedIds = {originalId, null, 0, -1, 1000, -1000, 123456789};
    final ClassEntity classEntity = ClassEntity.builder().id(originalId).name("TestClass").build();

    assertThat(classEntity.getId()).isEqualTo(originalId);

    for (Integer id : testedIds) {
      final ClassEntity changedClass = classEntity.withId(id);
      assertThat(classEntity).isInstanceOf(ClassEntity.class);
      assertThat(changedClass.getId()).isEqualTo(id);
    }
  }

  @Test
  void withName_NullName_ThrowsNullPointerException() {
    final String name = "withName_NullName";
    final ClassEntity testClass = ClassEntity.builder().name(name).build();
    assertThrows(NullPointerException.class, () -> testClass.withName(null));
  }

  @Test
  void withName_ValidName_ChangesName() {
    final String originalName = "TestClass";

    final ClassEntity classEntity = ClassEntity.builder().name(originalName).build();

    final String[] testedNames = {
      originalName, "", "newClass", "Long  With     Whitespace", "12345"
    };

    for (final String newName : testedNames) {
      final ClassEntity changedClass = classEntity.withName(newName);
      assertThat(changedClass).isInstanceOf(ClassEntity.class);
      assertThat(changedClass.getName()).isEqualTo(newName);
    }
  }
}
