package org.owasp.securityshepherd.service;

import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateClassNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.model.ClassEntity;
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

    return Mono.just(name).filterWhen(this::doesNotExistByName)
        .switchIfEmpty(Mono.error(new DuplicateClassNameException("Class name already exists")))
        .flatMap(className -> classRepository.save(ClassEntity.builder().name(className).build()));
  }

  public Mono<Void> deleteAll() {
    return classRepository.deleteAll();
  }

  private Mono<Boolean> doesNotExistByName(final String name) {
    return classRepository.findByName(name).map(u -> false).defaultIfEmpty(true);
  }

  public Mono<Boolean> existsById(final int id) {
    return classRepository.existsById(id);
  }

  public Mono<ClassEntity> getById(final int id) {
    if (id <= 0) {
      return Mono.error(new InvalidClassIdException());
    }

    return Mono.just(id).filterWhen(classRepository::existsById)
        .switchIfEmpty(Mono.error(new ClassIdNotFoundException()))
        .flatMap(classRepository::findById);
  }

  public Mono<ClassEntity> setName(final int id, final String name) throws InvalidClassIdException {

    if (name == null) {
      throw new IllegalArgumentException("Class name can't be null");
    }

    if (id <= 0) {
      throw new InvalidClassIdException();
    }

    Mono<String> nameMono = Mono.just(name).filterWhen(this::doesNotExistByName)
        .switchIfEmpty(Mono.error(new DuplicateClassNameException("Class name already exists")));

    return Mono.just(id).flatMap(this::getById).zipWith(nameMono)
        .map(tuple -> tuple.getT1().withName(tuple.getT2())).flatMap(classRepository::save);

  }

}
