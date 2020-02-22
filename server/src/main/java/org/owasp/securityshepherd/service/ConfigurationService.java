package org.owasp.securityshepherd.service;

import java.util.Base64;
import java.util.Optional;

import org.owasp.securityshepherd.persistence.model.Configuration;
import org.owasp.securityshepherd.proxy.ConfigurationRepositoryProxy;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public final class ConfigurationService {

	private final ConfigurationRepositoryProxy configurationRepositoryProxy;

	private final KeyService keyService;

	private Configuration create(final String key, final String value) {

		log.debug("Creating configuration key " + key + " with value " + value);

		return configurationRepositoryProxy.save(Configuration.builder().key(key).value(value).build());

	}

	private void setValue(final String key, final String value) {

		final Optional<Configuration> searchResult = configurationRepositoryProxy.findByKey(key);

		Configuration targetConfiguration;

		if (searchResult.isPresent()) {

			targetConfiguration = searchResult.get().withValue(value);

		} else {

			log.debug("Key " + key + " does not exist");
			throw new IllegalArgumentException("setValue requires an existing key");

		}

		log.debug("Setting configuration key " + key + " to value " + value);

		configurationRepositoryProxy.save(targetConfiguration);

	}

	private Optional<String> get(final String key) {

		final Optional<Configuration> searchResult = configurationRepositoryProxy.findByKey(key);

		if (searchResult.isPresent()) {

			return Optional.of(searchResult.get().getValue());

		} else {

			return Optional.empty();

		}

	}

	public byte[] getServerKey() {

		// Look for server key in database
		final Optional<String> returnedKey = get("serverKey");

		if (returnedKey.isPresent()) {

			// Server key exists in database, return it
			return Base64.getDecoder().decode(returnedKey.get());

		} else {

			// Key not found in configuration, generate it and return
			return refreshServerKey();

		}

	}

	public byte[] refreshServerKey() {

		log.info("Generating new server key");

		final String serverKeyConfigurationKey = "serverKey";

		final byte[] newServerKey = keyService.generateRandomBytes(16);

		// Create a String containing the key for storage in db
		final String newKeyString = Base64.getEncoder().encodeToString(newServerKey);

		// Look for server key in database
		if (existsByKey(serverKeyConfigurationKey)) {

			// Server key found in database, replace it with the new key
			setValue(serverKeyConfigurationKey, newKeyString);

		} else {

			// Key not found in database, store it there
			create(serverKeyConfigurationKey, newKeyString);

		}

		// Return the generated server key to caller
		return newServerKey;

	}

	private boolean existsByKey(final String key) {

		return configurationRepositoryProxy.existsByKey(key);

	}

}