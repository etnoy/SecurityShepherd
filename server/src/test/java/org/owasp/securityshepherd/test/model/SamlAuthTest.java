package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.securityshepherd.model.SamlAuth;
import org.owasp.securityshepherd.model.SamlAuth.SamlAuthBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("SamlAuth unit test")
public class SamlAuthTest {
  @Test
  public void build_SamlIdNotGiven_ThrowsException() {
    assertThrows(NullPointerException.class, () -> SamlAuth.builder().userId(3).build());
  }

  @Test
  public void build_UserIdNotGiven_ThrowsException() {
    assertThrows(NullPointerException.class,
        () -> SamlAuth.builder().samlId("me@example.com").build());
  }

  @Test
  public void builderToString_ValidData_AsExpected() {
    assertThat(SamlAuth.builder().id(15).userId(3).samlId("me@example.com").toString(),
        is("SamlAuth.SamlAuthBuilder(id=15, userId=3, samlId=me@example.com)"));
  }

  @Test
  public void buildSamlid_NullSamlId_ThrowsException() {
    assertThrows(NullPointerException.class, () -> SamlAuth.builder().samlId(null));
  }

  @Test
  public void buildUserId_NullUserId_ThrowsNullPointerException() {
    assertThrows(NullPointerException.class, () -> SamlAuth.builder().userId(null).build());
  }

  @Test
  public void buildUserId_ValidUserId_Builds() {
    final int[] userIdsToTest = {0, 1, -1, 1000, -1000, 123456789};

    for (final int userId : userIdsToTest) {
      final SamlAuthBuilder builder = SamlAuth.builder().samlId("me@example.com");

      builder.userId(userId);

      assertThat(builder.build(), instanceOf(SamlAuth.class));
      assertThat(builder.build().getUserId(), is(userId));
    }
  }

  @Test
  public void buildSamlId_ValidSamlId_Builds() {
    final String[] samlIdsToTest = {"", "me@example.com", "a", "1"};

    for (final String samlId : samlIdsToTest) {
      final SamlAuthBuilder builder = SamlAuth.builder().userId(3);

      builder.samlId(samlId);

      assertThat(builder.build(), instanceOf(SamlAuth.class));
      assertThat(builder.build().getSamlId(), is(samlId));
    }
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(SamlAuth.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void toString_ValidData_AsExpected() {
    assertThat(SamlAuth.builder().id(67).samlId("TestID").userId(3).build().toString(),
        is("SamlAuth(id=67, userId=3, samlId=TestID)"));
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final int originalId = 1;
    final int[] testedIds = {originalId, 0, -1, 1000, -1000, 123456789};

    final SamlAuth newPasswordAuth =
        SamlAuth.builder().id(originalId).userId(3).samlId("me@example.com").build();

    assertThat(newPasswordAuth.getId(), is(originalId));

    for (final int newId : testedIds) {
      assertThat(newPasswordAuth.withId(newId).getId(), is(newId));
    }
  }

  @Test
  public void withSamlid_NullSamlId_ThrowsException() {
    final SamlAuth samlAuth = SamlAuth.builder().userId(1).samlId("me@example.com").build();
    assertThrows(NullPointerException.class, () -> samlAuth.withSamlId(null));
  }

  @Test
  public void withSamlId_ValidSamlId_ChangesSamlId() {
    final String originalSamlId = "me@example.com";
    final SamlAuth samlAuth = SamlAuth.builder().userId(3).samlId(originalSamlId).build();

    final String[] testedSamlIds =
        {originalSamlId, "", "banned", "Long  With     Whitespace", "12345"};

    for (final String newSamlId : testedSamlIds) {
      final SamlAuth changedAuth = samlAuth.withSamlId(newSamlId);
      assertThat(changedAuth.getSamlId(), is(newSamlId));
    }
  }

  @Test
  public void withUserId_NullUserId_ThrowsException() {
    final SamlAuth samlAuth = SamlAuth.builder().userId(1).samlId("me@example.com").build();
    assertThrows(NullPointerException.class, () -> samlAuth.withUserId(null));
  }

  @Test
  public void withUserId_ValidUserId_ChangesUserId() {
    final int originalUserId = 1;
    final int[] userIds = {originalUserId, 0, -1, 1000, -1000, 123456789};

    final SamlAuth samlAuth =
        SamlAuth.builder().userId(originalUserId).samlId("me@example.com").build();

    assertThat(samlAuth.getUserId(), is(originalUserId));

    for (final int userId : userIds) {
      assertThat(samlAuth.withUserId(userId).getUserId(), is(userId));
    }
  }
}