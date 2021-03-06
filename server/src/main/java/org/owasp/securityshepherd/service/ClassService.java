/*
 * This file is part of Security Shepherd.
 * 
 * Security Shepherd is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Security Shepherd.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.owasp.securityshepherd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.securityshepherd.exception.ClassIdNotFoundException;
import org.owasp.securityshepherd.exception.DuplicateClassNameException;
import org.owasp.securityshepherd.exception.InvalidClassIdException;
import org.owasp.securityshepherd.model.ClassEntity;
import org.owasp.securityshepherd.user.ClassRepository;
import org.springframework.stereotype.Service;
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
      return Mono.error(new NullPointerException());
    }

    if (name.isEmpty()) {
      return Mono.error(new IllegalArgumentException());
    }

    log.debug("Creating class with name " + name);

    return Mono.just(name)
        .filterWhen(this::doesNotExistByName)
        .switchIfEmpty(Mono.error(new DuplicateClassNameException("Class name already exists")))
        .flatMap(className -> classRepository.save(ClassEntity.builder().name(className).build()));
  }

  private Mono<Boolean> doesNotExistByName(final String name) {
    return classRepository.findByName(name).map(u -> false).defaultIfEmpty(true);
  }

  public Mono<Boolean> existsById(final long classIdd) {
    return classRepository.existsById(classIdd);
  }

  public Mono<ClassEntity> getById(final long classId) {
    if (classId <= 0) {
      return Mono.error(new InvalidClassIdException());
    }

    return Mono.just(classId)
        .filterWhen(classRepository::existsById)
        .switchIfEmpty(Mono.error(new ClassIdNotFoundException()))
        .flatMap(classRepository::findById);
  }

  public Mono<ClassEntity> setName(final long classId, final String name) {
    if (name == null) {
      return Mono.error(new IllegalArgumentException("Class name can't be null"));
    }

    if (classId <= 0) {
      return Mono.error(new InvalidClassIdException());
    }

    Mono<String> nameMono =
        Mono.just(name)
            .filterWhen(this::doesNotExistByName)
            .switchIfEmpty(
                Mono.error(new DuplicateClassNameException("Class name already exists")));

    return Mono.just(classId)
        .flatMap(this::getById)
        .zipWith(nameMono)
        .map(tuple -> tuple.getT1().withName(tuple.getT2()))
        .flatMap(classRepository::save);
  }
}
