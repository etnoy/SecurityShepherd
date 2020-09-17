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
import org.owasp.securityshepherd.model.Configuration;
import org.owasp.securityshepherd.model.Configuration.ConfigurationBuilder;
import org.owasp.securityshepherd.test.util.TestUtils;

import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("Configuration unit test")
class ConfigurationTest {
  @Test
  void builderToString_ValidData_AsExpected() {
    assertThat(Configuration.builder())
        .hasToString("Configuration.ConfigurationBuilder(id=null, key=null, value=null)");
  }

  @Test
  void build_KeyNotGiven_ThrowsNullPointerException() {
    final ConfigurationBuilder configurationBuilder = Configuration.builder().id(17).value("value");
    assertThrows(NullPointerException.class, () -> configurationBuilder.build());
  }

  @Test
  void build_ValueNotGiven_ThrowsNullPointerException() {
    final ConfigurationBuilder configurationBuilder = Configuration.builder().id(17).key("key");
    assertThrows(NullPointerException.class, () -> configurationBuilder.build());
  }

  @Test
  void buildId_ValidId_Builds() {
    final int[] idsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (int id : idsToTest) {
      final ConfigurationBuilder builder =
          Configuration.builder().key("testKey").value("testValue");

      builder.id(id);

      assertThat(builder.build()).isInstanceOf(Configuration.class);
      assertThat(builder.build().getId()).isEqualTo(id);
    }
  }

  @Test
  void buildKey_NullKey_ThrowsNullPointerException() {
    final ConfigurationBuilder configurationBuilder = Configuration.builder();
    assertThrows(NullPointerException.class, () -> configurationBuilder.key(null));
  }

  @Test
  void buildKey_ValidKey_Builds() {
    final String[] keysToTest = {
      "", "serverKey", "timestampSetting", "\"", "$$^¨'", "åäö", "a", "1", "userName"
    };

    for (String key : keysToTest) {
      final ConfigurationBuilder builder = Configuration.builder().value("TestValue");

      builder.key(key);

      assertThat(builder.build()).isInstanceOf(Configuration.class);
      assertThat(builder.build().getKey()).isEqualTo(key);
    }
  }

  @Test
  void buildValue_NullValue_ThrowsNullPointerException() {
    final ConfigurationBuilder configurationBuilder = Configuration.builder();
    assertThrows(NullPointerException.class, () -> configurationBuilder.value(null));
  }

  @Test
  void buildValue_ValidValue_Builds() {
    final ConfigurationBuilder configurationBuilder = Configuration.builder().key("TestKey");

    for (String value : TestUtils.STRINGS) {
      configurationBuilder.value(value);
      assertThat(configurationBuilder.build()).isInstanceOf(Configuration.class);
      assertThat(configurationBuilder.build().getValue()).isEqualTo(value);
    }
  }

  @Test
  void equals_AutomaticTesting() {
    EqualsVerifier.forClass(Configuration.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  void toString_ValidData_AsExpected() {
    assertThat(Configuration.builder().key("serverKey").value("abc123secret").build())
        .hasToString("Configuration(id=null, key=serverKey, value=abc123secret)");
  }

  @Test
  void withId_ValidId_ChangesId() {
    final Integer originalId = 1;

    // TODO: create new list in TestUtils
    final Integer[] testedIds = {originalId, 0, null, -1, 1000, -1000, 123456789, -12346789};

    final Configuration configuration =
        Configuration.builder().key("settingKey").value("settingvalue").build();

    for (Integer id : testedIds) {
      final Configuration newConfiguration = configuration.withId(id);
      assertThat(newConfiguration.getId()).isEqualTo(id);
    }
  }

  @Test
  void withKey_NullKey_ThrowsNullPointerException() {
    final Configuration configuration =
        Configuration.builder().key("serverKey").value("secret123").build();

    assertThrows(NullPointerException.class, () -> configuration.withKey(null));
  }

  @Test
  void withKey_ValidKey_ChangesKey() {
    final Configuration configuration =
        Configuration.builder().key("settingKey").value("123").build();

    for (String key : TestUtils.STRINGS) {
      final Configuration changedConfiguration = configuration.withKey(key);
      assertThat(changedConfiguration.getKey()).hasToString(key);
    }
  }

  @Test
  void withValue_NullValue_ThrowsNullPointerException() {
    final Configuration configuration =
        Configuration.builder().key("serverKey").value("secret123").build();
    assertThrows(NullPointerException.class, () -> configuration.withValue(null));
  }

  @Test
  void withValue_ValidValue_ChangesValue() {
    final Configuration configuration =
        Configuration.builder().key("settingKey").value("settingValue").build();

    final String[] testedValues = {
      "settingValue", "", "\"", "!\"+,-", "serverValue", "Long  With     Whitespace", "12345"
    };

    for (String value : testedValues) {
      final Configuration changedConfiguration = configuration.withValue(value);
      assertThat(changedConfiguration.getValue()).isEqualTo(value);
    }
  }
}
