package org.owasp.securityshepherd.service;

import java.util.Optional;

import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.Module.ModuleBuilder;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.primitives.Bytes;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Service
public final class ModuleService {

	@Autowired
	ModuleRepository moduleRepository;

	@Autowired
	UserService userService;

	@Autowired
	ConfigurationService configurationService;

	@Autowired
	KeyService keyService;

	@Autowired
	CryptoService cryptoService;

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

	public boolean verifyFlag(final int userId, final int moduleId, final String submittedFlag) {

		if (submittedFlag == null) {
			return false;
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

			final String correctFlag=getDynamicFlag(userId, moduleId);
			
			return submittedFlag.equalsIgnoreCase(correctFlag);

		}

	}

	public void setExactFlag(final int id, final String exactFlag) {

		if (id == 0) {
			throw new IllegalArgumentException("id can't be zero");
		} else if (id < 0) {
			throw new IllegalArgumentException("id can't be negative");
		}

		if (exactFlag == null) {
			throw new NullPointerException("Flag can't be null");
		} else if (exactFlag.isEmpty()) {
			throw new IllegalArgumentException("Flag can't be empty");

		}

		final Module exactFlagModule = get(id).withFlagEnabled(true).withExactFlag(true).withFlag(exactFlag);

		moduleRepository.save(exactFlagModule);

	}

	public void setDynamicFlag(final int id) {

		if (id == 0) {
			throw new IllegalArgumentException("id can't be zero");
		} else if (id < 0) {
			throw new IllegalArgumentException("id can't be negative");
		}

		Module dynamicFlagModule = get(id).withFlagEnabled(true).withExactFlag(false);

		if (dynamicFlagModule.getFlag() == null) {
			dynamicFlagModule = dynamicFlagModule.withFlag(keyService.generateRandomString(16));
		}

		moduleRepository.save(dynamicFlagModule);

	}

	public String getDynamicFlag(final int userId, final int moduleId) {

		Module dynamicFlagModule = get(moduleId);

		if (!dynamicFlagModule.isFlagEnabled()) {
			throw new IllegalArgumentException("Can't get dynamic flag if flag is disabled");
		}

		final byte[] userKey = userService.getKey(userId);
		final byte[] serverKey = configurationService.getServerKey();

		final byte[] fullKey = Bytes.concat(userKey, serverKey);

		if (dynamicFlagModule.getFlag() == null) {

			dynamicFlagModule = dynamicFlagModule.withFlag(keyService.generateRandomString(16));
			moduleRepository.save(dynamicFlagModule);

		}

		final byte[] baseFlag = dynamicFlagModule.getFlag().getBytes();

		final byte[] generatedFlag = cryptoService.HMAC(fullKey, baseFlag);

		return keyService.convertByteKeyToString(generatedFlag);

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