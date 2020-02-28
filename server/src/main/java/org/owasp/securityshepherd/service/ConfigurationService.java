package org.owasp.securityshepherd.service;

import java.util.Base64;

import org.owasp.securityshepherd.exception.ConfigurationKeyNotFoundException;
import org.owasp.securityshepherd.persistence.model.Configuration;
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

		return Mono.just(Configuration.builder().key(key).value(value).build()).flatMap(configurationRepository::save);

	}

	private Mono<Configuration> setValue(final String key, final String value) {

		return configurationRepository.findByKey(key)
				.switchIfEmpty(
						Mono.error(new ConfigurationKeyNotFoundException("Configuration key " + key + " not found")))
				.map(configuration -> configuration.withValue(value)).flatMap(configurationRepository::save);

	}

	private Mono<String> getByKey(final String key) {

		return configurationRepository.findByKey(key)
				.switchIfEmpty(
						Mono.error(new ConfigurationKeyNotFoundException("Configuration key " + key + " not found")))
				.map(configuration -> configuration.getValue());

	}

	public Mono<byte[]> getServerKey() {

		return getByKey("serverKey").map(Base64.getDecoder()::decode)
				.onErrorResume(ConfigurationKeyNotFoundException.class, notFound -> refreshServerKey());

	}

	public Mono<byte[]> refreshServerKey() {

		final String serverKeyConfigurationKey = "serverKey";

		return keyService.generateRandomBytes(16).zipWith(existsByKey(serverKeyConfigurationKey)).flatMap(tuple -> {
			if (tuple.getT2()) {
				setValue(serverKeyConfigurationKey, Base64.getEncoder().encodeToString(tuple.getT1()));
			} else {
				create(serverKeyConfigurationKey, Base64.getEncoder().encodeToString(tuple.getT1()));
			}
			return Mono.just(tuple.getT1());
		});

	}

	private Mono<Boolean> existsByKey(final String key) {
		return configurationRepository.findByKey(key).map(u -> true).defaultIfEmpty(false);
	}

}