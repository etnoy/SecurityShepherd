package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.model.User;
import org.owasp.securityshepherd.model.User.UserBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("User unit test")
public class UserTest {

  @Test
  public void build_DisplayNameNotGiven_ThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> User.builder().build());
  }

  @Test
  public void buildAccountCreated_ValidTime_Builds() {
    final int[] timesToTest = {0, 1, 2, 1000, 4000, 1581806000, 42};

    final UserBuilder userBuilder = User.builder().displayName("TestUser");

    for (final int accountCreated : timesToTest) {
      final LocalDateTime time =
          LocalDateTime.ofInstant(Instant.ofEpochMilli(accountCreated), ZoneId.systemDefault());

      userBuilder.accountCreated(time);
      assertThat(userBuilder.build(), instanceOf(User.class));
      assertThat(userBuilder.build().getAccountCreated(), is(time));
    }
  }

  @Test
  public void buildClassId_ValidClassId_Builds() {
    final UserBuilder userBuilder = User.builder().displayName("TestUser");
    userBuilder.classId(1L);
    final User user = userBuilder.build();
    assertThat(user, is(instanceOf(User.class)));
    assertThat(user.getClassId(), is(1L));
  }

  @Test
  public void buildDisplayName_NullDisplayName_ThrowsNullPointerException() {
    final UserBuilder userBuilder = User.builder();
    assertThrows(NullPointerException.class, () -> userBuilder.displayName(null));
  }

  @Test
  public void buildDisplayName_ValidDisplayName_ReturnsUser() {
    final String validDisplayName = "build_ValidDisplayName";
    final User build_ValidDisplayNameLengthUser =
        User.builder().displayName(validDisplayName).build();
    assertThat(build_ValidDisplayNameLengthUser, instanceOf(User.class));
    assertThat(build_ValidDisplayNameLengthUser.getDisplayName(), is(validDisplayName));
  }

  @Test
  public void buildUserId_ValidModuleId_Builds() {
    final UserBuilder userBuilder = User.builder().id(12345L).displayName("TestUser");

    for (final long id : TestUtils.LONGS) {
      userBuilder.id(id);

      final User user = userBuilder.build();
      assertThat(user, instanceOf(User.class));
      assertThat(user.getId(), is(id));
    }
  }


  @Test
  public void buildIsNotBannedAdmin_TrueOrFalse_MatchesBuild() {
    final UserBuilder userBuilder = User.builder().displayName("TestUser");

    for (final boolean isNotBanned : TestUtils.BOOLEANS) {
      userBuilder.isNotBanned(isNotBanned);

      assertThat(userBuilder.build(), instanceOf(User.class));
      assertThat(userBuilder.build().isNotBanned(), is(isNotBanned));
    }
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(User.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void toString_ValidData_AsExpected() {
    final User testUser = User.builder().displayName("TestUser").build();

    assertThat(testUser.toString(),
        is("User(id=null, displayName=TestUser, classId=null, email=null, "
            + "isNotBanned=false, accountCreated=null, key=null)"));
  }

  @Test
  public void userBuilderToString_ValidData_AsExpected() {
    final UserBuilder builder = User.builder();

    assertThat(builder.toString(), is("User.UserBuilder(id=null, displayName=null, classId=null, "
        + "email=null, isNotBanned=false, accountCreated=null, key=null)"));
  }

  @Test
  public void withAccountCreated_ValidTime_ChangesAccountCreationTime() {
    final long originalTime = 0L;

    final List<Long> timesToTest =
        Arrays.asList(originalTime, 1L, 2L, 1000L, 5000L, 9000990909L, 12398234987345983L);

    final List<LocalDateTime> dateTimesToTest = timesToTest.stream()
        .map(epoch -> LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault()))
        .collect(Collectors.toCollection(ArrayList::new));

    final User user =
        User.builder().displayName("TestUser").accountCreated(dateTimesToTest.get(0)).build();

    for (final LocalDateTime time : dateTimesToTest) {
      final User changedUser = user.withAccountCreated(time);
      assertThat(changedUser, instanceOf(User.class));
      assertThat(changedUser.getAccountCreated(), is(time));
    }
  }

  @Test
  public void withClassId_ValidClassId_ChangesClassId() {
    final String displayName = "withClassId_ValidClassId";
    final Long originalClassId = 17L;
    final Long[] testedClassIds = {originalClassId, 0L, 1L, null, -1L, 1000L, -1000L, 123456789L};
    final User newUser = User.builder().displayName(displayName).classId(originalClassId).build();

    for (Long classId : testedClassIds) {
      final User changedUser = newUser.withClassId(classId);
      assertThat(changedUser, is(instanceOf(User.class)));
      assertThat(changedUser.getClassId(), is(classId));
    }
  }

  @Test
  public void withDisplayName_NullDisplayName_ThrowsNullPointerException() {
    final User user = User.builder().displayName("TestUser").build();
    assertThrows(NullPointerException.class, () -> user.withDisplayName(null));
  }

  @Test
  public void withDisplayName_ValidDisplayName_ChangesDisplayName() {
    final String displayName = "withDisplayName_ValidDisplayName";

    final User newUser = User.builder().displayName(displayName).build();

    assertThat(newUser.getDisplayName(), is(displayName));

    final String[] testedDisplayNames =
        {displayName, "", "newUser", "Long  With     Whitespace", "12345"};

    User changedUser;
    for (String newDisplayName : testedDisplayNames) {
      changedUser = newUser.withDisplayName(newDisplayName);
      assertThat(changedUser.getDisplayName(), is(newDisplayName));
    }
  }

  @Test
  public void withEmail_ValidEmail_ChangesEmail() {
    final String displayName = "withEmail_ValidEmail";

    final String originalEmail = "validEmail@example.com";

    final String[] testedStrings =
        {originalEmail, null, "", "newEmail@example.com", "e@e", "a", "alongemail@example.com"};

    final User newUser = User.builder().displayName(displayName).email(originalEmail).build();

    assertThat(newUser.getEmail(), is(originalEmail));

    User changedUser;

    for (String newEmail : testedStrings) {
      changedUser = newUser.withEmail(newEmail);
      assertThat(changedUser.getEmail(), is(newEmail));
    }
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final Long originalId = 1L;
    final Long[] testedIds = {originalId, null, 0L, -1L, 1000L, -1000L, 123456789L};

    final User newUser = User.builder().id(originalId).displayName("Test User").build();

    for (Long newId : testedIds) {
      final User changedUser = newUser.withId(newId);
      assertThat(changedUser.getId(), is(newId));
    }
  }

  @Test
  public void withKey_ValidKey_ChangesKey() {
    final String displayName = "withKey_ValidKey";
    final byte[] key = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    final byte[][] testedKeys = {key, null, {}, {1}, {19, 26, 127, -128}};
    final User newUser = User.builder().displayName(displayName).key(key).build();

    assertThat(newUser.getKey(), is(key));

    for (byte[] newKey : testedKeys) {
      final User changedUser = newUser.withKey(newKey);
      assertThat(changedUser.getKey(), is(newKey));
    }
  }

  @Test
  public void withNotBanned_ValidBoolean_ChangesIsAdmin() {
    final User testUser = User.builder().displayName("TestUser").build();

    for (final boolean isNotBanned : TestUtils.BOOLEANS) {
      final User changedUser = testUser.withNotBanned(isNotBanned);
      assertThat(changedUser, instanceOf(User.class));
      assertThat(changedUser.isNotBanned(), is(isNotBanned));
    }
  }
}
