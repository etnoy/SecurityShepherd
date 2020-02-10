package org.owasp.securityshepherd.service;

import java.util.Optional;

import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.Module.ModuleBuilder;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Service
public final class ModuleService {

	@Autowired
	ModuleRepository moduleRepository;

	public Module create(String name) {

		if (name == null) {
			throw new NullPointerException();
		}

		if (name.isEmpty()) {
			throw new IllegalArgumentException();
		}

		log.debug("Creating module with name " + name);

		ModuleBuilder moduleBuilder = Module.builder();
		moduleBuilder.name(name);

		Module savedModule = moduleRepository.save(moduleBuilder.build());

		log.debug("Created module with ID " + savedModule.getId());

		return savedModule;

	}

	public void setName(long id, String name) {

		Module newDisplayNameModule = get(id).withName(name);

		moduleRepository.save(newDisplayNameModule);

	}

	public long count() {
		return moduleRepository.count();
	}

	public Module get(long id) {
		Optional<Module> returnedModule = moduleRepository.findById(id);

		if (!returnedModule.isPresent()) {
			throw new NullPointerException();
		} else {
			return returnedModule.get();
		}
	}

}