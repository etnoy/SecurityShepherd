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
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.Auth;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.SamlAuth;
import org.owasp.securityshepherd.model.Auth.AuthBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("Auth")
public class AuthTest {

  private static final boolean[] BOOLEANS = {false, true};

  @Test
  public void authBuildertoString_ValidData_AsExpected() {
    final AuthBuilder builder = Auth.builder();

    assertThat(builder.toString(), is(
        "Auth.AuthBuilder(id=null, userId=null, isEnabled=false, badLoginCount=0, isAdmin=false, suspendedUntil=null, suspensionMessage=null, accountCreated=null, lastLogin=null, lastLoginMethod=null, password=null, saml=null)"));
  }

  @Test
  public void persistenceConstructor_ValidData_ConstructsObject() {
    final Auth testAuth = new Auth(1, 2, false, 3, false, null, null, null, null, null);

    assertThat(testAuth.getId(), is(1));
    assertThat(testAuth.getUserId(), is(2));
    assertThat(testAuth.isEnabled(), is(false));
    assertThat(testAuth.getBadLoginCount(), is(3));
    assertThat(testAuth.isAdmin(), is(false));
    assertThat(testAuth.getSuspendedUntil(), is(nullValue()));
    assertThat(testAuth.getSuspensionMessage(), is(nullValue()));
    assertThat(testAuth.getAccountCreated(), is(nullValue()));
    assertThat(testAuth.getLastLogin(), is(nullValue()));
    assertThat(testAuth.getLastLoginMethod(), is(nullValue()));
    assertThat(testAuth.getPassword(), is(nullValue()));
    assertThat(testAuth.getSaml(), is(nullValue()));
  }

