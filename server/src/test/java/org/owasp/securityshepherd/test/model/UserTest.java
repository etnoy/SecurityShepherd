/**
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.test.util.TestUtils;
import org.owasp.securityshepherd.user.User;
import org.owasp.securityshepherd.user.User.UserBuilder;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("User unit test")
public class UserTest {
  @Test
  public void build_DisplayNameNotGiven_ThrowsNullPointerException() {
    final UserBuilder userBuilder = User.builder();
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> userBuilder.build());
    assertThat(thrownException.getMessage(), is("displayName is marked non-null but is null"));
  }

  @Test
  public void buildAccountCreated_ValidTime_BuildsUser() {
    final UserBuilder userBuilder = User.builder().displayName("TestUser");

    for (final LocalDateTime accountCreated : TestUtils.LOCALDATETIMES_WITH_NULL) {
      final User user = userBuilder.accountCreated(accountCreated).build();
      assertThat(user.getAccountCreated(), is(accountCreated));
    }
  }

  @Test
  public void buildClassId_ValidClassId_BuildsUser() {
    final UserBuilder userBuilder = User.builder().classId(1L).displayName("TestUser");

    for (final Long classId : TestUtils.LONGS_WITH_NULL) {
      final User user = userBuilder.classId(classId).build();
      assertThat(user.getClassId(), is(classId));
    }
  }

  @Test
  public void buildDisplayName_NullDisplayName_ThrowsNullPointerException() {
    final UserBuilder userBuilder = User.builder();
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> userBuilder.displayName(null));
    assertThat(thrownException.getMessage(), is("displayName is marked non-null but is null"));
  }

  @Test
  public void buildDisplayName_ValidDisplayName_BuildsUser() {
    final UserBuilder userBuilder = User.builder();
    for (final String displayName : TestUtils.STRINGS) {
      final User user = userBuilder.displayName(displayName).build();
      assertThat(user.getDisplayName(), is(displayName));
    }
  }

  @Test
  public void buildId_ValidId_BuildsUser() {
    final UserBuilder userBuilder = User.builder().id(12345L).displayName("TestUser");
    for (final Long id : TestUtils.LONGS_WITH_NULL) {
      final User user = userBuilder.id(id).build();
      assertThat(user.getId(), is(id));
    }
  }

  @Test
  public void buildIsNotBanned_ValidBoolean_BuildsUser() {
    final UserBuilder userBuilder = User.builder().displayName("TestUser");
    for (final boolean isNotBanned : TestUtils.BOOLEANS) {
      final User user = userBuilder.isNotBanned(isNotBanned).build();
      assertThat(user.isNotBanned(), is(isNotBanned));
    }
  }

  @Test
  public void equals_EqualsVerifier_AsExpected() {
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
    final User user = User.builder().displayName("TestUser")
        .accountCreated(TestUtils.INITIAL_LOCALDATETIME).build();

    for (final LocalDateTime accountCreated : TestUtils.LOCALDATETIMES_WITH_NULL) {
      final User withUser = user.withAccountCreated(accountCreated);
      assertThat(withUser.getAccountCreated(), is(accountCreated));
    }
  }

  @Test
  public void withClassId_ValidClassId_ChangesClassId() {
    final User user =
        User.builder().displayName("TestUser").classId(TestUtils.INITIAL_LONG).build();

    for (final Long classId : TestUtils.LONGS_WITH_NULL) {
      final User withUser = user.withClassId(classId);
      assertThat(withUser.getClassId(), is(classId));
    }
  }

  @Test
  public void withDisplayName_NullDisplayName_ThrowsNullPointerException() {
    final User user = User.builder().displayName("TestUser").build();
    final Exception thrownException =
        assertThrows(NullPointerException.class, () -> user.withDisplayName(null));
    assertThat(thrownException.getMessage(), is("displayName is marked non-null but is null"));
  }

  @Test
  public void withDisplayName_ValidDisplayName_ChangesDisplayName() {
    final User user = User.builder().displayName(TestUtils.INITIAL_STRING).build();

    for (final String displayName : TestUtils.STRINGS) {
      final User withUser = user.withDisplayName(displayName);
      assertThat(withUser.getDisplayName(), is(displayName));
    }
  }

  @Test
  public void withEmail_ValidEmail_ChangesEmail() {
    final User user =
        User.builder().displayName("TestUser").email(TestUtils.INITIAL_STRING).build();

    for (final String email : TestUtils.STRINGS) {
      final User withUser = user.withEmail(email);
      assertThat(withUser.getEmail(), is(email));
    }
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final User user = User.builder().id(TestUtils.INITIAL_LONG).displayName("Test User").build();

    for (final Long id : TestUtils.LONGS_WITH_NULL) {
      final User withUser = user.withId(id);
      assertThat(withUser.getId(), is(id));
    }
  }

  @Test
  public void withKey_ValidKey_ChangesKey() {
    final User user =
        User.builder().displayName("TestUser").key(TestUtils.INITIAL_BYTE_ARRAY).build();

    for (byte[] key : TestUtils.BYTE_ARRAYS_WITH_NULL) {
      final User withUser = user.withKey(key);
      assertThat(withUser.getKey(), is(key));
    }
  }

  @Test
  public void withNotBanned_ValidBoolean_ChangesIsAdmin() {
    final User user =
        User.builder().isNotBanned(TestUtils.INITIAL_BOOLEAN).displayName("TestUser").build();

    for (final boolean isNotBanned : TestUtils.BOOLEANS) {
      final User withUser = user.withNotBanned(isNotBanned);
      assertThat(withUser.isNotBanned(), is(isNotBanned));
    }
  }
}
