package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.owasp.securityshepherd.model.Configuration;
import org.owasp.securityshepherd.repository.ConfigurationRepository;
import org.owasp.securityshepherd.service.ConfigurationService;
import org.owasp.securityshepherd.service.KeyService;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DisplayName("ConfigurationService unit test")
public class ConfigurationServiceTest {

  private ConfigurationService configurationService;

  private ConfigurationRepository configurationRepository =
      Mockito.mock(ConfigurationRepository.class);

  private KeyService keyService = Mockito.mock(KeyService.class);

  @Test
  public void getServerKey_KeyExists_ReturnsExistingKey() throws Exception {

    final String serverKeyConfigurationKey = "serverKey";
    final Configuration mockedConfiguration = mock(Configuration.class);

    final byte[] mockedServerKey =
        {-118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29};

    when(configurationRepository.findByKey(serverKeyConfigurationKey))
        .thenReturn(Mono.just(mockedConfiguration));
    when(mockedConfiguration.getValue())
        .thenReturn(Base64.getEncoder().encodeToString(mockedServerKey));

    StepVerifier.create(configurationService.getServerKey()).assertNext(serverKey -> {

      assertThat(serverKey, is(mockedServerKey));

      verify(configurationRepository).findByKey(serverKeyConfigurationKey);
      verify(keyService, never()).generateRandomBytes(16);
      verify(configurationRepository, never()).save(any(Configuration.class));

    }).expectComplete().verify();

  }

  @Test
  public void getServerKey_NoKeyExists_ReturnsNewKey() throws Exception {

    final String serverKeyConfigurationKey = "serverKey";

    final byte[] mockedServerKey =
        {-118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29};

    when(keyService.generateRandomBytes(16)).thenReturn(Mono.just(mockedServerKey));
    when(configurationRepository.findByKey(serverKeyConfigurationKey)).thenReturn(Mono.empty());
    when(configurationRepository.save(any(Configuration.class)))
        .thenAnswer(configuration -> Mono.just(configuration.getArgument(0, Configuration.class)));

    StepVerifier.create(configurationService.getServerKey()).assertNext(serverKey -> {

      assertThat(serverKey, is(mockedServerKey));

      verify(configurationRepository, atLeast(1)).findByKey(serverKeyConfigurationKey);
      verify(keyService).generateRandomBytes(16);
      verify(configurationRepository).save(any(Configuration.class));

    }).expectComplete().verify();

  }

  @Test
  public void refreshServerKey_KeyDoesNotExist_GeneratesNewKey() throws Exception {

    final String serverKeyConfigurationKey = "serverKey";

    final byte[] newServerKey =
        {-118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29};

    final String encodedNewServerKey = Base64.getEncoder().encodeToString(newServerKey);

    when(configurationRepository.findByKey(serverKeyConfigurationKey)).thenReturn(Mono.empty());

    when(keyService.generateRandomBytes(16)).thenReturn(Mono.just(newServerKey));
    when(configurationRepository.save(any(Configuration.class)))
        .thenAnswer(configuration -> Mono.just(configuration.getArgument(0, Configuration.class)));

    StepVerifier.create(configurationService.refreshServerKey()).assertNext(serverKey -> {

      assertThat(serverKey, is(newServerKey));

      verify(configurationRepository, atLeast(1)).findByKey(serverKeyConfigurationKey);
      verify(keyService).generateRandomBytes(16);

      ArgumentCaptor<Configuration> argument = ArgumentCaptor.forClass(Configuration.class);

      verify(configurationRepository).save(argument.capture());

      assertThat(argument.getValue().getValue(), is(encodedNewServerKey));

    }).expectComplete().verify();

  }

  @Test
  public void refreshServerKey_KeyExists_GeneratesNewKey() throws Exception {

    final String serverKeyConfigurationKey = "serverKey";
    final Configuration mockedConfiguration = mock(Configuration.class);
    final Configuration mockedConfigurationNewKey = mock(Configuration.class);

    final byte[] newServerKey =
        {-118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29};

    final String encodedNewServerKey = Base64.getEncoder().encodeToString(newServerKey);

    when(configurationRepository.findByKey(serverKeyConfigurationKey))
        .thenReturn(Mono.just(mockedConfiguration));

    when(mockedConfiguration.withValue(encodedNewServerKey)).thenReturn(mockedConfigurationNewKey);
    when(mockedConfigurationNewKey.getValue()).thenReturn(encodedNewServerKey);

    when(keyService.generateRandomBytes(16)).thenReturn(Mono.just(newServerKey));
    when(configurationRepository.save(any(Configuration.class)))
        .thenAnswer(configuration -> Mono.just(configuration.getArgument(0, Configuration.class)));

    StepVerifier.create(configurationService.refreshServerKey()).assertNext(serverKey -> {

      assertThat(serverKey, is(newServerKey));

      verify(configurationRepository, atLeast(1)).findByKey(serverKeyConfigurationKey);
      verify(keyService).generateRandomBytes(16);

      verify(mockedConfiguration).withValue(encodedNewServerKey);

      ArgumentCaptor<Configuration> argument = ArgumentCaptor.forClass(Configuration.class);

      verify(configurationRepository).save(argument.capture());

      assertThat(argument.getValue(), is(mockedConfigurationNewKey));
      assertThat(argument.getValue().getValue(), is(encodedNewServerKey));

    }).expectComplete().verify();

  }

  @BeforeEach
  private void setUp() {
    // Print more verbose errors if something goes wrong
    Hooks.onOperatorDebug();

    // Set up configurationService to use our mocked repos and services
    configurationService = new ConfigurationService(configurationRepository, keyService);

  }

}
