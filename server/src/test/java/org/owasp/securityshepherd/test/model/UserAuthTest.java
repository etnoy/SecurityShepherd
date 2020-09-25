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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.authentication.UserAuth;
import org.owasp.securityshepherd.authentication.UserAuth.UserAuthBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;

@DisplayName("UserAuth unit test")
class UserAuthTest {

  @Test
  void build_UserIdNotGiven_ThrowsNullPointerException() {
    final UserAuthBuilder userAuthBuilder = UserAuth.builder();
    assertThrows(NullPointerException.class, () -> userAuthBuilder.build());
  }

  @Test
  void buildBadLoginCount_ValidBadLoginCount_Builds() {
    final int[] badLoginCountsToTest = {0, 1, 1000, -1, 999999, 42, 567890, -1234567};
    final UserAuthBuilder userAuthBuilder = UserAuth.builder().userId(100L);

    for (final int badLoginCount : badLoginCountsToTest) {
      userAuthBuilder.badLoginCount(badLoginCount);
      final UserAuth userAuth = userAuthBuilder.build();

      assertThat(userAuth).isInstanceOf(UserAuth.class);
      assertThat(userAuth.getBadLoginCount()).isEqualTo(badLoginCount);
    }
  }

  @Test
  void builderToString_ValidData_AsExpected() {
    final UserAuthBuilder builder = UserAuth.builder();

    assertThat(builder)
        .hasToString(
            "UserAuth.UserAuthBuilder(id=null, userId=null, isEnabled=false, "
                + "badLoginCount=0, isAdmin=false, suspendedUntil=null, suspensionMessage=null, "
                + "lastLogin=null, lastLoginMethod=null)");
  }

  @Test
  void buildIsAdmin_TrueOrFalse_MatchesBuild() {
    final UserAuthBuilder builder = UserAuth.builder().userId(79L);

    for (final boolean isAdmin : TestUtils.BOOLEANS) {
      builder.isAdmin(isAdmin);

      assertThat(builder.build()).isInstanceOf(UserAuth.class);
      assertThat(builder.build().isAdmin()).isEqualTo(isAdmin);
    }
  }

  @Test
  void buildIsEnabled_TrueOrFalse_MatchesBuild() {
    final UserAuthBuilder userAuthBuilder = UserAuth.builder().userId(100L);

    for (final boolean isEnabled : TestUtils.BOOLEANS) {
      userAuthBuilder.isEnabled(isEnabled);
      final UserAuth userAuth = userAuthBuilder.build();

      assertThat(userAuth).isInstanceOf(UserAuth.class);
      assertThat(userAuth.isEnabled()).isEqualTo(isEnabled);
    }
  }

  @Test
  void buildLastLogin_ValidTime_Builds() {
    final int originalTime = 77;
    final int[] timesToTest = {originalTime, 0, 1, 2, 1000, 4000, 1581806000, 42};
    final UserAuthBuilder userAuthBuilder =
        UserAuth.builder()
            .userId(87L)
            .lastLogin(
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(originalTime), ZoneId.systemDefault()));

    for (final int millis : timesToTest) {
      final LocalDateTime time =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());

      userAuthBuilder.lastLogin(time);

