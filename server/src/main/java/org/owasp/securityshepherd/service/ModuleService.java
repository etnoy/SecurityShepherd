package org.owasp.securityshepherd.service;

import org.owasp.securityshepherd.exception.DuplicateModuleNameException;
import org.owasp.securityshepherd.exception.InvalidFlagException;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleIdNotFoundException;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.repository.ModuleRepository;
import org.owasp.securityshepherd.repository.ModuleScoreRepository;
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
  
  private final ModuleScoreRepository moduleScoreRepository;

  private final UserService userService;

  private final ConfigurationService configurationService;

  private final KeyService keyService;

  private final CryptoService cryptoService;

  public Mono<Long> count() {
    return moduleRepository.count();
  }

  public Mono<String> findNameById(final int moduleId) {
    return moduleRepository.findById(moduleId).map(Module::getName);
  }

  public Mono<Void> deleteAll() {
    return moduleScoreRepository.deleteAll().then(moduleRepository.deleteAll());
  }

  public Mono<Module> create(final String moduleName) {
    if (moduleName == null) {
      throw new NullPointerException();
    }

    if (moduleName.isEmpty()) {
      throw new IllegalArgumentException();
    }

    log.info("Creating new module with name " + moduleName);

    return Mono.just(moduleName).filterWhen(this::doesNotExistByName)
        .switchIfEmpty(Mono.error(new DuplicateModuleNameException("Module name already exists")))
        .map(name -> Module.builder().name(name).build())
        .flatMap(moduleRepository::save);
  }

  private Mono<Boolean> doesNotExistByName(final String moduleName) {
    return moduleRepository.findByName(moduleName).map(u -> false).defaultIfEmpty(true);
  }

  public Flux<Module> findAll() {
    return moduleRepository.findAll();
  }

  public Mono<Module> findById(final int id) {
    if (id <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }

    return moduleRepository.findById(id).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()));
  }

  public Mono<String> getDynamicFlag(final int userId, final int moduleId) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }

    final Mono<byte[]> baseFlag = findById(moduleId)
        .switchIfEmpty(Mono.error(new ModuleIdNotFoundException())).filter(Module::isFlagEnabled)
        .switchIfEmpty(
            Mono.error(new InvalidFlagStateException("Can't get dynamic flag if flag is disabled")))
        .filter(module -> !module.isFlagExact())
        .switchIfEmpty(
            Mono.error(new InvalidFlagStateException("Can't get dynamic flag if flag is exact")))
        .map(module -> module.getFlag().getBytes());

    final Mono<byte[]> keyMono =
        userService.getKeyById(userId).zipWith(configurationService.getServerKey())
            .map(tuple -> Bytes.concat(tuple.getT1(), tuple.getT2()));

    return keyMono.zipWith(baseFlag)
        .flatMap(tuple -> cryptoService.hmac(tuple.getT1(), tuple.getT2()))
        .map(keyService::convertByteKeyToString);
  }

  public Mono<Module> setDynamicFlag(final int moduleId) {
    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }

    return findById(moduleId).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
        .map(module -> module.withFlagEnabled(true).withFlagExact(false)).flatMap(module -> {
          if (module.getFlag() == null) {
            return keyService.generateRandomString(16).map(module::withFlag);
          }
          return Mono.just(module);
        }).flatMap(moduleRepository::save);
  }

  public Mono<Module> setExactFlag(final int moduleId, final String exactFlag)
      throws InvalidFlagException, InvalidModuleIdException {
    if (moduleId <= 0) {
      throw new InvalidModuleIdException();
    }

    if (exactFlag == null) {
      throw new InvalidFlagException("Flag can't be null");
    } else if (exactFlag.isEmpty()) {
      throw new InvalidFlagException("Flag can't be empty");
    }

    return findById(moduleId).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
        .map(module -> module.withFlagEnabled(true).withFlagExact(true).withFlag(exactFlag))
        .flatMap(moduleRepository::save);
  }

  public Mono<Module> setName(final int moduleId, final String moduleName) {
    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }

    if (moduleName == null) {
      return Mono.error(new NullPointerException());
    }

    if (moduleName.isEmpty()) {
      return Mono.error(new IllegalArgumentException());
    }

    log.info("Setting name of module with id " + moduleId + " to " + moduleName);

    return findById(moduleId).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
        .map(module -> module.withName(moduleName)).flatMap(moduleRepository::save);
  }

  public Mono<Boolean> verifyFlag(final int userId, final int moduleId,
      final String submittedFlag) {
    if (submittedFlag == null) {
      return Mono.just(false);
    }

    log.info("User " + userId + " submitted flag " + submittedFlag + " to module " + moduleId);

    return findById(moduleId).switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
        .filter(Module::isFlagEnabled)
        .switchIfEmpty(
            Mono.error(new InvalidFlagStateException("Cannot verify flag if flag is not enabled")))
        .flatMap(module -> {
          if (module.isFlagExact()) {
            return Mono.just(module.getFlag().equalsIgnoreCase(submittedFlag));
          } else {
            return getDynamicFlag(userId, moduleId).map(submittedFlag::equalsIgnoreCase);
          }
        });
  }
}
