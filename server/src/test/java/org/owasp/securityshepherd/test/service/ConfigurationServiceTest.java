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
package org.owasp.securityshepherd.test.service;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.configuration.ConfigurationRepository;
import org.owasp.securityshepherd.crypto.KeyService;
import org.owasp.securityshepherd.model.Configuration;
import org.owasp.securityshepherd.service.ConfigurationService;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigurationService unit test")
class ConfigurationServiceTest {

  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  private ConfigurationService configurationService;

  @Mock private ConfigurationRepository configurationRepository;

  @Mock private KeyService keyService;

  @Test
  void getServerKey_KeyExists_ReturnsExistingKey() throws Exception {
    final String serverKeyConfigurationKey = "serverKey";
    final Configuration mockedConfiguration = mock(Configuration.class);

    final byte[] mockedServerKey = {
      -118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29
    };

    when(configurationRepository.findByKey(serverKeyConfigurationKey))
        .thenReturn(Mono.just(mockedConfiguration));
    when(mockedConfiguration.getValue())
        .thenReturn(Base64.getEncoder().encodeToString(mockedServerKey));

    StepVerifier.create(configurationService.getServerKey())
        .assertNext(
            serverKey -> {
              assertThat(serverKey).isEqualTo(mockedServerKey);
              verify(configurationRepository).findByKey(serverKeyConfigurationKey);
              verify(keyService, never()).generateRandomBytes(16);
              verify(configurationRepository, never()).save(any(Configuration.class));
            })
        .expectComplete()
        .verify();
  }

  @Test
  void getServerKey_NoKeyExists_ReturnsNewKey() throws Exception {
    final String serverKeyConfigurationKey = "serverKey";
    final byte[] mockedServerKey = {
      -118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29
    };

    when(keyService.generateRandomBytes(16)).thenReturn(mockedServerKey);
    when(configurationRepository.findByKey(serverKeyConfigurationKey)).thenReturn(Mono.empty());
    when(configurationRepository.save(any(Configuration.class)))
        .thenAnswer(configuration -> Mono.just(configuration.getArgument(0, Configuration.class)));

    StepVerifier.create(configurationService.getServerKey())
        .assertNext(
            serverKey -> {
              assertThat(serverKey).isEqualTo(mockedServerKey);

              verify(configurationRepository, atLeast(1)).findByKey(serverKeyConfigurationKey);
              verify(keyService).generateRandomBytes(16);
              verify(configurationRepository).save(any(Configuration.class));
            })
        .expectComplete()
        .verify();
  }

  @Test
  void refreshServerKey_KeyDoesNotExist_GeneratesNewKey() throws Exception {
    final String serverKeyConfigurationKey = "serverKey";
    final byte[] newServerKey = {
      -118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29
    };
    final String encodedNewServerKey = Base64.getEncoder().encodeToString(newServerKey);

    when(configurationRepository.findByKey(serverKeyConfigurationKey)).thenReturn(Mono.empty());
    when(keyService.generateRandomBytes(16)).thenReturn(newServerKey);
    when(configurationRepository.save(any(Configuration.class)))
        .thenAnswer(configuration -> Mono.just(configuration.getArgument(0, Configuration.class)));

    StepVerifier.create(configurationService.refreshServerKey())
        .assertNext(
            serverKey -> {
              assertThat(serverKey).isEqualTo(newServerKey);

              verify(configurationRepository, atLeast(1)).findByKey(serverKeyConfigurationKey);
              verify(keyService).generateRandomBytes(16);

              ArgumentCaptor<Configuration> argument = ArgumentCaptor.forClass(Configuration.class);

              verify(configurationRepository).save(argument.capture());

              assertThat(argument.getValue().getValue()).isEqualTo(encodedNewServerKey);
            })
        .expectComplete()
        .verify();
  }

  @Test
  void refreshServerKey_KeyExists_GeneratesNewKey() throws Exception {
    final String serverKeyConfigurationKey = "serverKey";
    final Configuration mockedConfiguration = mock(Configuration.class);
    final Configuration mockedConfigurationNewKey = mock(Configuration.class);

    final byte[] newServerKey = {
      -118, 9, -7, -35, 17, -116, -94, 0, -32, -117, 65, -127, 12, 82, 9, 29
    };

    final String encodedNewServerKey = Base64.getEncoder().encodeToString(newServerKey);

    when(configurationRepository.findByKey(serverKeyConfigurationKey))
        .thenReturn(Mono.just(mockedConfiguration));

    when(mockedConfiguration.withValue(encodedNewServerKey)).thenReturn(mockedConfigurationNewKey);
    when(mockedConfigurationNewKey.getValue()).thenReturn(encodedNewServerKey);

    when(keyService.generateRandomBytes(16)).thenReturn(newServerKey);
    when(configurationRepository.save(any(Configuration.class)))
        .thenAnswer(configuration -> Mono.just(configuration.getArgument(0, Configuration.class)));

    StepVerifier.create(configurationService.refreshServerKey())
        .assertNext(
            serverKey -> {
              assertThat(serverKey).isEqualTo(newServerKey);
              verify(configurationRepository, atLeast(1)).findByKey(serverKeyConfigurationKey);
              verify(keyService).generateRandomBytes(16);

              verify(mockedConfiguration).withValue(encodedNewServerKey);

              ArgumentCaptor<Configuration> argument = ArgumentCaptor.forClass(Configuration.class);

              verify(configurationRepository).save(argument.capture());

              assertThat(argument.getValue()).isEqualTo(mockedConfigurationNewKey);
              assertThat(argument.getValue().getValue()).isEqualTo(encodedNewServerKey);
            })
        .expectComplete()
        .verify();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    configurationService = new ConfigurationService(configurationRepository, keyService);
  }
}
