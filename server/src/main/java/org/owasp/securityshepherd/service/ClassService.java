package org.owasp.securityshepherd.service;

import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateClassNameException;
import org.owasp.securityshepherd.exception.DuplicateUserDisplayNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.UserIdNotFoundException;
import org.owasp.securityshepherd.persistence.model.ClassEntity;
import org.owasp.securityshepherd.persistence.model.User;
import org.owasp.securityshepherd.persistence.model.ClassEntity.ClassBuilder;
import org.owasp.securityshepherd.repository.ClassRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public final class ClassService {

	private final ClassRepository classRepository;

	public Mono<Long> count() {

		return classRepository.count();

	}

	public Mono<ClassEntity> create(final String name) {

		if (name == null) {
			throw new NullPointerException();
		}

		if (name.isEmpty()) {
			throw new IllegalArgumentException();
		}

		log.debug("Creating class with name " + name);

		final ClassBuilder classBuilder = ClassEntity.builder();
		classBuilder.name(name);

		return classRepository.save(classBuilder.build());

		// log.debug("Created user with ID " + savedClass.getId());

	}

	private Mono<Boolean> doesNotExistByName(final String name) {
		return classRepository.findByName(name).map(u -> false).defaultIfEmpty(true);
	}

	public Mono<Boolean> existsById(final int id) {

		return classRepository.existsById(id);

	}

	public Mono<ClassEntity> getById(final int id) throws InvalidClassIdException {

		if (id <= 0) {
			throw new InvalidClassIdException();
		}

		return Mono.just(id).filterWhen(classRepository::existsById)
				.switchIfEmpty(Mono.error(new ClassIdNotFoundException())).flatMap(classRepository::findById);

	}

	public Mono<ClassEntity> setName(final int id, final String name)
			throws ClassIdNotFoundException, InvalidClassIdException {

		if (name == null) {
			throw new IllegalArgumentException("Class name can't be null");
		}
		
		if (id <= 0) {
			throw new InvalidClassIdException();
		}

		Mono<String> nameMono = Mono.just(name).filterWhen(this::doesNotExistByName)
				.switchIfEmpty(Mono.error(new DuplicateClassNameException("Class name already exists")));

		return Mono.just(id).flatMap(classId -> {
			try {
				return getById(classId);
			} catch (InvalidClassIdException e) {
				return Mono.error(e);
			}
		}).zipWith(nameMono).map(tuple -> tuple.getT1().withName(tuple.getT2())).flatMap(classRepository::save);

		// return getById(id).switchIfEmpty(Mono.error(new ClassIdNotFoundException()))
		// .flatMap(classEntity -> classRepository.save(classEntity.withName(name)));

	}

}