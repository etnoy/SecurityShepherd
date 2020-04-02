package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.model.UserAuth;
import org.owasp.securityshepherd.model.UserAuth.UserAuthBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("UserAuth unit test")
public class UserAuthTest {
  @Test
  public void buildAccountCreated_ValidTime_Builds() {
    final int[] timesToTest = {0, 1, 2, 1000, 4000, 1581806000, 42};

    final UserAuthBuilder userAuthBuilder = UserAuth.builder().userId(5L);

    for (final int accountCreated : timesToTest) {
      final LocalDateTime time =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(accountCreated), ZoneId.systemDefault());

      userAuthBuilder.accountCreated(time);

      assertThat(userAuthBuilder.build(), instanceOf(UserAuth.class));
      assertThat(userAuthBuilder.build().getAccountCreated(), is(time));
    }
  }

  @Test
  public void buildBadLoginCount_ValidBadLoginCount_Builds() {
    final int[] badLoginCountsToTest = {0, 1, 1000, -1, 999999, 42, 567890, -1234567};
    final UserAuthBuilder userAuthBuilder = UserAuth.builder().userId(100L);

    for (final int badLoginCount : badLoginCountsToTest) {
      userAuthBuilder.badLoginCount(badLoginCount);
      final UserAuth userAuth = userAuthBuilder.build();

      assertThat(userAuth, instanceOf(UserAuth.class));
      assertThat(userAuth.getBadLoginCount(), is(badLoginCount));
    }
  }

  @Test
  public void builderToString_ValidData_AsExpected() {
    final UserAuthBuilder builder = UserAuth.builder();

    assertThat(builder.toString(), is(
        "UserAuth.UserAuthBuilder(id=null, userId=null, isEnabled=false, badLoginCount=0, isAdmin=false, suspendedUntil=null, suspensionMessage=null, accountCreated=null, lastLogin=null, lastLoginMethod=null)"));
  }

  @Test
  public void buildIsAdmin_TrueOrFalse_MatchesBuild() {
    final UserAuthBuilder builder = UserAuth.builder().userId(79L);

    for (final boolean isAdmin : TestUtils.BOOLEANS) {
      builder.isAdmin(isAdmin);

      assertThat(builder.build(), instanceOf(UserAuth.class));
      assertThat(builder.build().isAdmin(), is(isAdmin));
    }
  }

  @Test
  public void buildIsEnabled_TrueOrFalse_MatchesBuild() {
    final UserAuthBuilder userAuthBuilder = UserAuth.builder().userId(100L);

    for (final boolean isEnabled : TestUtils.BOOLEANS) {
      userAuthBuilder.isEnabled(isEnabled);
      final UserAuth userAuth = userAuthBuilder.build();

      assertThat(userAuth, instanceOf(UserAuth.class));
      assertThat(userAuth.isEnabled(), is(isEnabled));
    }
  }

  @Test
  public void buildLastLogin_ValidTime_Builds() {
    final int originalTime = 77;
    final int[] timesToTest = {originalTime, 0, 1, 2, 1000, 4000, 1581806000, 42};
    final UserAuthBuilder userAuthBuilder = UserAuth.builder().userId(87L).lastLogin(
        LocalDateTime.ofInstant(Instant.ofEpochMilli(originalTime), ZoneId.systemDefault()));

    for (final int millis : timesToTest) {
      final LocalDateTime time =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());

      userAuthBuilder.lastLogin(time);

      assertThat(userAuthBuilder.build(), instanceOf(UserAuth.class));
      assertThat(userAuthBuilder.build().getLastLogin(), is(time));
    }
  }

  @Test
  public void buildLastLoginMethod_ValidLastLoginMethod_Builds() {
    final String[] lastLoginMethodsToTest =
        {"password", "saml", "ldap", null, "", "a", "login method with spaces", "_+^"};
    final UserAuthBuilder userAuthBuilder = UserAuth.builder().userId(6926L);

    for (final String lastLoginMethod : lastLoginMethodsToTest) {
      userAuthBuilder.lastLoginMethod(lastLoginMethod);

      final UserAuth userAuth = userAuthBuilder.build();

      assertThat(userAuth, instanceOf(UserAuth.class));
      assertThat(userAuth.getLastLoginMethod(), is(lastLoginMethod));
    }
  }

  @Test
  public void buildSuspendedUntil_ValidTime_Builds() {
    final long[] timesToTest = {0, 1, 2, 1000, 4000, 1581806000, 42000043450000L};

    final UserAuthBuilder userAuthBuilder = UserAuth.builder().userId(25L);
    for (final long suspendedUntil : timesToTest) {

      final LocalDateTime time =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(suspendedUntil), ZoneId.systemDefault());

      userAuthBuilder.suspendedUntil(time);
      final UserAuth userAuth = userAuthBuilder.build();

      assertThat(userAuth, instanceOf(UserAuth.class));
      assertThat(userAuth.getSuspendedUntil(), is(time));
    }
  }

  @Test
  public void buildSuspensionMessage_ValidSuspensionMessage_Builds() {
    final String originalSuspensionMessage = "suspended";
    final String[] suspensionMessagesToTest =
        {originalSuspensionMessage, "You are suspended!", null, "", "banned",
            "Long  With     Whitespace", "12345", "You tried to hack the server, fool!"};
    final UserAuthBuilder userAuthBuilder = UserAuth.builder().userId(269L);

    for (final String suspensionMessage : suspensionMessagesToTest) {
      userAuthBuilder.suspensionMessage(suspensionMessage);
      final UserAuth userAuth = userAuthBuilder.build();

      assertThat(userAuth, instanceOf(UserAuth.class));
      assertThat(userAuth.getSuspensionMessage(), is(suspensionMessage));
    }
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(UserAuth.class).verify();
  }

  @Test
  public void toString_ValidData_AsExpected() {
    final UserAuth testAuth = UserAuth.builder().userId(14L).build();

    assertThat(testAuth.toString(), is(
        "UserAuth(id=null, userId=14, isEnabled=false, badLoginCount=0, isAdmin=false, suspendedUntil=null, suspensionMessage=null, accountCreated=null, lastLogin=null, lastLoginMethod=null)"));
  }

  @Test
  public void withAccountCreated_ValidTime_ChangesAccountCreationTime() {
    final long originalTime = 0L;

    final List<Long> timesToTest =
        Arrays.asList(originalTime, 1L, 2L, 1000L, 5000L, 9000990909L, 12398234987345983L);

    final List<LocalDateTime> dateTimesToTest = timesToTest.stream()
        .map(epoch -> LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()))
        .collect(Collectors.toCollection(ArrayList::new));

    final UserAuth testAuth =
        UserAuth.builder().userId(19L).accountCreated(dateTimesToTest.get(0)).build();

    for (final LocalDateTime time : dateTimesToTest) {
      final UserAuth changedAuth = testAuth.withAccountCreated(time);
      assertThat(changedAuth, instanceOf(UserAuth.class));
      assertThat(changedAuth.getAccountCreated(), is(time));
    }
  }

  @Test
  public void withAdmin_ValidBoolean_ChangesIsAdmin() {
    final UserAuth testAuth = UserAuth.builder().userId(100L).build();

    for (final boolean isAdmin : TestUtils.BOOLEANS) {
      final UserAuth changedAuth = testAuth.withAdmin(isAdmin);
      assertThat(changedAuth, instanceOf(UserAuth.class));
      assertThat(changedAuth.isAdmin(), is(isAdmin));
    }
  }

  @Test
  public void withBadLoginCount_ValidBadLoginCount_ChangesBadLoginCount() {
    final int originalBadLoginCount = 0;
    final int[] testedBadLoginCounts =
        {originalBadLoginCount, 1, -1, 1000, -1000, 123456789, -12346789};

    final UserAuth userAuth = UserAuth.builder().userId(4963L).build();

    for (final int badLoginCount : testedBadLoginCounts) {
      final UserAuth changedAuth = userAuth.withBadLoginCount(badLoginCount);

      assertThat(changedAuth, instanceOf(UserAuth.class));
      assertThat(changedAuth.getBadLoginCount(), is(badLoginCount));
    }
  }

  @Test
  public void withEnabled_ValidBoolean_ChangesIsEnabled() {
    final UserAuth testAuth = UserAuth.builder().userId(123L).build();

    for (final boolean isEnabled : TestUtils.BOOLEANS) {
      final UserAuth changedAuth = testAuth.withEnabled(isEnabled);
      assertThat(changedAuth, instanceOf(UserAuth.class));
      assertThat(changedAuth.isEnabled(), is(isEnabled));
    }
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final Long originalId = 1991L;
    final Long[] testedIds = {originalId, 55L, -7993L, 0L, -1L, 1000L, -1000L, 123456789L};

    final UserAuth newAuth = UserAuth.builder().userId(41L).id(originalId).build();

    for (final Long newId : testedIds) {
      final UserAuth changedAuth = newAuth.withId(newId);
      assertThat(changedAuth, instanceOf(UserAuth.class));
      assertThat(changedAuth.getId(), is(newId));
    }
  }

  @Test
  public void withLastLogin_ValidTime_ChangesLastLoginTime() {
    final long originalTime = 0L;
    final List<Long> timesToTest =
        Arrays.asList(originalTime, 1L, 2L, 1000L, 5000L, 9000990909L, 12398234987345983L);

    final List<LocalDateTime> dateTimesToTest = timesToTest.stream()
        .map(epoch -> LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()))
        .collect(Collectors.toCollection(ArrayList::new));

    final UserAuth testAuth =
        UserAuth.builder().userId(5L).lastLogin(dateTimesToTest.get(0)).build();

    for (final LocalDateTime time : dateTimesToTest) {
      final UserAuth changedAuth = testAuth.withLastLogin(time);
      assertThat(changedAuth, instanceOf(UserAuth.class));

      assertThat(changedAuth.getLastLogin(), is(time));
    }
  }

  @Test
  public void withLastLoginMethod_ValidLastLoginMethod_ChangesLastLoginMethod() {
    final UserAuth userAuth = UserAuth.builder().userId(95L).build();

    final String[] testedLastLoginMethods =
        {null, "", "password", "saml", "ldap", "Long  With     Whitespace", "12345"};

    for (final String lastLoginMethod : testedLastLoginMethods) {

      final UserAuth changedAuth = userAuth.withLastLoginMethod(lastLoginMethod);
      assertThat(changedAuth, instanceOf(UserAuth.class));
      assertThat(changedAuth.getLastLoginMethod(), is(lastLoginMethod));
    }
  }

  @Test
  public void withSuspendedUntil_ValidLocalDateTime_ChangesSuspendedUntil() {
    final long originalTime = 0L;

    final List<Long> timesToTest =
        Arrays.asList(originalTime, 1L, 2L, 1000L, 5000L, 9000990909L, 12398234987345983L);

    final List<LocalDateTime> dateTimesToTest = timesToTest.stream()
        .map(epoch -> LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()))
        .collect(Collectors.toCollection(ArrayList::new));

    final UserAuth testAuth =
        UserAuth.builder().userId(6L).lastLogin(dateTimesToTest.get(0)).build();

    for (final LocalDateTime time : dateTimesToTest) {
      final UserAuth changedAuth = testAuth.withSuspendedUntil(time);
      assertThat(changedAuth, instanceOf(UserAuth.class));
      assertThat(changedAuth.getSuspendedUntil(), is(time));
    }

    final UserAuth changedAuth = testAuth.withSuspendedUntil(null);

    assertThat(changedAuth.getSuspendedUntil(), is(nullValue()));
  }

  @Test
  public void withSuspensionMessage_ValidSuspensionMessage_ChangesSuspensionMessage() {
    final UserAuth userAuth = UserAuth.builder().userId(591L).build();
    assertThat(userAuth.getSuspensionMessage(), is(nullValue()));
    final String[] testedSuspensionMessages = {null, "", "banned", "Long  With     Whitespace",
        "12345", "You tried to hack the server, fool!"};

    for (final String suspensionMessage : testedSuspensionMessages) {
      final UserAuth changedAuth = userAuth.withSuspensionMessage(suspensionMessage);
      assertThat(changedAuth, instanceOf(UserAuth.class));
      assertThat(changedAuth.getSuspensionMessage(), is(suspensionMessage));
    }
  }

  @Test
  public void withUserId_ValidUser_ChangesUser() {
    final long originalUserId = 1;
    final long[] testedUserIds = {originalUserId, 0, -1, 1000, -1000, 123456789};

    final UserAuth newAuth = UserAuth.builder().userId(originalUserId).build();

    for (final long newUserId : testedUserIds) {
      final UserAuth changedAuth = newAuth.withUserId(newUserId);
      assertThat(changedAuth.getUserId(), is(newUserId));
    }
  }
}
