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

import org.owasp.securityshepherd.crypto.CryptoService;
import org.owasp.securityshepherd.crypto.KeyService;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleIdNotFoundException;
import org.owasp.securityshepherd.service.ConfigurationService;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.stereotype.Service;
import com.google.common.primitives.Bytes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public final class FlagHandler {

  private final ModuleService moduleService;

  private final UserService userService;

  private final ConfigurationService configurationService;

  private final CryptoService cryptoService;

  private final KeyService keyService;

  public Mono<Boolean> verifyFlag(final long userId, final long moduleId,
      final String submittedFlag) {
    if (submittedFlag == null) {
      return Mono.error(new NullPointerException("Submitted flag cannot be null"));
    }

    log.trace("Verifying flag " + submittedFlag + " submitted by userId " + userId + " to moduleId "
        + moduleId);

    // Get the module from the repository
    final Mono<Module> currentModule = moduleService.findById(moduleId);

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

  public Mono<String> getDynamicFlag(final long userId, final long moduleId) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }

    final Mono<byte[]> baseFlag =
        // Find the module in the repo
        moduleService.findById(moduleId)
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

    return keyMono.zipWith(baseFlag).map(tuple -> cryptoService.hmac(tuple.getT1(), tuple.getT2()))
        .map(keyService::byteFlagToString);
  }
}
