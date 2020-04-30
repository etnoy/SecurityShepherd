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

package org.owasp.securityshepherd.service;

import java.util.Base64;

import org.owasp.securityshepherd.exception.ConfigurationKeyNotFoundException;
import org.owasp.securityshepherd.model.Configuration;
import org.owasp.securityshepherd.repository.ConfigurationRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public final class ConfigurationService {

  private final ConfigurationRepository configurationRepository;

  private final KeyService keyService;

  private Mono<Configuration> create(final String key, final String value) {
    log.debug("Creating configuration key " + key + " with value " + value);
    return configurationRepository.save(Configuration.builder().key(key).value(value).build());
  }

  private Mono<Boolean> existsByKey(final String key) {
    return configurationRepository.findByKey(key).map(u -> true).defaultIfEmpty(false);
  }

  private Mono<String> getByKey(final String key) {
    return configurationRepository.findByKey(key)
        .switchIfEmpty(Mono.error(
            new ConfigurationKeyNotFoundException("Configuration key " + key + " not found")))
        .map(Configuration::getValue);
  }

  public Mono<byte[]> getServerKey() {
    return getByKey("serverKey").map(Base64.getDecoder()::decode)
        .onErrorResume(ConfigurationKeyNotFoundException.class, notFound -> refreshServerKey());
  }

  public Mono<byte[]> refreshServerKey() {
    final String serverKeyConfigurationKey = "serverKey";
    log.info("Refreshing server key");
    final String newServerKey =
        Base64.getEncoder().encodeToString(keyService.generateRandomBytes(16));
    return existsByKey(serverKeyConfigurationKey).flatMap(exists -> {
      if (Boolean.TRUE.equals(exists)) {
        return setValue(serverKeyConfigurationKey, newServerKey);
      } else {
        return create(serverKeyConfigurationKey, newServerKey);
      }
    }).map(Configuration::getValue).map(Base64.getDecoder()::decode);
  }

  private Mono<Configuration> setValue(final String key, final String value) {
    log.debug("Setting configuration key " + key + " to value " + value);
    return Mono.just(key).filterWhen(this::existsByKey)
        .switchIfEmpty(Mono.error(
            new ConfigurationKeyNotFoundException("Configuration key " + key + " not found")))
        .flatMap(configurationRepository::findByKey)
        .flatMap(configuration -> configurationRepository.save(configuration.withValue(value)));
  }
}
