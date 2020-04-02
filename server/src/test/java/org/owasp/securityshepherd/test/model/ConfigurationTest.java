package org.owasp.securityshepherd.test.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.owasp.securityshepherd.model.Configuration;
import org.owasp.securityshepherd.model.Configuration.ConfigurationBuilder;
import lombok.NonNull;
import nl.jqno.equalsverifier.EqualsVerifier;

@DisplayName("Configuration unit test")
public class ConfigurationTest {
  @Test
  public void builderToString_ValidData_AsExpected() {
    assertThat(Configuration.builder().toString(),
        is("Configuration.ConfigurationBuilder(id=null, key=null, value=null)"));
  }

  @Test
  public void build_KeyNotGiven_ThrowsNullPointerException() {
    final ConfigurationBuilder configurationBuilder = Configuration.builder().id(17).value("value");
    assertThrows(NullPointerException.class, () -> configurationBuilder.build());
  }

  @Test
  public void build_ValueNotGiven_ThrowsNullPointerException() {
    final ConfigurationBuilder configurationBuilder = Configuration.builder().id(17).key("key");
    assertThrows(NullPointerException.class, () -> configurationBuilder.build());
  }

  @Test
  public void buildId_ValidId_Builds() {
    final int[] idsToTest = {0, 1, -1, 1000, -1000, 1234567, -1234567, 42};

    for (int id : idsToTest) {
      final ConfigurationBuilder builder =
          Configuration.builder().key("testKey").value("testValue");

      builder.id(id);

      assertThat(builder.build(), instanceOf(Configuration.class));
      assertThat(builder.build().getId(), is(id));
    }
  }

  @Test
  public void buildKey_NullKey_ThrowsException() {
    assertThrows(NullPointerException.class, () -> Configuration.builder().key(null));
  }

  @Test
  public void buildKey_ValidKey_Builds() {
    final String[] keysToTest =
        {"", "serverKey", "timestampSetting", "\"", "$$^¨'", "åäö", "a", "1", "userName"};

    for (String key : keysToTest) {
      final ConfigurationBuilder builder = Configuration.builder().value("TestValue");

      builder.key(key);

      assertThat(builder.build(), instanceOf(Configuration.class));
      assertThat(builder.build().getKey(), is(key));
    }
  }

  @Test
  public void buildValue_NullValue_ThrowsException() {
    assertThrows(NullPointerException.class, () -> Configuration.builder().value(null));
  }

  @Test
  public void buildValue_ValidValue_Builds() {
    final String[] valuesToTest =
        {"", "serverValue", "timestampSetting", "\"", "$$^¨'", "åäö", "a", "1", "userName"};

    for (String value : valuesToTest) {

      final ConfigurationBuilder builder = Configuration.builder().key("TestKey");

      builder.value(value);

      assertThat(builder.build(), instanceOf(Configuration.class));
      assertThat(builder.build().getValue(), is(value));
    }
  }

  @Test
  public void equals_AutomaticTesting() {
    EqualsVerifier.forClass(Configuration.class).withIgnoredAnnotations(NonNull.class).verify();
  }

  @Test
  public void toString_ValidData_AsExpected() {
    assertThat(Configuration.builder().key("serverKey").value("abc123secret").build().toString(),
        is("Configuration(id=null, key=serverKey, value=abc123secret)"));
  }

  @Test
  public void withId_ValidId_ChangesId() {
    final Integer originalId = 1;
    final Integer[] testedIds = {originalId, 0, null, -1, 1000, -1000, 123456789, -12346789};

    final Configuration configuration =
        Configuration.builder().key("settingKey").value("settingvalue").build();

    for (Integer id : testedIds) {
      final Configuration newConfiguration = configuration.withId(id);
      assertThat(newConfiguration.getId(), is(id));
    }
  }

  @Test
  public void withKey_NullKey_ThrowsException() {
    assertThrows(NullPointerException.class,
        () -> Configuration.builder().key("serverKey").value("secret123").build().withKey(null));
  }

  @Test
  public void withKey_ValidKey_ChangesKey() {
    final Configuration configuration =
        Configuration.builder().key("settingKey").value("123").build();

    final String[] testedKeys =
        {"settingKey", "", "\"", "!\"+,-", "serverKey", "Long  With     Whitespace", "12345"};

    for (String key : testedKeys) {

      final Configuration changedConfiguration = configuration.withKey(key);
      assertThat(changedConfiguration.getKey(), is(key));
    }
  }

  @Test
  public void withValue_NullValue_ThrowsException() {
    assertThrows(NullPointerException.class,
        () -> Configuration.builder().key("serverKey").value("secret123").build().withValue(null));
  }

  @Test
  public void withValue_ValidValue_ChangesValue() {
    final Configuration configuration =
        Configuration.builder().key("settingKey").value("settingValue").build();

    final String[] testedValues =
        {"settingValue", "", "\"", "!\"+,-", "serverValue", "Long  With     Whitespace", "12345"};

    for (String value : testedValues) {
      final Configuration changedConfiguration = configuration.withValue(value);
      assertThat(changedConfiguration.getValue(), is(value));
    }
  }
}
