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
package org.owasp.securityshepherd.test.model;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.authentication.PasswordAuth;
import org.owasp.securityshepherd.authentication.PasswordAuth.PasswordAuthBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("PasswordAuth unit test")
class PasswordAuthTest {
  @Test
  void build_HashedPasswordNotGiven_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().userId(1L).loginName("TestUser");
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> passwordAuthBuilder.build());
    assertThat(thrownException.getMessage()).isEqualTo("hashedPassword is marked non-null but is null");
  }

  @Test
  void build_LoginNameNotGiven_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().userId(1L).hashedPassword("hashedPass");
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> passwordAuthBuilder.build());
    assertThat(thrownException.getMessage()).isEqualTo("loginName is marked non-null but is null");
  }

  @Test
  void build_UserIdNotGiven_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().loginName("TestUser").hashedPassword("hashedPass");
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> passwordAuthBuilder.build());
    assertThat(thrownException.getMessage()).isEqualTo("userId is marked non-null but is null");
  }

  @Test
  void builderToString_ValidData_AsExpected() {
    assertThat(PasswordAuth.builder().loginName("TestUser").hashedPassword("987").toString()).isEqualTo(
            "PasswordAuth.PasswordAuthBuilder(id=null, userId=null, loginName=TestUser, hashedPassword=987, isPasswordNonExpired=false)");
    assertThat(PasswordAuth.builder()
            .id(3L)
            .userId(4L)
            .loginName("TestUser2")
            .hashedPassword("123")
            .isPasswordNonExpired(true)
            .toString()).isEqualTo(
            "PasswordAuth.PasswordAuthBuilder(id=3, userId=4, loginName=TestUser2, hashedPassword=123, isPasswordNonExpired=true)");
  }

  @Test
  void buildHashedPassword_NullHashedPassword_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> passwordAuthBuilder.hashedPassword(null));
    assertThat(thrownException.getMessage()).isEqualTo("hashedPassword is marked non-null but is null");
  }

  @Test
  void buildHashedPassword_ValidHashedPassword_BuildsPasswordAuth() {
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().userId(555L).loginName("TestUser");
    for (final String hashedPassword : TestUtils.STRINGS) {
      final PasswordAuth passwordAuth = passwordAuthBuilder.hashedPassword(hashedPassword).build();
      assertThat(passwordAuth.getHashedPassword()).isEqualTo(hashedPassword);
    }
  }

  @Test
  void buildId_ValidId_BuildsPasswordAuth() {
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().userId(1L).loginName("TestUser").hashedPassword("passwordHash");
    for (final long id : TestUtils.LONGS) {
      final PasswordAuth passwordAuth = passwordAuthBuilder.id(id).build();
      assertThat(passwordAuth.getId()).isEqualTo(id);
    }
  }

  @Test
  void buildIsPasswordExpired_TrueOrFalse_BuildsPasswordAuth() {
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().userId(45L).loginName("TestUser").hashedPassword("passwordHash");
    for (final boolean isPasswordNonExpired : TestUtils.BOOLEANS) {
      final PasswordAuth passwordAuth =
          passwordAuthBuilder.isPasswordNonExpired(isPasswordNonExpired).build();
      assertThat(passwordAuth.isPasswordNonExpired()).isEqualTo(isPasswordNonExpired);
    }
  }

  @Test
  void buildLoginName_NullLoginName_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> passwordAuthBuilder.loginName(null));
    assertThat(thrownException.getMessage()).isEqualTo("loginName is marked non-null but is null");
  }

  @Test
  void buildLoginName_ValidLoginName_BuildsPasswordAuth() {
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().userId(681L).hashedPassword("passwordHash");
    for (final String loginName : TestUtils.STRINGS) {
      final PasswordAuth changedPasswordAuth = passwordAuthBuilder.loginName(loginName).build();
      assertThat(changedPasswordAuth.getLoginName()).isEqualTo(loginName);
    }
  }

  @Test
  void buildUserId_NullUserId_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> passwordAuthBuilder.userId(null));
    assertThat(thrownException.getMessage()).isEqualTo("userId is marked non-null but is null");
  }

  @Test
  void buildUserId_ValidUserId_BuildsPasswordAuth() {
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().loginName("TestUser").hashedPassword("passwordHash");
    for (final long userId : TestUtils.LONGS) {
      final PasswordAuth passwordAuth = passwordAuthBuilder.userId(userId).build();
      assertThat(passwordAuth.getUserId()).isEqualTo(userId);
    }
  }

  @Test
  void equals_EqualsVerifier_AsExpected() {
    EqualsVerifier.forClass(PasswordAuth.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  void toString_ValidData_AsExpected() {
    assertThat(PasswordAuth.builder()
            .loginName("TestUser")
            .hashedPassword("hashedPassword")
            .userId(1278L)
            .build()
            .toString()).isEqualTo(
            "PasswordAuth(id=null, userId=1278, loginName=TestUser, hashedPassword=hashedPassword, isPasswordNonExpired=false)");
    assertThat(PasswordAuth.builder()
            .id(5L)
            .userId(95L)
            .loginName("TestUser3")
            .hashedPassword("hashedPassword2")
            .build()
            .toString()).isEqualTo(
            "PasswordAuth(id=5, userId=95, loginName=TestUser3, hashedPassword=hashedPassword2, isPasswordNonExpired=false)");
    assertThat(PasswordAuth.builder()
            .id(14L)
            .userId(35L)
            .loginName("TestUser4")
            .hashedPassword("hashedPassword3")
            .isPasswordNonExpired(true)
            .build()
            .toString()).isEqualTo(
            "PasswordAuth(id=14, userId=35, loginName=TestUser4, hashedPassword=hashedPassword3, isPasswordNonExpired=true)");
  }

  @Test
  void withHashedPassword_NullHashedPassword_ThrowsNullPointerException() {
    final PasswordAuth passwordAuth =
        PasswordAuth.builder()
            .userId(1L)
            .loginName("Test")
            .hashedPassword("hashedPassword")
            .build();
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> passwordAuth.withHashedPassword(null));
    assertThat(thrownException.getMessage()).isEqualTo("hashedPassword is marked non-null but is null");
  }

  @Test
  void withHashedPassword_ValidHashedPassword_ChangesHashedPassword() {
    final PasswordAuth passwordAuth =
        PasswordAuth.builder()
            .userId(46L)
            .loginName("abc123hash")
            .hashedPassword(TestUtils.INITIAL_STRING)
            .build();
    for (final String hashedPassword : TestUtils.STRINGS) {
      final PasswordAuth withPasswordAuth = passwordAuth.withHashedPassword(hashedPassword);
      assertThat(withPasswordAuth.getHashedPassword()).isEqualTo(hashedPassword);
    }
  }

  @Test
  void withId_ValidId_ChangesId() {
    final PasswordAuth passwordAuth =
        PasswordAuth.builder()
            .userId(6L)
            .id(TestUtils.INITIAL_LONG)
            .loginName("TestUser")
            .hashedPassword("HashedPassword")
            .build();
    for (final Long id : TestUtils.LONGS_WITH_NULL) {
      final PasswordAuth withPasswordAuth = passwordAuth.withId(id);
      assertThat(withPasswordAuth.getId()).isEqualTo(id);
    }
  }

  @Test
  void withLoginName_NullLoginName_ThrowsNullPointerException() {
    final PasswordAuth passwordAuth =
        PasswordAuth.builder()
            .userId(1L)
            .loginName("Test")
            .hashedPassword("hashedPassword")
            .build();
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> passwordAuth.withLoginName(null));
    assertThat(thrownException.getMessage()).isEqualTo("loginName is marked non-null but is null");
  }

  @Test
  void withLoginName_ValidLoginName_ChangesLoginName() {
    final PasswordAuth passwordAuth =
        PasswordAuth.builder()
            .loginName(TestUtils.INITIAL_STRING)
            .userId(31L)
            .hashedPassword("passwordHash")
            .build();
    for (final String loginName : TestUtils.STRINGS) {
      final PasswordAuth changedPasswordAuth = passwordAuth.withLoginName(loginName);
      assertThat(changedPasswordAuth.getLoginName()).isEqualTo(loginName);
    }
  }

  @Test
  void withPasswordNonExpired_ValidBoolean_ChangesPasswordNonExpired() {
    final PasswordAuth passwordAuth =
        PasswordAuth.builder()
            .userId(45L)
            .loginName("TestUser")
            .hashedPassword("passwordHash")
            .isPasswordNonExpired(TestUtils.INITIAL_BOOLEAN)
            .build();
    for (final boolean isPasswordNonExpired : TestUtils.BOOLEANS) {
      final PasswordAuth withPasswordAuth =
          passwordAuth.withPasswordNonExpired(isPasswordNonExpired);
      assertThat(withPasswordAuth.isPasswordNonExpired()).isEqualTo(isPasswordNonExpired);
    }
  }

  @Test
  void withUserId_NullUserId_ThrowsNullPointerException() {
    final PasswordAuth passwordAuth =
        PasswordAuth.builder()
            .userId(1L)
            .loginName("TestUser")
            .hashedPassword("TestPassword")
            .build();
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> passwordAuth.withUserId(null));
    assertThat(thrownException.getMessage()).isEqualTo("userId is marked non-null but is null");
  }

  @Test
  void withUserId_ValidUserId_ChangesUserId() {
    final PasswordAuth passwordAuth =
        PasswordAuth.builder()
            .userId(TestUtils.INITIAL_LONG)
            .loginName("TestUser")
            .hashedPassword("TestPassword")
            .build();
    for (final Long userId : TestUtils.LONGS) {
      final PasswordAuth newPasswordAuth = passwordAuth.withUserId(userId);
      assertThat(newPasswordAuth.getUserId()).isEqualTo(userId);
    }
  }
}
