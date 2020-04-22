/**
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

package org.owasp.securityshepherd.module;

import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.EmptyModuleNameException;
import org.owasp.securityshepherd.exception.EmptyModuleShortNameException;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.ModuleIdNotFoundException;
import org.owasp.securityshepherd.service.KeyService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public final class ModuleService {

  private final ModuleRepository moduleRepository;

  private final KeyService keyService;

  public Mono<Long> count() {
    return moduleRepository.count();
  }

  public Mono<Module> create(final String moduleName, final String url) {
    return this.create(moduleName, url, null);
  }

  public Mono<Module> create(final String moduleName, final String shortName,
      final String description) {
    if (moduleName == null) {
      return Mono.error(new NullPointerException("Module name cannot be null"));
    }

    if (moduleName.isEmpty()) {
      return Mono.error(new EmptyModuleNameException("Module name cannot be empty"));
    }

    log.info("Creating new module with name " + moduleName + " and url " + shortName);

    return Mono.just(moduleName).filterWhen(this::doesNotExistByName)
        .switchIfEmpty(Mono.error(new DuplicateModuleNameException(
            String.format("Module name %s already exists", moduleName))))
        .map(name -> Module.builder().name(name).description(description).shortName(shortName)
            .build())
        .flatMap(moduleRepository::save);
  }

  private Mono<Boolean> doesNotExistByName(final String moduleName) {
    return moduleRepository.findByName(moduleName).map(u -> false).defaultIfEmpty(true);
  }

  public Flux<Module> findAll() {
    return moduleRepository.findAll();
  }

  public Flux<Module> findAllOpen() {
    return moduleRepository.findAllOpen();
  }
  
  public Mono<Module> findById(final long moduleId) {
    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }
    return moduleRepository.findById(moduleId);
  }

  public Mono<Module> findByShortName(final String shortName) {
    if (shortName == null) {
      return Mono.error(new NullPointerException("Module short name cannot be null"));
    }
    if (shortName.isEmpty()) {
      return Mono.error(new EmptyModuleShortNameException("Module short name cannot be empty"));
    }
    return moduleRepository.findByShortName(shortName);
  }

  public Mono<String> findNameById(final long moduleId) {
    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException("Module id must be a strictly positive integer"));
    }

    return moduleRepository.findById(moduleId).map(Module::getName);
  }

  private Exception moduleIdMustBePositive() {
    return new InvalidModuleIdException("Module id must be a strictly positive integer");
  }

  public Mono<Module> setDynamicFlag(final long moduleId) {
    if (moduleId <= 0) {
      return Mono.error(moduleIdMustBePositive());
    }

    return findById(moduleId).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
        .map(module -> module.withFlagEnabled(true).withFlagExact(false)).flatMap(module -> {
          if (module.getFlag() == null) {
            return keyService.generateRandomString(16).map(module::withFlag);
          }
          return Mono.just(module);
        }).flatMap(moduleRepository::save);
  }

  public Mono<Module> setExactFlag(final long moduleId, final String exactFlag) {
    if (moduleId <= 0) {
      return Mono.error(moduleIdMustBePositive());
    }

    if (exactFlag == null) {
      return Mono.error(new NullPointerException("Flag cannot be null"));
    } else if (exactFlag.isEmpty()) {
      return Mono.error(new InvalidFlagException("Flag cannot be empty"));
    }

    return findById(moduleId).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
        .map(module -> module.withFlagEnabled(true).withFlagExact(true).withFlag(exactFlag))
        .flatMap(moduleRepository::save);
  }

  public Mono<Module> setName(final Long moduleId, final String moduleName) {
    if (moduleId <= 0) {
      return Mono.error(moduleIdMustBePositive());
    }

    if (moduleName == null) {
      return Mono.error(new NullPointerException("Module name cannot be null"));
    }

    if (moduleName.isEmpty()) {
      return Mono.error(new EmptyModuleNameException("Module name cannot be empty"));
    }

    log.info("Setting name of module with id " + moduleId + " to " + moduleName);

    return findById(moduleId).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
        .map(module -> module.withName(moduleName)).flatMap(moduleRepository::save);
  }
}