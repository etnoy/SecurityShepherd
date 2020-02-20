package org.owasp.securityshepherd.service;

import java.util.Optional;

import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.model.ClassEntity;
import org.owasp.securityshepherd.model.ClassEntity.ClassBuilder;
import org.owasp.securityshepherd.repository.ClassRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public final class ClassService {

	private final ClassRepository classRepository;

	public ClassEntity create(final String name) {

		if (name == null) {
			throw new NullPointerException();
		}

		if (name.isEmpty()) {
			throw new IllegalArgumentException();
		}

		log.debug("Creating class with name " + name);

		final ClassBuilder classBuilder = ClassEntity.builder();
		classBuilder.name(name);

		final ClassEntity savedClass = classRepository.save(classBuilder.build());

		log.debug("Created user with ID " + savedClass.getId());

		return savedClass;

	}

	public void setName(final int id, final String name) throws ClassIdNotFoundException, InvalidClassIdException {

		if (name == null) {
			throw new IllegalArgumentException("name can't be null");
		}

		Optional<ClassEntity> returnedClass = get(id);

		if (returnedClass.isPresent()) {

			classRepository.save(returnedClass.get().withName(name));

		} else {

			throw new ClassIdNotFoundException();

		}

	}

	public long count() {

		return classRepository.count();

	}

	public boolean existsById(final int id) {

		return classRepository.existsById(id);

	}

	public Optional<ClassEntity> get(final int id) throws InvalidClassIdException {

		if (id <= 0) {
			throw new InvalidClassIdException();
		}

		return classRepository.findById(id);

	}

}