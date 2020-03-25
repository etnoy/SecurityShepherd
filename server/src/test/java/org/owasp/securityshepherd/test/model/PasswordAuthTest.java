package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.persistence.model.PasswordAuth;
import org.owasp.securityshepherd.persistence.model.PasswordAuth.PasswordAuthBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PasswordAuthTest {

  private static final boolean[] BOOLEANS = {false, true};

  //TODO: test null arguments to constructor
  
  @Test
  public void builderToString_ValidData_AsExpected() {

    assertThat(PasswordAuth.builder().toString(), is(
        "PasswordAuth.PasswordAuthBuilder(id=null, user=0, loginName=null, hashedPassword=null, isPasswordNonExpired=false)"));

  }
  
  @Test
  public void PersistenceConstructor_NullLoginName_ThrowsNullPointerException() {

    assertThrows(NullPointerException.class, () -> new PasswordAuth(1, 2, null, "hashedPassword", false));
    
  }
  
  @Test
  public void PersistenceConstructor_NullHashedPasswordName_ThrowsNullPointerException() {

    assertThrows(NullPointerException.class, () -> new PasswordAuth(1, 2, "loginName", null, false));
    
  }

  @Test
  public void buildHashedPassword_NullHashedPassword_ThrowsException() {

    assertThrows(NullPointerException.class, () -> PasswordAuth.builder().hashedPassword(null));

  }

  @Test
  public void buildHashedPassword_ValidHashedPassword_Builds() {

    final String[] hashedPasswordsToTest =
        {"abc123", "0xdeadbeef", "", "me@example.com", "a", "1", "password"};

    for (String hashedPassword : hashedPasswordsToTest) {

      final PasswordAuthBuilder builder = PasswordAuth.builder().loginName("TestUser");

      builder.hashedPassword(hashedPassword);

      assertThat(builder.build(), instanceOf(PasswordAuth.class));
      assertThat(builder.build().getHashedPassword(), is(hashedPassword));

    }

  }


  @Test
  public void buildIsPasswordExpired_TrueOrFalse_MatchesBuild() {

    for (boolean isPasswordNonExpired : BOOLEANS) {

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

    for (String loginName : loginNamesToTest) {

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
        is("PasswordAuth(id=null, user=0, loginName=TestUser, hashedPassword=hashedPassword, isPasswordNonExpired=false)"));

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

    for (String hashedPassword : testedHashedPasswords) {

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

    for (int newId : testedIds) {

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

    for (String newLoginName : testedLoginNames) {

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
        .hashedPassword("hashedPassword").user(originalUser).build();

    assertThat(newPasswordAuth.getUser(), is(originalUser));

    for (int newUser : testedUsers) {

      assertThat(newPasswordAuth.withUser(newUser).getUser(), is(newUser));

    }

  }

}
