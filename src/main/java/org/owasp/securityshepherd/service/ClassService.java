package org.owasp.securityshepherd.service;

import java.util.Optional;

import org.owasp.securityshepherd.model.ClassEntity;
import org.owasp.securityshepherd.model.ClassEntity.ClassBuilder;
import org.owasp.securityshepherd.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
@Service
public final class ClassService {

	@Autowired
	ClassRepository classRepository;

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

	public void setName(final int id, final String displayName) {

		ClassEntity newNameClass = get(id).get().withName(displayName);

		classRepository.save(newNameClass);

	}

	public long count() {

		return classRepository.count();

	}

	public Optional<ClassEntity> get(final int id) {

		if (id == 0) {
			throw new IllegalArgumentException("id can't be zero");
		} else if (id < 0) {
			throw new IllegalArgumentException("id can't be negative");
		}

		return classRepository.findById(id);

	}

}