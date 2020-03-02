package org.owasp.securityshepherd.service;

import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.EntityIdException;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleIdNotFoundException;
import org.owasp.securityshepherd.persistence.model.Module;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.springframework.stereotype.Service;

import com.google.common.primitives.Bytes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public final class ModuleService {

	private final ModuleRepository moduleRepository;

	private final UserService userService;

	private final ConfigurationService configurationService;

	private final KeyService keyService;

	private final CryptoService cryptoService;

	private Mono<Boolean> doesNotExistByName(final String name) {
		return moduleRepository.findByName(name).map(u -> false).defaultIfEmpty(true);
	}

	public Mono<Module> create(final String name) {

		if (name == null) {
			throw new NullPointerException();
		}

		if (name.isEmpty()) {
			throw new IllegalArgumentException();
		}

		return Mono.just(name).filterWhen(this::doesNotExistByName)
				.switchIfEmpty(Mono.error(new DuplicateModuleNameException("Module name already exists")))
				.map(moduleName -> Module.builder().name(moduleName).build()).flatMap(moduleRepository::save);

	}

	public Mono<Boolean> verifyFlag(final int userId, final int moduleId, final String submittedFlag) {

		if (submittedFlag == null) {
			return Mono.just(false);
		}

		return getById(moduleId).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
				.filter(module -> module.isFlagEnabled())
				.switchIfEmpty(Mono.error(new InvalidFlagStateException("Cannot verify flag if flag is not enabled")))
				.flatMap(module -> {
					if (module.isFlagExact()) {
						return Mono.just(module.getFlag().equalsIgnoreCase(submittedFlag));
					} else {
						return getDynamicFlag(userId, moduleId).map(flag -> submittedFlag.equalsIgnoreCase(flag));
					}
				});

	}

	public Mono<Module> setExactFlag(final int id, final String exactFlag) throws EntityIdException, InvalidFlagException {

		if (id <= 0) {

			throw new InvalidModuleIdException();

		}

		if (exactFlag == null) {

			throw new InvalidFlagException("Flag can't be null");

		} else if (exactFlag.isEmpty()) {

			throw new InvalidFlagException("Flag can't be empty");

		}

		return getById(id).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
				.map(module -> module.withFlagEnabled(true).withFlagExact(true).withFlag(exactFlag))
				.flatMap(moduleRepository::save);

	}

	public Mono<Module> setDynamicFlag(final int id) {

		if (id <= 0) {

			return Mono.error(new InvalidModuleIdException());

		}

		return getById(id).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
				.map(module -> module.withFlagEnabled(true).withFlagExact(false))
				.flatMap(module -> {
					if (module.getFlag() == null) {
						return keyService.generateRandomString(16).map(module::withFlag);
					}
					return Mono.just(module);
				}).flatMap(moduleRepository::save);

	}

	public Mono<String> getDynamicFlag(final int userId, final int moduleId) {

		if (userId <= 0) {

			return Mono.error(new InvalidUserIdException());

		}

		if (moduleId <= 0) {

			return Mono.error(new InvalidModuleIdException());

		}

		final Mono<byte[]> baseFlag = getById(moduleId).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
				.filter(module -> module.isFlagEnabled())
				.switchIfEmpty(Mono.error(new InvalidFlagStateException("Can't get dynamic flag if flag is disabled")))
				.map(module -> module.getFlag().getBytes());

		final Mono<byte[]> keyMono = userService.getKeyById(userId).zipWith(configurationService.getServerKey())
				.map(tuple -> Bytes.concat(tuple.getT1(), tuple.getT2()));

		return keyMono.zipWith(baseFlag).flatMap(tuple -> {
			return cryptoService.hmac(tuple.getT1(), tuple.getT2());
		}).map(keyService::convertByteKeyToString);

	}

	public Mono<Module> setName(final int id, final String name) {

		if (id <= 0) {

			return Mono.error(new InvalidModuleIdException());

		}

		return getById(id).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
				.map(module -> module.withName(name)).flatMap(moduleRepository::save);

	}

	public Mono<Long> count() {

		return moduleRepository.count();

	}

	public Mono<Module> getById(final int id) {

		if (id <= 0) {

			return Mono.error(new InvalidModuleIdException());

		}

		return moduleRepository.findById(id).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()));

	}

}