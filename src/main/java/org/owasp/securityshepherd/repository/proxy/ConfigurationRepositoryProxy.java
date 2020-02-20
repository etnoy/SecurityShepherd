package org.owasp.securityshepherd.repository.proxy;

import java.util.Optional;

import org.owasp.securityshepherd.model.Configuration;

public interface ConfigurationRepositoryProxy extends RepositoryProxy<Configuration> {

	public boolean existsByKey(String key);

	public void deleteByKey(String key);

	public Optional<Configuration> findByKey(String key);
	
}