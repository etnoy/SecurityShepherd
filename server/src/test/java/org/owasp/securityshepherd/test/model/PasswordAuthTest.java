package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.model.PasswordAuth;
import org.owasp.securityshepherd.model.PasswordAuth.PasswordAuthBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("PasswordAuth unit test")
public class PasswordAuthTest {
  @Test
  public void build_HashedPasswordNotGiven_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().userId(1L).loginName("TestUser");
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> passwordAuthBuilder.build());
    assertThat(thrownException.getMessage(), is("hashedPassword is marked non-null but is null"));
  }

  @Test
  public void build_LoginNameNotGiven_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().userId(1L).hashedPassword("hashedPass");
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> passwordAuthBuilder.build());
    assertThat(thrownException.getMessage(), is("loginName is marked non-null but is null"));

  }

  @Test
  public void build_UserIdNotGiven_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().loginName("TestUser").hashedPassword("hashedPass");
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> passwordAuthBuilder.build());
    assertThat(thrownException.getMessage(), is("userId is marked non-null but is null"));

  }

  @Test
  public void builderToString_ValidData_AsExpected() {
    assertThat(PasswordAuth.builder().loginName("TestUser").hashedPassword("987").toString(), is(
        "PasswordAuth.PasswordAuthBuilder(id=null, userId=null, loginName=TestUser, hashedPassword=987, isPasswordNonExpired=false)"));
    assertThat(
        PasswordAuth.builder().id(3L).userId(4L).loginName("TestUser2").hashedPassword("123")
            .isPasswordNonExpired(true).toString(),
        is("PasswordAuth.PasswordAuthBuilder(id=3, userId=4, loginName=TestUser2, hashedPassword=123, isPasswordNonExpired=true)"));
  }

  @Test
  public void buildHashedPassword_NullHashedPassword_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
    assertThrows(NullPointerException.class, () -> passwordAuthBuilder.hashedPassword(null));
  }

  @Test
  public void buildHashedPassword_ValidHashedPassword_Builds() {
    final String[] hashedPasswordsToTest =
        {"abc123", "0xdeadbeef", "", "me@example.com", "a", "1", "password"};
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().userId(555L).loginName("TestUser");

    for (final String hashedPassword : hashedPasswordsToTest) {
      passwordAuthBuilder.hashedPassword(hashedPassword);

      final PasswordAuth passwordAuth = passwordAuthBuilder.build();

      assertThat(passwordAuth, instanceOf(PasswordAuth.class));
      assertThat(passwordAuth.getHashedPassword(), is(hashedPassword));
    }
  }

  @Test
  public void buildIsPasswordExpired_TrueOrFalse_MatchesBuild() {
    for (final boolean isPasswordNonExpired : TestUtils.BOOLEANS) {
      final PasswordAuthBuilder builder =
          PasswordAuth.builder().userId(45L).loginName("TestUser").hashedPassword("passwordHash");

      builder.isPasswordNonExpired(isPasswordNonExpired);

      assertThat(builder.build(), instanceOf(PasswordAuth.class));
      assertThat(builder.build().isPasswordNonExpired(), is(isPasswordNonExpired));
    }
  }

  @Test
  public void buildLoginName_NullLoginName_ThrowsException() {
    assertThrows(NullPointerException.class, () -> PasswordAuth.builder().loginName(null));
  }

  @Test
  public void buildLoginName_ValidLoginName_Builds() {
    final String[] loginNamesToTest = {"", "me@example.com", "a", "1", "userName"};

    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().userId(681L).hashedPassword("passwordHash");

    for (final String loginName : loginNamesToTest) {
      passwordAuthBuilder.loginName(loginName);

      final PasswordAuth changedPasswordAuth = passwordAuthBuilder.build();
      assertThat(changedPasswordAuth, instanceOf(PasswordAuth.class));
      assertThat(changedPasswordAuth.getLoginName(), is(loginName));
    }
  }

  @Test
  public void buildUserId_NullUserId_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder();
    assertThrows(NullPointerException.class, () -> passwordAuthBuilder.userId(null));
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(PasswordAuth.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void toString_ValidData_AsExpected() {
    assertThat(
        PasswordAuth.builder().loginName("TestUser").hashedPassword("hashedPassword").userId(1278L)
            .build().toString(),
        is("PasswordAuth(id=null, userId=1278, loginName=TestUser, hashedPassword=hashedPassword, isPasswordNonExpired=false)"));
    assertThat(
        PasswordAuth.builder().id(5L).userId(95L).loginName("TestUser3")
            .hashedPassword("hashedPassword2").build().toString(),
        is("PasswordAuth(id=5, userId=95, loginName=TestUser3, hashedPassword=hashedPassword2, isPasswordNonExpired=false)"));
    assertThat(
        PasswordAuth.builder().id(14L).userId(35L).loginName("TestUser4")
            .hashedPassword("hashedPassword3").isPasswordNonExpired(true).build().toString(),
        is("PasswordAuth(id=14, userId=35, loginName=TestUser4, hashedPassword=hashedPassword3, isPasswordNonExpired=true)"));
  }

  @Test
  public void withHashedPassword_NullHashedPassword_ThrowsNullPointerException() {
    final PasswordAuth passwordAuth = PasswordAuth.builder().userId(1L).loginName("Test")
        .hashedPassword("hashedPassword").build();
    assertThrows(NullPointerException.class, () -> passwordAuth.withHashedPassword(null));
  }

  @Test
  public void withHashedPassword_ValidHashedPassword_ChangesHashedPassword() {
    final String originalHashedPassword = "passwordHash";

    final PasswordAuth passwordAuth = PasswordAuth.builder().userId(46L).loginName("abc123hash")
        .hashedPassword(originalHashedPassword).build();

    final String[] testedHashedPasswords = {originalHashedPassword, "abc123hash", "", "!\"+,-",
        "userName", "Long  With     Whitespace", "12345"};

    for (final String hashedPassword : testedHashedPasswords) {
      assertThat(passwordAuth.withHashedPassword(hashedPassword).getHashedPassword(),
          is(hashedPassword));
    }
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final String loginName = "testUser";
    final String hashedPassword = "hashedPassword";

    final long originalId = 1;
    final long[] testedIds = {originalId, 0, -1, 1000, -1000, 123456789};

    final PasswordAuth newPasswordAuth = PasswordAuth.builder().userId(6L).id(originalId)
        .loginName(loginName).hashedPassword(hashedPassword).build();

    assertThat(newPasswordAuth.getId(), is(originalId));

    for (final long newId : testedIds) {
      assertThat(newPasswordAuth.withId(newId).getId(), is(newId));
    }
  }

  @Test
  public void withLoginName_NullLoginName_ThrowsNullPointerException() {
    final PasswordAuth passwordAuth = PasswordAuth.builder().userId(1L).loginName("Test")
        .hashedPassword("hashedPassword").build();
    assertThrows(NullPointerException.class, () -> passwordAuth.withLoginName(null));
  }

  @Test
  public void withLoginName_ValidLoginName_ChangesLoginName() {
    final String originalLoginName = "me@example.com";

    final PasswordAuth passwordAuth = PasswordAuth.builder().loginName(originalLoginName)
        .userId(31L).hashedPassword("passwordHash").build();

    final String[] testedLoginNames =
        {originalLoginName, "", "userName", "Long  With     Whitespace", "12345"};

    for (final String loginName : testedLoginNames) {
      final PasswordAuth changedPasswordAuth = passwordAuth.withLoginName(loginName);
      assertThat(changedPasswordAuth.getLoginName(), is(loginName));
    }
  }

  @Test
  public void withPasswordNonExpired_ValidBoolean_ChangesPasswordExpired() {
    final PasswordAuth passwordAuth = PasswordAuth.builder().userId(45L).loginName("TestUser")
        .hashedPassword("passwordHash").build();

    for (final boolean isPasswordNonExpired : TestUtils.BOOLEANS) {
      final PasswordAuth changedAuth = passwordAuth.withPasswordNonExpired(isPasswordNonExpired);

      assertThat(changedAuth, instanceOf(PasswordAuth.class));
      assertThat(changedAuth.isPasswordNonExpired(), is(isPasswordNonExpired));
    }
  }


  @Test
  public void withUser_ValidUser_ChangesUser() {
    final long originalUser = 1;
    final long[] testedUsers = {originalUser, 0, -1, 1000, -1000, 123456789};

    final PasswordAuth newPasswordAuth = PasswordAuth.builder().loginName("TestName")
        .hashedPassword("hashedPassword").userId(originalUser).build();

    assertThat(newPasswordAuth.getUserId(), is(originalUser));

    for (final long newUser : testedUsers) {
      assertThat(newPasswordAuth.withUserId(newUser).getUserId(), is(newUser));
    }
  }

  @Test
  public void withUserId_NullUserId_ThrowsNullPointerException() {
    final PasswordAuth passwordAuth = PasswordAuth.builder().userId(1L).loginName("TestUser")
        .hashedPassword("TestPassword").build();
    assertThrows(NullPointerException.class, () -> passwordAuth.withUserId(null));
  }

  @Test
  public void withUserId_ValidUserId_ChangesUserId() {
    final PasswordAuth passwordAuth = PasswordAuth.builder().userId(TestUtils.INITIAL_LONG)
        .loginName("TestUser").hashedPassword("TestPassword").build();

    for (final Long userId : TestUtils.LONGS) {
      final PasswordAuth newPasswordAuth = passwordAuth.withUserId(userId);
      assertThat(newPasswordAuth, is(instanceOf(PasswordAuth.class)));
      assertThat(newPasswordAuth.getUserId(), is(userId));
    }
  }
}
