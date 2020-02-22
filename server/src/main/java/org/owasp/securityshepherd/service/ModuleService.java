package org.owasp.securityshepherd.service;

import java.util.Optional;

import org.owasp.securityshepherd.exception.EntityIdException;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleIdNotFoundException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.persistence.model.Module;
import org.owasp.securityshepherd.persistence.model.Module.ModuleBuilder;
import org.owasp.securityshepherd.proxy.ModuleRepositoryProxy;
import org.springframework.stereotype.Service;

import com.google.common.primitives.Bytes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public final class ModuleService {

	private final ModuleRepositoryProxy moduleRepositoryProxy;

	private final UserService userService;

	private final ConfigurationService configurationService;

	private final KeyService keyService;

	private final CryptoService cryptoService;

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

		final Module savedModule = moduleRepositoryProxy.save(moduleBuilder.build());

		log.debug("Created module with ID " + savedModule.getId());

		return savedModule;

	}

	public boolean verifyFlag(final int userId, final int moduleId, final String submittedFlag)
			throws ModuleIdNotFoundException, UserIdNotFoundException, InvalidUserIdException,
			InvalidFlagStateException {

		if (submittedFlag == null) {
			return false;
		}

		final Optional<Module> returnedModule = get(moduleId);

		if (!returnedModule.isPresent()) {

			throw new ModuleIdNotFoundException();

		}

		final Module submittedModule = returnedModule.get();

		if (!submittedModule.isFlagEnabled()) {

			throw new InvalidFlagStateException("Cannot verify flag if flag is not enabled");
		}

		if (submittedModule.isExactFlag()) {

			// Flag is of the exact type, so no cryptography needed
			return submittedModule.getFlag().equalsIgnoreCase(submittedFlag);

		} else {

			final String correctFlag = getDynamicFlag(userId, moduleId);

			return submittedFlag.equalsIgnoreCase(correctFlag);

		}

	}

	public void setExactFlag(final int id, final String exactFlag) throws EntityIdException {

		if (id <= 0) {

			throw new InvalidModuleIdException();

		}

		if (exactFlag == null) {

			throw new NullPointerException("Flag can't be null");

		} else if (exactFlag.isEmpty()) {

			throw new IllegalArgumentException("Flag can't be empty");

		}

		final Optional<Module> returnedModule = get(id);

		if (!returnedModule.isPresent()) {

			throw new ModuleIdNotFoundException();

		}

		final Module exactFlagModule = returnedModule.get().withFlagEnabled(true).withExactFlag(true)
				.withFlag(exactFlag);

		moduleRepositoryProxy.save(exactFlagModule);

	}

	public void setDynamicFlag(final int id) throws InvalidModuleIdException, ModuleIdNotFoundException {

		if (id <= 0) {

			throw new InvalidModuleIdException();

		}

		final Optional<Module> returnedModule = get(id);

		if (!returnedModule.isPresent()) {

			throw new ModuleIdNotFoundException();

		}

		Module dynamicFlagModule = returnedModule.get().withFlagEnabled(true).withExactFlag(false);

		if (dynamicFlagModule.getFlag() == null) {
			dynamicFlagModule = dynamicFlagModule.withFlag(keyService.generateRandomString(16));
		}

		moduleRepositoryProxy.save(dynamicFlagModule);

	}

	public String getDynamicFlag(final int userId, final int moduleId)
			throws ModuleIdNotFoundException, UserIdNotFoundException, InvalidUserIdException {

		final Optional<Module> returnedModule = get(moduleId);

		if (!returnedModule.isPresent()) {

			throw new ModuleIdNotFoundException();

		}

		final Module dynamicFlagModule = returnedModule.get();

		if (!dynamicFlagModule.isFlagEnabled()) {
			throw new IllegalArgumentException("Can't get dynamic flag if flag is disabled");
		}

		final byte[] userKey = userService.getKey(userId);
		final byte[] serverKey = configurationService.getServerKey();

		final byte[] fullKey = Bytes.concat(userKey, serverKey);

		final byte[] baseFlag = dynamicFlagModule.getFlag().getBytes();

		final byte[] generatedFlag = cryptoService.hmac(fullKey, baseFlag);

		return keyService.convertByteKeyToString(generatedFlag);

	}

	public void setName(final int id, final String name) throws ModuleIdNotFoundException {

		final Optional<Module> returnedModule = get(id);

		if (!returnedModule.isPresent()) {

			throw new ModuleIdNotFoundException();

		}

		final Module newDisplayNameModule = returnedModule.get().withName(name);

		moduleRepositoryProxy.save(newDisplayNameModule);

	}

	public long count() {

		return moduleRepositoryProxy.count();

	}

	public Optional<Module> get(final int id) {

		if (id == 0) {
			throw new IllegalArgumentException("id can't be zero");
		} else if (id < 0) {
			throw new IllegalArgumentException("id can't be negative");
		}

		return moduleRepositoryProxy.findById(id);

	}

}