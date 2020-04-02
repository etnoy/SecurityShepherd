package org.owasp.securityshepherd.service;

import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleIdNotFoundException;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.repository.ModuleRepository;
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

  public Mono<Long> count() {
    return moduleRepository.count();
  }

  public Mono<Module> create(final String moduleName) {
    if (moduleName == null) {
      return Mono.error(new NullPointerException("Module name cannot be null"));
    }

    if (moduleName.isEmpty()) {
      return Mono.error(new IllegalArgumentException("Module name cannot be empty"));
    }

    log.info("Creating new module with name " + moduleName);

    return Mono.just(moduleName).filterWhen(this::doesNotExistByName)
        .switchIfEmpty(Mono.error(new DuplicateModuleNameException("Module name already exists")))
        .map(name -> Module.builder().name(name).build()).flatMap(moduleRepository::save);
  }

  private Mono<Boolean> doesNotExistByName(final String moduleName) {
    return moduleRepository.findByName(moduleName).map(u -> false).defaultIfEmpty(true);
  }

  public Flux<Module> findAll() {
    return moduleRepository.findAll();
  }

  public Mono<Module> findById(final int moduleId) {
    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }
    return moduleRepository.findById(moduleId);
  }

  public Mono<String> findNameById(final int moduleId) {
    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }

    return moduleRepository.findById(moduleId).map(Module::getName);
  }

  public Mono<String> getDynamicFlag(final int userId, final int moduleId) {
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
        .map(keyService::convertByteKeyToString);
  }

  private Exception moduleIdMustBePositive() {
    return new InvalidModuleIdException("Module id must be a strictly positive integer");
  }

  public Mono<Module> setDynamicFlag(final int moduleId) {
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

  public Mono<Module> setExactFlag(final int moduleId, final String exactFlag) {
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

  public Mono<Module> setName(final int moduleId, final String moduleName) {
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

  public Mono<Boolean> verifyFlag(final int userId, final int moduleId,
      final String submittedFlag) {
    if (submittedFlag == null) {
      return Mono.error(new NullPointerException("Submitted flag cannot be null"));
    }

    // Get the module from the repository
    final Mono<Module> currentModule = findById(moduleId);

    final Mono<Boolean> isValid = currentModule
        // If the module wasn't found, return error
        .switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
        // Check to see if flags are enabled
        .filter(Module::isFlagEnabled)
        // If flag wasn't enabled, return error
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