      assertThat(userAuthBuilder.build()).isInstanceOf(UserAuth.class);
      assertThat(userAuthBuilder.build().getLastLogin()).isEqualTo(time);
    }
  }

  @Test
  void buildLastLoginMethod_ValidLastLoginMethod_Builds() {
    final String[] lastLoginMethodsToTest = {
      "password", "saml", "ldap", null, "", "a", "login method with spaces", "_+^"
    };
    final UserAuthBuilder userAuthBuilder = UserAuth.builder().userId(6926L);

    for (final String lastLoginMethod : lastLoginMethodsToTest) {
      userAuthBuilder.lastLoginMethod(lastLoginMethod);

      final UserAuth userAuth = userAuthBuilder.build();

      assertThat(userAuth).isInstanceOf(UserAuth.class);
      assertThat(userAuth.getLastLoginMethod()).isEqualTo(lastLoginMethod);
    }
  }

  @Test
  void buildSuspendedUntil_ValidTime_Builds() {
    final long[] timesToTest = {0, 1, 2, 1000, 4000, 1581806000, 42000043450000L};

    final UserAuthBuilder userAuthBuilder = UserAuth.builder().userId(25L);
    for (final long suspendedUntil : timesToTest) {

      final LocalDateTime time =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(suspendedUntil), ZoneId.systemDefault());

      userAuthBuilder.suspendedUntil(time);
      final UserAuth userAuth = userAuthBuilder.build();

      assertThat(userAuth).isInstanceOf(UserAuth.class);
      assertThat(userAuth.getSuspendedUntil()).isEqualTo(time);
    }
  }

  @Test
  void buildSuspensionMessage_ValidSuspensionMessage_Builds() {
    final String originalSuspensionMessage = "suspended";
    final String[] suspensionMessagesToTest = {
      originalSuspensionMessage,
      "You are suspended!",
      null,
      "",
      "banned",
      "Long  With     Whitespace",
      "12345",
      "You tried to hack the server, fool!"
    };
    final UserAuthBuilder userAuthBuilder = UserAuth.builder().userId(269L);

    for (final String suspensionMessage : suspensionMessagesToTest) {
      userAuthBuilder.suspensionMessage(suspensionMessage);
      final UserAuth userAuth = userAuthBuilder.build();

      assertThat(userAuth).isInstanceOf(UserAuth.class);
      assertThat(userAuth.getSuspensionMessage()).isEqualTo(suspensionMessage);
    }
  }

  @Test
  void buildUserId_NullUserId_ThrowsNullPointerException() {
    final UserAuthBuilder userAuthBuilder = UserAuth.builder();
    assertThrows(NullPointerException.class, () -> userAuthBuilder.userId(null));
  }

  @Test
  void buildUserId_ValidUserId_Builds() {
    final long[] idsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (final long userId : idsToTest) {
      final UserAuthBuilder userAuthBuilder = UserAuth.builder();

      userAuthBuilder.userId(userId);

      final UserAuth userAuth = userAuthBuilder.build();

      assertThat(userAuth).isInstanceOf(UserAuth.class);
      assertThat(userAuth.getUserId()).isEqualTo(userId);
    }
  }

  @Test
  void equals_AutomaticTesting() {
    EqualsVerifier.forClass(UserAuth.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  void toString_ValidData_AsExpected() {
    final UserAuth testAuth = UserAuth.builder().userId(14L).build();

    assertThat(testAuth)
        .hasToString(
            "UserAuth(id=null, userId=14, isEnabled=false, badLoginCount=0, "
                + "isAdmin=false, suspendedUntil=null, suspensionMessage=null, "
                + "lastLogin=null, lastLoginMethod=null)");
  }

  @Test
  void withAdmin_ValidBoolean_ChangesIsAdmin() {
    final UserAuth testAuth = UserAuth.builder().userId(100L).build();

    for (final boolean isAdmin : TestUtils.BOOLEANS) {
      final UserAuth changedAuth = testAuth.withAdmin(isAdmin);
      assertThat(changedAuth).isInstanceOf(UserAuth.class);
      assertThat(changedAuth.isAdmin()).isEqualTo(isAdmin);
    }
  }

  @Test
  void withBadLoginCount_ValidBadLoginCount_ChangesBadLoginCount() {
    final int originalBadLoginCount = 0;
    final int[] testedBadLoginCounts = {
      originalBadLoginCount, 1, -1, 1000, -1000, 123456789, -12346789
    };

    final UserAuth userAuth = UserAuth.builder().userId(4963L).build();

    for (final int badLoginCount : testedBadLoginCounts) {
      final UserAuth changedAuth = userAuth.withBadLoginCount(badLoginCount);

      assertThat(changedAuth).isInstanceOf(UserAuth.class);
      assertThat(changedAuth.getBadLoginCount()).isEqualTo(badLoginCount);
    }
  }

  @Test
  void withEnabled_ValidBoolean_ChangesIsEnabled() {
    final UserAuth testAuth = UserAuth.builder().userId(123L).build();

    for (final boolean isEnabled : TestUtils.BOOLEANS) {
      final UserAuth changedAuth = testAuth.withEnabled(isEnabled);
      assertThat(changedAuth).isInstanceOf(UserAuth.class);
      assertThat(changedAuth.isEnabled()).isEqualTo(isEnabled);
    }
  }

  @Test
  void withId_ValidId_ChangesId() {
    final Long originalId = 1991L;
    final Long[] testedIds = {originalId, 55L, -7993L, 0L, -1L, 1000L, -1000L, 123456789L};

    final UserAuth newAuth = UserAuth.builder().userId(41L).id(originalId).build();

    for (final Long newId : testedIds) {
      final UserAuth changedAuth = newAuth.withId(newId);
      assertThat(changedAuth).isInstanceOf(UserAuth.class);
      assertThat(changedAuth.getId()).isEqualTo(newId);
    }
  }

  @Test
  void withLastLogin_ValidTime_ChangesLastLoginTime() {
    final long originalTime = 0L;
    final List<Long> timesToTest =
        Arrays.asList(originalTime, 1L, 2L, 1000L, 5000L, 9000990909L, 12398234987345983L);

    final List<LocalDateTime> dateTimesToTest =
        timesToTest.stream()
            .map(
                epoch ->
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()))
            .collect(Collectors.toCollection(ArrayList::new));

    final UserAuth testAuth =
        UserAuth.builder().userId(5L).lastLogin(dateTimesToTest.get(0)).build();

    for (final LocalDateTime time : dateTimesToTest) {
      final UserAuth changedAuth = testAuth.withLastLogin(time);
      assertThat(changedAuth).isInstanceOf(UserAuth.class);

      assertThat(changedAuth.getLastLogin()).isEqualTo(time);
    }
  }

  @Test
  void withLastLoginMethod_ValidLastLoginMethod_ChangesLastLoginMethod() {
    final UserAuth userAuth = UserAuth.builder().userId(95L).build();

    final String[] testedLastLoginMethods = {
      null, "", "password", "saml", "ldap", "Long  With     Whitespace", "12345"
    };

    for (final String lastLoginMethod : testedLastLoginMethods) {

      final UserAuth changedAuth = userAuth.withLastLoginMethod(lastLoginMethod);
      assertThat(changedAuth).isInstanceOf(UserAuth.class);
      assertThat(changedAuth.getLastLoginMethod()).isEqualTo(lastLoginMethod);
    }
  }

  @Test
  void withSuspendedUntil_ValidLocalDateTime_ChangesSuspendedUntil() {
    final long originalTime = 0L;

    final List<Long> timesToTest =
        Arrays.asList(originalTime, 1L, 2L, 1000L, 5000L, 9000990909L, 12398234987345983L);

    final List<LocalDateTime> dateTimesToTest =
        timesToTest.stream()
            .map(
                epoch ->
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()))
            .collect(Collectors.toCollection(ArrayList::new));

    final UserAuth testAuth =
        UserAuth.builder().userId(6L).lastLogin(dateTimesToTest.get(0)).build();

    for (final LocalDateTime time : dateTimesToTest) {
      final UserAuth changedAuth = testAuth.withSuspendedUntil(time);
      assertThat(changedAuth).isInstanceOf(UserAuth.class);
      assertThat(changedAuth.getSuspendedUntil()).isEqualTo(time);
    }

    final UserAuth changedAuth = testAuth.withSuspendedUntil(null);

    assertThat(changedAuth.getSuspendedUntil()).isNull();
  }

  @Test
  void withSuspensionMessage_ValidSuspensionMessage_ChangesSuspensionMessage() {
    final UserAuth userAuth = UserAuth.builder().userId(591L).build();
    assertThat(userAuth.getSuspensionMessage()).isNull();
    final String[] testedSuspensionMessages = {
      null,
      "",
      "banned",
      "Long  With     Whitespace",
      "12345",
      "You tried to hack the server, fool!"
    };

    for (final String suspensionMessage : testedSuspensionMessages) {
      final UserAuth changedAuth = userAuth.withSuspensionMessage(suspensionMessage);
      assertThat(changedAuth).isInstanceOf(UserAuth.class);
      assertThat(changedAuth.getSuspensionMessage()).isEqualTo(suspensionMessage);
    }
  }

  @Test
  void withUserId_NullUserId_ThrowsNullPointerException() {
    final UserAuth userAuth = UserAuth.builder().userId(1L).build();
    assertThrows(NullPointerException.class, () -> userAuth.withUserId(null));
  }

  @Test
  void withUserId_ValidUserId_ChangesUserId() {
    final UserAuth userAuth = UserAuth.builder().userId(TestUtils.INITIAL_LONG).build();

    for (final Long userId : TestUtils.LONGS) {
      final UserAuth newUserAuth = userAuth.withUserId(userId);
      assertThat(newUserAuth).isInstanceOf(UserAuth.class);
      assertThat(newUserAuth.getUserId()).isEqualTo(userId);
    }
  }
}
