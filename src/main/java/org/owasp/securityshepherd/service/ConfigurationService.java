package org.owasp.securityshepherd.service;

import java.util.Base64;
import java.util.Optional;

import org.owasp.securityshepherd.model.Configuration;
import org.owasp.securityshepherd.model.Configuration.ConfigurationBuilder;
import org.owasp.securityshepherd.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Service
public final class ConfigurationService {

	@Autowired
	ConfigurationRepository configurationRepository;

	@Autowired
	KeyService keyService;

	public Configuration create(final String key, final String value) {

		if (key == null) {
			throw new NullPointerException();
		} else if (key.isEmpty()) {
			throw new IllegalArgumentException();
		}

		if (value == null) {
			throw new NullPointerException();
		} else if (value.isEmpty()) {
			throw new IllegalArgumentException();
		}

		log.debug("Creating configuration key " + key + " with value " + value);

		return configurationRepository.save(Configuration.builder().key(key).value(value).build());

	}

	public void setValue(final String key, final String value) {

		if (key == null) {
			throw new NullPointerException();
		} else if (key.isEmpty()) {
			throw new IllegalArgumentException();
		}

		if (value == null) {
			throw new NullPointerException();
		} else if (value.isEmpty()) {
			throw new IllegalArgumentException();
		}

		final Optional<Configuration> searchResult = configurationRepository.findByKey(key);

		Configuration targetConfiguration;

		if (searchResult.isPresent()) {

			targetConfiguration = searchResult.get().withValue(value);

		} else {

			targetConfiguration = Configuration.builder().key(key).value(value).build();

		}

		configurationRepository.save(targetConfiguration);

	}

	public Optional<String> get(final String key) {

		if (key == null) {
			throw new NullPointerException();
		} else if (key.isEmpty()) {
			throw new IllegalArgumentException();
		}

		final Optional<Configuration> searchResult = configurationRepository.findByKey(key);

		if (searchResult.isPresent()) {

			return Optional.of(searchResult.get().getValue());

		} else {

			return Optional.empty();

		}

	}

	public long count() {

		return configurationRepository.count();

	}

	public Optional<Configuration> findByKey(final String key) {

		return configurationRepository.findByKey(key);

	}

	public byte[] getServerKey() {

		final String serverKeyConfigurationKey = "serverKey";
		byte[] serverKey;

		Optional<String> returnedKey = get(serverKeyConfigurationKey);

		if (!returnedKey.isPresent()) {
			// Key not found in configuration, generate it first
			ConfigurationBuilder serverKeyConfiguration = Configuration.builder();

			serverKeyConfiguration.key(serverKeyConfigurationKey);

			// TODO: Configure how many bytes we want
			serverKey = keyService.generateRandomBytes(16);

			// Create a String containing the key for storage in db
			final String keyString = Base64.getEncoder().encodeToString(serverKey);

			serverKeyConfiguration.value(keyString);
			
			configurationRepository.save(serverKeyConfiguration.build());
			
		} else {
			serverKey = Base64.getDecoder().decode(returnedKey.get());
		}
		
		return serverKey;

	}

}