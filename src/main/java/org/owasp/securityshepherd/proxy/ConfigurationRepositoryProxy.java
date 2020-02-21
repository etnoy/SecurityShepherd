package org.owasp.securityshepherd.proxy;

import java.util.Optional;

import org.owasp.securityshepherd.persistence.model.Configuration;

public interface ConfigurationRepositoryProxy extends RepositoryProxy<Configuration> {

	public boolean existsByKey(String key);

	public void deleteByKey(String key);

	public Optional<Configuration> findByKey(String key);
	
}