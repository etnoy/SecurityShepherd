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

	@Autowired
	RNGService rngService;

	private final int keyLength = 16;

	public Module create(final String name) {

		if (name == null) {
			throw new NullPointerException();
		}

		if (name.isEmpty()) {
			throw new IllegalArgumentException();
		}

		log.debug("Creating module with name " + name);

		final ModuleBuilder moduleBuilder = Module.builder();
		moduleBuilder.name(name);

		final Module savedModule = moduleRepository.save(moduleBuilder.build());

		log.debug("Created module with ID " + savedModule.getId());

		return savedModule;

	}

	public boolean verifyFlag(final int moduleId, final int userId, final String submittedFlag) {

		if (submittedFlag == null) {
			throw new NullPointerException();
		}

		final Module submittedModule = get(moduleId);

		if (!submittedModule.isFlagEnabled()) {
			// TODO: maybe a better exception here?
			throw new IllegalArgumentException();
		}

		if (submittedModule.isExactFlag()) {
			// Flag is of the exact type, so no cryptography needed
			return submittedModule.getFlag().equalsIgnoreCase(submittedFlag);
		} else {

			// TODO
			return false;

		}

	}

	public String generateFlag(final long moduleId, final long userId) {

		// TODO
		return null;
	}

	public void setExactFlag(final int id, final String exactFlag) {

		final Module exactFlagModule = get(id).withFlagEnabled(true).withExactFlag(true).withFlag(exactFlag);

		moduleRepository.save(exactFlagModule);

	}

	public void setName(final int id, final String name) {

		final Module newDisplayNameModule = get(id).withName(name);

		moduleRepository.save(newDisplayNameModule);

	}

	public long count() {

		return moduleRepository.count();

	}

	public Module get(final int id) {
		
		final Optional<Module> returnedModule = moduleRepository.findById(id);

		if (!returnedModule.isPresent()) {
			throw new NullPointerException();
		} else {
			return returnedModule.get();
		}
		
	}

}