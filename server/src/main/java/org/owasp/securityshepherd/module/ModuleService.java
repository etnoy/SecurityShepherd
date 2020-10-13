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
package org.owasp.securityshepherd.module;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.securityshepherd.crypto.KeyService;
import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.ModuleNameNotFoundException;
import org.springframework.stereotype.Service;
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

  public Mono<Module> create(final String moduleName) {
    return findByName(moduleName).switchIfEmpty(doCreateModule(moduleName));
  }

  private Mono<Module> doCreateModule(final String moduleName) {
    if (moduleName == null) {
      return Mono.error(new NullPointerException("Module name cannot be null"));
    }

    log.info("Creating new module in database with id " + moduleName);

    return Mono.just(moduleName)
        .filterWhen(this::doesNotExistByName)
        .switchIfEmpty(
            Mono.error(
                new DuplicateModuleNameException(
                    String.format("Module id %s already exists", moduleName))))
        .map(
            exists ->
                Module.builder()
                    .isOpen(true)
                    .name(moduleName)
                    .key(keyService.generateRandomBytes(16))
                    .build())
        .flatMap(moduleRepository::save)
        .doOnSuccess(created -> log.trace("Created module with id " + moduleName));
  }

  public Flux<Module> findAll() {
    return moduleRepository.findAll();
  }

  public Flux<Module> findAllOpen() {
    return moduleRepository.findAllOpen();
  }

  public Mono<Module> findByName(final String moduleName) {
    log.trace("Find module with name " + moduleName);
    return moduleRepository.findByName(moduleName);
  }

  private Mono<Boolean> doesNotExistByName(final String moduleName) {
    return findByName(moduleName).map(u -> false).defaultIfEmpty(true);
  }

  public Mono<Module> setDynamicFlag(final String moduleName) {

    return findByName(moduleName)
        .switchIfEmpty(Mono.error(new ModuleNameNotFoundException()))
        .map(module -> module.withFlagStatic(false))
        .flatMap(moduleRepository::save);
  }

  public Mono<Module> setStaticFlag(final String moduleName, final String staticFlag) {

    if (staticFlag == null) {
      return Mono.error(new NullPointerException("Flag cannot be null"));
    } else if (staticFlag.isEmpty()) {
      return Mono.error(new InvalidFlagException("Flag cannot be empty"));
    }

    return findByName(moduleName)
        .switchIfEmpty(Mono.error(new ModuleNameNotFoundException()))
        .map(module -> module.withFlagStatic(true).withStaticFlag(staticFlag))
        .flatMap(moduleRepository::save);
  }
}
