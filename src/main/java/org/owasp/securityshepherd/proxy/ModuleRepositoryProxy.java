package org.owasp.securityshepherd.proxy;

import java.util.Optional;

import org.owasp.securityshepherd.persistence.model.Module;

public interface ModuleRepositoryProxy extends RepositoryProxy<Module> {

	public boolean existsByName(String name);

	public void deleteByName(String name);

	public Optional<Module> findByName(String name);

}