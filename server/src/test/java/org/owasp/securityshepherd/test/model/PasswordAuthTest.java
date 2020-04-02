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

//@ExtendWith(SpringExtension.class)
//@SpringBootTest
@DisplayName("PasswordAuth unit test")
public class PasswordAuthTest {
  @Test
  public void builderToString_ValidData_AsExpected() {
    assertThat(PasswordAuth.builder().loginName("TestUser").hashedPassword("987").toString(), is(
        "PasswordAuth.PasswordAuthBuilder(id=null, userId=null, loginName=TestUser, hashedPassword=987, isPasswordNonExpired=false)"));
    assertThat(
        PasswordAuth.builder().id(3).userId(4).loginName("TestUser2").hashedPassword("123")
            .isPasswordNonExpired(true).toString(),
        is("PasswordAuth.PasswordAuthBuilder(id=3, userId=4, loginName=TestUser2, hashedPassword=123, isPasswordNonExpired=true)"));
  }

  @Test
  public void buildHashedPassword_NullHashedPassword_ThrowsException() {
    assertThrows(NullPointerException.class, () -> PasswordAuth.builder().hashedPassword(null));
  }

  @Test
  public void build_HashedPasswordNotGiven_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder = PasswordAuth.builder().loginName("TestUser");
    assertThrows(NullPointerException.class, () -> passwordAuthBuilder.build());
  }

  @Test
  public void build_LoginNameNotGiven_ThrowsNullPointerException() {
    final PasswordAuthBuilder passwordAuthBuilder =
        PasswordAuth.builder().hashedPassword("hashedPass");
    assertThrows(NullPointerException.class, () -> passwordAuthBuilder.build());
  }

  @Test
  public void buildHashedPassword_ValidHashedPassword_Builds() {
    final String[] hashedPasswordsToTest =
        {"abc123", "0xdeadbeef", "", "me@example.com", "a", "1", "password"};

    for (final String hashedPassword : hashedPasswordsToTest) {
      final PasswordAuthBuilder builder = PasswordAuth.builder().loginName("TestUser");

      builder.hashedPassword(hashedPassword);

      assertThat(builder.build(), instanceOf(PasswordAuth.class));
      assertThat(builder.build().getHashedPassword(), is(hashedPassword));
    }
  }

  @Test
  public void buildIsPasswordExpired_TrueOrFalse_MatchesBuild() {
    for (final boolean isPasswordNonExpired : TestUtils.BOOLEANS) {
      final PasswordAuthBuilder builder =
          PasswordAuth.builder().loginName("TestUser").hashedPassword("passwordHash");

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

    for (final String loginName : loginNamesToTest) {
      final PasswordAuthBuilder builder = PasswordAuth.builder().hashedPassword("passwordHash");

      builder.loginName(loginName);

      assertThat(builder.build(), instanceOf(PasswordAuth.class));
      assertThat(builder.build().getLoginName(), is(loginName));
    }
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(PasswordAuth.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void toString_ValidData_AsExpected() {
    assertThat(
        PasswordAuth.builder().loginName("TestUser").hashedPassword("hashedPassword").build()
            .toString(),
        is("PasswordAuth(id=null, userId=null, loginName=TestUser, hashedPassword=hashedPassword, isPasswordNonExpired=false)"));
    assertThat(
        PasswordAuth.builder().id(5).userId(95).loginName("TestUser3")
            .hashedPassword("hashedPassword2").build().toString(),
        is("PasswordAuth(id=5, userId=95, loginName=TestUser3, hashedPassword=hashedPassword2, isPasswordNonExpired=false)"));
    assertThat(
        PasswordAuth.builder().id(14).userId(35).loginName("TestUser4")
            .hashedPassword("hashedPassword3").isPasswordNonExpired(true).build().toString(),
        is("PasswordAuth(id=14, userId=35, loginName=TestUser4, hashedPassword=hashedPassword3, isPasswordNonExpired=true)"));
  }

  @Test
  public void withHashedPassword_NullHashedPassword_ThrowsException() {
    assertThrows(NullPointerException.class, () -> PasswordAuth.builder().loginName("Test")
        .hashedPassword("hashedPassword").build().withHashedPassword(null));
  }

  @Test
  public void withHashedPassword_ValidHashedPassword_ChangesHashedPassword() {
    final String originalHashedPassword = "passwordHash";

    final PasswordAuth passwordAuth = PasswordAuth.builder().loginName("abc123hash")
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

    final int originalId = 1;
    final int[] testedIds = {originalId, 0, -1, 1000, -1000, 123456789};

    final PasswordAuth newPasswordAuth = PasswordAuth.builder().id(originalId).loginName(loginName)
        .hashedPassword(hashedPassword).build();

    assertThat(newPasswordAuth.getId(), is(originalId));

    for (final int newId : testedIds) {
      assertThat(newPasswordAuth.withId(newId).getId(), is(newId));
    }
  }

  @Test
  public void withLoginName_NullLoginName_ThrowsException() {
    assertThrows(NullPointerException.class, () -> PasswordAuth.builder().loginName("Test")
        .hashedPassword("hashedPassword").build().withLoginName(null));
  }

  @Test
  public void withLoginName_ValidLoginName_ChangesLoginName() {
    final String originalLoginName = "me@example.com";

    final PasswordAuth passwordAuth =
        PasswordAuth.builder().loginName(originalLoginName).hashedPassword("passwordHash").build();

    final String[] testedLoginNames =
        {originalLoginName, "", "userName", "Long  With     Whitespace", "12345"};

    for (final String newLoginName : testedLoginNames) {
      assertThat(passwordAuth.withLoginName(newLoginName).getLoginName(), is(newLoginName));
    }
  }

  @Test
  public void withPasswordExpired_ValidBoolean_ChangesPasswordExpired() {
    final PasswordAuth testPasswordAuth =
        PasswordAuth.builder().loginName("TestUser").hashedPassword("passwordHash").build();

    final PasswordAuth changedPasswordAuth1 = testPasswordAuth.withPasswordNonExpired(false);
    assertThat(changedPasswordAuth1.isPasswordNonExpired(), is(false));
    final PasswordAuth changedPasswordAuth2 = testPasswordAuth.withPasswordNonExpired(true);
    assertThat(changedPasswordAuth2.isPasswordNonExpired(), is(true));
  }

  @Test
  public void withUser_ValidUser_ChangesUser() {
    final int originalUser = 1;
    final int[] testedUsers = {originalUser, 0, -1, 1000, -1000, 123456789};

    final PasswordAuth newPasswordAuth = PasswordAuth.builder().loginName("TestName")
        .hashedPassword("hashedPassword").userId(originalUser).build();

    assertThat(newPasswordAuth.getUserId(), is(originalUser));

    for (final int newUser : testedUsers) {
      assertThat(newPasswordAuth.withUserId(newUser).getUserId(), is(newUser));
    }
  }
}
