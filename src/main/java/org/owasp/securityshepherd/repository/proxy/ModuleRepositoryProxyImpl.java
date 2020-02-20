package org.owasp.securityshepherd.repository.proxy;

import java.util.Optional;

import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ModuleRepositoryProxyImpl implements ModuleRepositoryProxy {

	private final ModuleRepository moduleRepository;

	@Override
	public Module save(final Module module) {
		return moduleRepository.save(module);
	}

	@Override
	public Iterable<Module> saveAll(final Iterable<Module> module) {
		return moduleRepository.saveAll(module);
	}

	@Override
	public Optional<Module> findById(Integer id) {
		return moduleRepository.findById(id);
	}

	@Override
	public boolean existsById(Integer id) {
		return moduleRepository.existsById(id);
	}

	@Override
	public Iterable<Module> findAll() {
		return moduleRepository.findAll();
	}

	@Override
	public Iterable<Module> findAllById(Iterable<Integer> ids) {
		return moduleRepository.findAllById(ids);
	}

	@Override
	public long count() {
		return moduleRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		moduleRepository.deleteById(id);
	}

	@Override
	public void delete(Module module) {
		moduleRepository.delete(module);
	}

	@Override
	public void deleteAll(Iterable<? extends Module> module) {
		moduleRepository.deleteAll(module);
	}

	@Override
	public void deleteAll() {
		moduleRepository.deleteAll();
	}

	@Override
	public boolean existsByName(String name) {
		return moduleRepository.existsByName(name);
	}

	@Override
	public void deleteByName(String name) {
		moduleRepository.deleteByName(name);

	}

	@Override
	public Optional<Module> findByName(String name) {
		return moduleRepository.findByName(name);
	}

}