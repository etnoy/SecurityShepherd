package org.owasp.securityshepherd.proxy;

import java.util.Optional;

import org.owasp.securityshepherd.model.Configuration;
import org.owasp.securityshepherd.repository.ConfigurationRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ConfigurationRepositoryProxyImpl implements ConfigurationRepositoryProxy {

	private final ConfigurationRepository configurationRepository;

	@Override
	public Configuration save(final Configuration configuration) {
		return configurationRepository.save(configuration);
	}

	@Override
	public Iterable<Configuration> saveAll(final Iterable<Configuration> configuration) {
		return configurationRepository.saveAll(configuration);
	}

	@Override
	public Optional<Configuration> findById(Integer id) {
		return configurationRepository.findById(id);
	}

	@Override
	public boolean existsById(Integer id) {
		return configurationRepository.existsById(id);
	}

	@Override
	public Iterable<Configuration> findAll() {
		return configurationRepository.findAll();
	}

	@Override
	public Iterable<Configuration> findAllById(Iterable<Integer> ids) {
		return configurationRepository.findAllById(ids);
	}

	@Override
	public long count() {
		return configurationRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		configurationRepository.deleteById(id);
	}

	@Override
	public void delete(Configuration configuration) {
		configurationRepository.delete(configuration);
	}

	@Override
	public void deleteAll(Iterable<? extends Configuration> configuration) {
		configurationRepository.deleteAll(configuration);
	}

	@Override
	public void deleteAll() {
		configurationRepository.deleteAll();
	}

	@Override
	public boolean existsByKey(String key) {
		return configurationRepository.existsByKey(key);
	}

	@Override
	public void deleteByKey(String key) {
		configurationRepository.deleteByKey(key);
	}

	@Override
	public Optional<Configuration> findByKey(String key) {
		return configurationRepository.findByKey(key);
	}

}