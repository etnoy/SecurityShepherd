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
import org.owasp.securityshepherd.authentication.SamlAuth;
import org.owasp.securityshepherd.authentication.SamlAuth.SamlAuthBuilder;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("SamlAuth unit test")
class SamlAuthTest {
  @Test
  void build_SamlIdNotGiven_ThrowsNullPointerException() {
    final SamlAuthBuilder samlAuthBuilder = SamlAuth.builder().userId(3);
    assertThrows(NullPointerException.class, () -> samlAuthBuilder.build());
  }

  @Test
  void build_UserIdNotGiven_ThrowsNullPointerException() {
    final SamlAuthBuilder samlAuthBuilder = SamlAuth.builder().samlId("me@example.com");
    assertThrows(NullPointerException.class, () -> samlAuthBuilder.build());
  }

  @Test
  void builderToString_ValidData_AsExpected() {
    assertThat(SamlAuth.builder().id(15).userId(3).samlId("me@example.com"))
        .hasToString("SamlAuth.SamlAuthBuilder(id=15, userId=3, samlId=me@example.com)");
  }

  @Test
  void buildSamlid_NullSamlId_ThrowsNullPointerException() {
    final SamlAuthBuilder samlAuthBuilder = SamlAuth.builder();
    assertThrows(NullPointerException.class, () -> samlAuthBuilder.samlId(null));
  }

  @Test
  void buildUserId_NullUserId_ThrowsNullPointerException() {
    final SamlAuthBuilder samlAuthBuilder = SamlAuth.builder();
    assertThrows(NullPointerException.class, () -> samlAuthBuilder.userId(null));
  }

  @Test
  void buildUserId_ValidUserId_Builds() {
    final int[] userIdsToTest = {0, 1, -1, 1000, -1000, 123456789};

    final SamlAuthBuilder samlAuthBuilder = SamlAuth.builder().samlId("me@example.com");

    for (final int userId : userIdsToTest) {
      samlAuthBuilder.userId(userId);

      assertThat(samlAuthBuilder.build()).isInstanceOf(SamlAuth.class);
      assertThat(samlAuthBuilder.build().getUserId()).isEqualTo(userId);
    }
  }

  @Test
  void buildSamlId_ValidSamlId_Builds() {
    final String[] samlIdsToTest = {"", "me@example.com", "a", "1"};

    for (final String samlId : samlIdsToTest) {
      final SamlAuthBuilder builder = SamlAuth.builder().userId(3);

      builder.samlId(samlId);

      assertThat(builder.build()).isInstanceOf(SamlAuth.class);
      assertThat(builder.build().getSamlId()).isEqualTo(samlId);
    }
  }

  @Test
  void equals_AutomaticTesting() {
    EqualsVerifier.forClass(SamlAuth.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  void toString_ValidData_AsExpected() {
    assertThat(SamlAuth.builder().id(67).samlId("TestID").userId(3).build())
        .hasToString("SamlAuth(id=67, userId=3, samlId=TestID)");
  }

  @Test
  void withId_ValidId_ChangesId() {
    final int originalId = 1;
    final int[] testedIds = {originalId, 0, -1, 1000, -1000, 123456789};

    final SamlAuth newPasswordAuth =
        SamlAuth.builder().id(originalId).userId(3).samlId("me@example.com").build();

    assertThat(newPasswordAuth.getId()).isEqualTo(originalId);

    for (final int newId : testedIds) {
      assertThat(newPasswordAuth.withId(newId).getId()).isEqualTo(newId);
    }
  }

  @Test
  void withSamlid_NullSamlId_ThrowsNullPointerException() {
    final SamlAuth samlAuth = SamlAuth.builder().userId(1).samlId("me@example.com").build();
    assertThrows(NullPointerException.class, () -> samlAuth.withSamlId(null));
  }

  @Test
  void withSamlId_ValidSamlId_ChangesSamlId() {
    final String originalSamlId = "me@example.com";
    final SamlAuth samlAuth = SamlAuth.builder().userId(3).samlId(originalSamlId).build();

    final String[] testedSamlIds = {
      originalSamlId, "", "banned", "Long  With     Whitespace", "12345"
    };

    for (final String newSamlId : testedSamlIds) {
      final SamlAuth changedAuth = samlAuth.withSamlId(newSamlId);
      assertThat(changedAuth.getSamlId()).isEqualTo(newSamlId);
    }
  }

  @Test
  void withUserId_NullUserId_ThrowsNullPointerException() {
    final SamlAuth samlAuth = SamlAuth.builder().userId(1).samlId("me@example.com").build();
    assertThrows(NullPointerException.class, () -> samlAuth.withUserId(null));
  }

  @Test
  void withUserId_ValidUserId_ChangesUserId() {
    final int originalUserId = 1;
    final int[] userIds = {originalUserId, 0, -1, 1000, -1000, 123456789};

    final SamlAuth samlAuth =
        SamlAuth.builder().userId(originalUserId).samlId("me@example.com").build();

    assertThat(samlAuth.getUserId()).isEqualTo(originalUserId);

    for (final int userId : userIds) {
      assertThat(samlAuth.withUserId(userId).getUserId()).isEqualTo(userId);
    }
  }
}