  @Test
  public void buildAccountCreated_ValidTime_Builds() {
    final int[] timesToTest = {0, 1, 2, 1000, 4000, 1581806000, 42};

    for (final int accountCreated : timesToTest) {
      final AuthBuilder builder = Auth.builder();

      final LocalDateTime time =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(accountCreated), ZoneId.systemDefault());

      builder.accountCreated(time);

      assertThat(builder.build(), instanceOf(Auth.class));
      assertThat(builder.build().getAccountCreated(), is(time));
    }
  }

  @Test
  public void buildBadLoginCount_ValidBadLoginCount_Builds() {
    final int[] badLoginCountsToTest = {0, 1, 1000, -1, 999999, 42, 567890, -1234567};

    for (int badLoginCount : badLoginCountsToTest) {
      final AuthBuilder builder = Auth.builder();

      builder.badLoginCount(badLoginCount);

      assertThat(builder.build(), instanceOf(Auth.class));
      assertThat(builder.build().getBadLoginCount(), is(badLoginCount));
    }
  }

  @Test
  public void buildIsAdmin_TrueOrFalse_MatchesBuild() {
    for (boolean isAdmin : BOOLEANS) {
      final AuthBuilder builder = Auth.builder();

      builder.isAdmin(isAdmin);

      assertThat(builder.build(), instanceOf(Auth.class));
      assertThat(builder.build().isAdmin(), is(isAdmin));
    }
  }

  @Test
  public void buildIsEnabled_TrueOrFalse_MatchesBuild() {
    for (boolean isEnabled : BOOLEANS) {
      final AuthBuilder builder = Auth.builder();

      builder.isEnabled(isEnabled);

      assertThat(builder.build(), instanceOf(Auth.class));
      assertThat(builder.build().isEnabled(), is(isEnabled));
    }
  }

  @Test
  public void buildLastLogin_ValidTime_Builds() {
    final int[] timesToTest = {0, 1, 2, 1000, 4000, 1581806000, 42};
    for (int lastLogin : timesToTest) {

      final AuthBuilder builder = Auth.builder();

      final LocalDateTime time =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(lastLogin), ZoneId.systemDefault());

      builder.lastLogin(time);

      assertThat(builder.build(), instanceOf(Auth.class));
      assertThat(builder.build().getLastLogin(), is(time));
    }
  }

  @Test
  public void buildLastLoginMethod_ValidLastLoginMethod_Builds() {
    final String[] lastLoginMethodsToTest =
        {"password", "saml", "ldap", null, "", "a", "login method with spaces", "_+^"};

    for (String lastLoginMethod : lastLoginMethodsToTest) {
      final AuthBuilder builder = Auth.builder();

      builder.lastLoginMethod(lastLoginMethod);

      assertThat(builder.build(), instanceOf(Auth.class));
      assertThat(builder.build().getLastLoginMethod(), is(lastLoginMethod));
    }
  }

  @Test
  public void buildPassword_ValidPasswordAuth_Builds() {
    final String[] loginNamesToTest = {"", "a", "loginName"};

    for (String loginName : loginNamesToTest) {
      final AuthBuilder builder = Auth.builder();
      final PasswordAuth passwordAuth =
          PasswordAuth.builder().loginName(loginName).hashedPassword("passwordHash").build();

      builder.password(passwordAuth);

      assertThat(builder.build(), instanceOf(Auth.class));
      assertThat(builder.build().getPassword(), is(passwordAuth));
    }

    final AuthBuilder builder = Auth.builder().password(null);

    assertThat(builder.build(), instanceOf(Auth.class));
    assertThat(builder.build().getPassword(), is(nullValue()));
  }

  @Test
  public void buildSaml_ValidSamlAuth_Builds() {
    final String[] samlIdsToTest = {"", "a", "loginName", "me@example.com"};

    for (String samlId : samlIdsToTest) {
      final AuthBuilder builder = Auth.builder();
      final SamlAuth samlAuth = SamlAuth.builder().userId(3).samlId(samlId).build();

      builder.saml(samlAuth);

      assertThat(builder.build(), instanceOf(Auth.class));
      assertThat(builder.build().getSaml(), is(samlAuth));
    }

    final AuthBuilder builder = Auth.builder().saml(null);

    assertThat(builder.build(), instanceOf(Auth.class));
    assertThat(builder.build().getSaml(), is(nullValue()));
  }

  @Test
  public void buildSuspendedUntil_ValidTime_Builds() {
    final long[] timesToTest = {0, 1, 2, 1000, 4000, 1581806000, 42000043450000L};

    for (final long suspendedUntil : timesToTest) {
      final AuthBuilder builder = Auth.builder();

      final LocalDateTime time =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(suspendedUntil), ZoneId.systemDefault());

      builder.suspendedUntil(time);

      assertThat(builder.build(), instanceOf(Auth.class));
      assertThat(builder.build().getSuspendedUntil(), is(time));
    }
  }

  @Test
  public void buildSuspensionMessage_ValidSuspensionMessage_Builds() {
    final String[] suspensionMessagesToTest = {"You are suspended!", null, "", "banned",
        "Long  With     Whitespace", "12345", "You tried to hack the server, fool!"};

    for (String suspensionMessage : suspensionMessagesToTest) {
      final AuthBuilder builder = Auth.builder();

      builder.suspensionMessage(suspensionMessage);

      assertThat(builder.build(), instanceOf(Auth.class));
      assertThat(builder.build().getSuspensionMessage(), is(suspensionMessage));
    }
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(Auth.class).verify();
  }

  @Test
  public void toString_ValidData_AsExpected() {
    final Auth testAuth = Auth.builder().build();

    assertThat(testAuth.toString(), is(
        "Auth(id=null, userId=null, isEnabled=false, badLoginCount=0, isAdmin=false, suspendedUntil=null, suspensionMessage=null, accountCreated=null, lastLogin=null, lastLoginMethod=null, password=null, saml=null)"));
  }

  @Test
  public void withAccountCreated_ValidTime_ChangesAccountCreationTime() {
    final long originalTime = 0L;

    final List<Long> timesToTest =
        Arrays.asList(originalTime, 1L, 2L, 1000L, 5000L, 9000990909L, 12398234987345983L);

    final List<LocalDateTime> dateTimesToTest = timesToTest.stream()
        .map(epoch -> LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()))
        .collect(Collectors.toCollection(ArrayList::new));

    final Auth testAuth = Auth.builder().accountCreated(dateTimesToTest.get(0)).build();

    for (final LocalDateTime time : dateTimesToTest) {
      final Auth changedAuth = testAuth.withAccountCreated(time);

      assertThat(changedAuth.getAccountCreated(), is(time));
    }
  }

  @Test
  public void withAdmin_ValidBoolean_ChangesIsAdmin() {
    final Auth testAuth = Auth.builder().build();

    for (final boolean isAdmin : BOOLEANS) {
      final Auth changedAuth = testAuth.withAdmin(isAdmin);
      assertThat(changedAuth.isAdmin(), is(isAdmin));
    }
  }

  @Test
  public void withBadLoginCount_ValidBadLoginCount_ChangesBadLoginCount() {
    final int originalBadLoginCount = 0;
    final int[] testedBadLoginCounts =
        {originalBadLoginCount, 1, -1, 1000, -1000, 123456789, -12346789};

    final Auth testAuth = Auth.builder().build();

    for (final int badLoginCount : testedBadLoginCounts) {
      final Auth changedAuth = testAuth.withBadLoginCount(badLoginCount);
      assertThat(changedAuth.getBadLoginCount(), is(badLoginCount));
    }
  }

  @Test
  public void withEnabled_ValidBoolean_ChangesIsEnabled() {
    final Auth testAuth = Auth.builder().build();

    for (boolean isEnabled : BOOLEANS) {
      final Auth changedAuth = testAuth.withEnabled(isEnabled);
      assertThat(changedAuth.isEnabled(), is(isEnabled));
    }
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final int originalId = 1;
    final int[] testedIds = {originalId, 0, -1, 1000, -1000, 123456789};

    final Auth newAuth = Auth.builder().id(originalId).build();

    assertThat(newAuth.getId(), is(originalId));

    for (final int newId : testedIds) {
      final Auth changedAuth = newAuth.withId(newId);
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

    final Auth testAuth = Auth.builder().lastLogin(dateTimesToTest.get(0)).build();

    for (final LocalDateTime time : dateTimesToTest) {
      final Auth changedAuth = testAuth.withLastLogin(time);

      assertThat(changedAuth.getLastLogin(), is(time));
    }
  }

  @Test
  public void withLastLoginMethod_ValidLastLoginMethod_ChangesLastLoginMethod() {
    final Auth auth = Auth.builder().build();

    assertThat(auth.getLastLoginMethod(), is(nullValue()));

    final String[] testedLastLoginMethods =
        {null, "", "password", "saml", "ldap", "Long  With     Whitespace", "12345"};

    for (String newLastLoginMethod : testedLastLoginMethods) {

      final Auth changedAuth = auth.withLastLoginMethod(newLastLoginMethod);
      assertThat(changedAuth.getLastLoginMethod(), is(newLastLoginMethod));
    }
  }

  @Test
  public void withPassword_ValidPasswordAuth_ChangesPasswordAuth() {
    final PasswordAuth passwordAuth1 =
        PasswordAuth.builder().loginName("TestPassword1").hashedPassword("passwordHash").build();
    final PasswordAuth passwordAuth2 =
        PasswordAuth.builder().loginName("TestPassword2").hashedPassword("passwordHash").build();

    final Auth testAuth = Auth.builder().password(passwordAuth1).build();

    final Auth changedAuth1 = testAuth.withPassword(passwordAuth1);
    final Auth changedAuth2 = testAuth.withPassword(passwordAuth2);

    assertThat(changedAuth1.getPassword(), is(passwordAuth1));
    assertThat(changedAuth2.getPassword(), is(passwordAuth2));
  }

  @Test
  public void withSaml_ValidSamlAuth_ChangesSamlAuth() {
    final SamlAuth samlAuth1 = SamlAuth.builder().samlId("samlid1").userId(3).build();
    final SamlAuth samlAuth2 = SamlAuth.builder().samlId("samlid2").userId(3).build();

    final Auth testAuth = Auth.builder().saml(samlAuth1).build();

    final Auth changedAuth1 = testAuth.withSaml(samlAuth1);
    final Auth changedAuth2 = testAuth.withSaml(samlAuth2);

    assertThat(changedAuth1.getSaml(), is(samlAuth1));
    assertThat(changedAuth2.getSaml(), is(samlAuth2));
  }

  @Test
  public void withSuspendedUntil_ValidLocalDateTime_ChangesSuspendedUntil() {
    final long originalTime = 0L;

    final List<Long> timesToTest =
        Arrays.asList(originalTime, 1L, 2L, 1000L, 5000L, 9000990909L, 12398234987345983L);

    final List<LocalDateTime> dateTimesToTest = timesToTest.stream()
        .map(epoch -> LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()))
        .collect(Collectors.toCollection(ArrayList::new));

    final Auth testAuth = Auth.builder().lastLogin(dateTimesToTest.get(0)).build();

    for (final LocalDateTime time : dateTimesToTest) {
      final Auth changedAuth = testAuth.withSuspendedUntil(time);

      assertThat(changedAuth.getSuspendedUntil(), is(time));
    }
  }

  @Test
  public void withSuspensionMessage_ValidSuspensionMessage_ChangesSuspensionMessage() {
    final Auth auth = Auth.builder().build();

    assertThat(auth.getSuspensionMessage(), is(nullValue()));

    final String[] testedSuspensionMessages = {null, "", "banned", "Long  With     Whitespace",
        "12345", "You tried to hack the server, fool!"};

    for (String newSuspensionMessage : testedSuspensionMessages) {
      final Auth changedAuth = auth.withSuspensionMessage(newSuspensionMessage);
      assertThat(changedAuth.getSuspensionMessage(), is(newSuspensionMessage));
    }
  }

  @Test
  public void withUser_ValidUser_ChangesUser() {
    final int originalUser = 1;
    final int[] testedUsers = {originalUser, 0, -1, 1000, -1000, 123456789};

    final Auth newAuth = Auth.builder().userId(originalUser).build();

    assertThat(newAuth.getUserId(), is(originalUser));

    for (int newUser : testedUsers) {
      final Auth changedAuth = newAuth.withUserId(newUser);
      assertThat(changedAuth.getUserId(), is(newUser));
    }
  }
}
