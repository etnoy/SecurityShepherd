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

package org.owasp.securityshepherd.service;

import org.owasp.securityshepherd.dto.ModuleListItemDto;
import org.owasp.securityshepherd.dto.ModuleListItemDto.ModuleListItemDtoBuilder;
import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleIdNotFoundException;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import com.google.common.primitives.Bytes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
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

  private final SubmissionRepository submissionRepository;

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
      return Mono.error(new IllegalArgumentException("Module name cannot be empty"));
    }

    log.info("Creating new module with name " + moduleName + " and url " + shortName);

    return Mono.just(moduleName).filterWhen(this::doesNotExistByName)
        .switchIfEmpty(Mono.error(new DuplicateModuleNameException("Module name already exists")))
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

  public Flux<ModuleListItemDto> findAllOpenByUserId(final long userId) {
    if (userId <= 0) {
      return Flux.error(new InvalidUserIdException());
    }

    final ModuleListItemDtoBuilder moduleListItemDtoBuilder = ModuleListItemDto.builder();
    // TODO: Check if user id is valid and exists
    return submissionRepository
        // Find all valid submissions by this user
        .findAllValidByUserId(userId)
        // Extract the corresponding module ids
        .map(Submission::getModuleId)
        // Collect all finished modules into a list
        .collectList()
        //
        .flatMapMany(finishedModules ->
        // Get all modules
        moduleRepository.findAll().map(module -> {
          // For each module, construct a module list item
          moduleListItemDtoBuilder.id(module.getId());
          moduleListItemDtoBuilder.name(module.getName());
          moduleListItemDtoBuilder.shortName(module.getShortName());
          moduleListItemDtoBuilder.description(module.getDescription());
          moduleListItemDtoBuilder.name(module.getName());
          // Check if this module id is finished
          moduleListItemDtoBuilder.isSolved(finishedModules.contains(module.getId()));
          // Build the module list item and return
          return moduleListItemDtoBuilder.build();
        }));

  }

  public Mono<Module> findById(final long moduleId) {
    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }
    return moduleRepository.findById(moduleId);
  }

  public Mono<Module> findByShortName(final String shortName) {
    return moduleRepository.findByShortName(shortName);
  }

  public Mono<ModuleListItemDto> findByUserIdAndShortName(final long userId,
      final String shortName) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    final ModuleListItemDtoBuilder moduleListItemDtoBuilder = ModuleListItemDto.builder();
    // TODO: Check if user id is valid and exists

    final Mono<Module> moduleMono = findByShortName(shortName);

    return moduleMono.map(Module::getId)
        // Find all valid submissions by this user
        .flatMap(moduleId -> validSubmissionExistsByUserIdAndModuleId(userId, moduleId))
        .defaultIfEmpty(false).zipWith(moduleMono).map(tuple -> {
          final Module module = tuple.getT2();
          // For each module, construct a module list item
          moduleListItemDtoBuilder.id(module.getId());
          moduleListItemDtoBuilder.name(module.getName());
          moduleListItemDtoBuilder.shortName(module.getShortName());
          moduleListItemDtoBuilder.description(module.getDescription());
          moduleListItemDtoBuilder.name(module.getName());
          moduleListItemDtoBuilder.isSolved(tuple.getT1());
          // Build the module list item and return
          return moduleListItemDtoBuilder.build();
        });
  }

  public Mono<String> findNameById(final long moduleId) {
    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }

    return moduleRepository.findById(moduleId).map(Module::getName);
  }

  public Mono<String> getDynamicFlag(final long userId, final long moduleId) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }

    final Mono<byte[]> baseFlag =
        // Find the module in the repo
        findById(moduleId)
            // Return error if module wasn't found
            .switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
            // Check to see if flag is enable
            .filter(Module::isFlagEnabled)
            // Return error if flag isn't enabled
            .switchIfEmpty(Mono.error(
                new InvalidFlagStateException("Cannot get dynamic flag if flag is disabled")))
            // Check to see if flag is dynamic
            .filter(module -> !module.isFlagExact())
            // Return error if flag isn't dynamic
            .switchIfEmpty(Mono
                .error(new InvalidFlagStateException("Cannot get dynamic flag if flag is exact")))
            // Get flag from module
            .map(Module::getFlag)
            // Get bytes from flag string
            .map(String::getBytes);

    final Mono<byte[]> keyMono =
        userService.findKeyById(userId).zipWith(configurationService.getServerKey())
            .map(tuple -> Bytes.concat(tuple.getT1(), tuple.getT2()));

    return keyMono.zipWith(baseFlag)
        .flatMap(tuple -> cryptoService.hmac(tuple.getT1(), tuple.getT2()))
        .map(keyService::byteFlagToString);
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
      return Mono.error(new InvalidFlagException("Flag cannot be null"));
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
      return Mono.error(new IllegalArgumentException("Module name cannot be empty"));
    }

    log.info("Setting name of module with id " + moduleId + " to " + moduleName);

    return findById(moduleId).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
        .map(module -> module.withName(moduleName)).flatMap(moduleRepository::save);
  }

  private Mono<Boolean> validSubmissionExistsByUserIdAndModuleId(final long userId,
      final long moduleId) {
    return submissionRepository.findValidByUserIdAndModuleId(userId, moduleId).map(u -> true)
        .defaultIfEmpty(false).next();
  }

  public Mono<Boolean> verifyFlag(final long userId, final long moduleId,
      final String submittedFlag) {
    if (submittedFlag == null) {
      return Mono.error(new NullPointerException("Submitted flag cannot be null"));
    }

    log.trace("Verifying flag " + submittedFlag + " submitted by userId " + userId + " to moduleId "
        + moduleId);

    // Get the module from the repository
    final Mono<Module> currentModule = findById(moduleId);

    final Mono<Boolean> isValid = currentModule
        // If the module wasn't found, return exception
        .switchIfEmpty(
            Mono.error(new ModuleIdNotFoundException("Module id " + moduleId + " was not found")))
        // Check to see if flags are enabled
        .filter(Module::isFlagEnabled)
        // If flag wasn't enabled, return exception
        .switchIfEmpty(
            Mono.error(new InvalidFlagStateException("Cannot verify flag if flag is not enabled")))
        // Now check if the flag is valid
        .flatMap(module -> {
          if (module.isFlagExact()) {
            // Verifying an exact flag
            return Mono.just(module.getFlag().equalsIgnoreCase(submittedFlag));
          } else {
            // Verifying a dynamic flag
            return getDynamicFlag(userId, moduleId).map(submittedFlag::equalsIgnoreCase);
          }
        });

    // Do some logging. First, check if error occurred and then print logs
    final Mono<String> validText = isValid.onErrorReturn(false)
        .map(validFlag -> Boolean.TRUE.equals(validFlag) ? "valid" : "invalid");

    Mono.zip(userService.findDisplayNameById(userId), validText, currentModule.map(Module::getName))
        .map(tuple -> "User " + tuple.getT1() + " submitted " + tuple.getT2() + " flag "
            + submittedFlag + " to module " + tuple.getT3())
        .subscribe(log::debug);

    return isValid;
  }
}
