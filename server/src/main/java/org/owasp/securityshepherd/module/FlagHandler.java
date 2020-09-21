/**
 * This file is part of Security Shepherd.
 *
 * <p>Security Shepherd is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with Security
 * Shepherd. If not, see <http://www.gnu.org/licenses/>.
 */
package org.owasp.securityshepherd.module;

import org.owasp.securityshepherd.crypto.CryptoService;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.owasp.securityshepherd.exception.InvalidModuleIdException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.exception.ModuleIdNotFoundException;
import org.owasp.securityshepherd.service.ConfigurationService;
import org.owasp.securityshepherd.user.UserService;
import org.springframework.stereotype.Service;

import com.google.common.io.BaseEncoding;
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

  public Mono<String> getSaltedHmac(
      final long userId, final long moduleId, final String prefix, final String algorithm) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    if (moduleId <= 0) {
      return Mono.error(new InvalidModuleIdException());
    }

    final Mono<byte[]> moduleKey =
        // Find the module in the repo
        moduleService
            .findById(moduleId)
            // Return error if module wasn't found
            .switchIfEmpty(Mono.error(new ModuleIdNotFoundException()))
            // Make sure that the flag isn't static
            .filter(foundModule -> !foundModule.isFlagStatic())
            .switchIfEmpty(
                Mono.error(
                    new InvalidFlagStateException("Cannot get dynamic flag if flag is static")))
            // Get module key and convert to bytes
            .map(Module::getKey);

    final Mono<byte[]> userKey = userService.findKeyById(userId);

    final Mono<byte[]> serverKey = configurationService.getServerKey();

    return userKey
        .zipWith(moduleKey)
        .map(tuple -> Bytes.concat(tuple.getT1(), tuple.getT2(), prefix.getBytes()))
        .zipWith(serverKey)
        .map(tuple -> cryptoService.hmac(algorithm, tuple.getT2(), tuple.getT1()))
        .map(BaseEncoding.base32().lowerCase().omitPadding()::encode);
  }

  public Mono<Boolean> verifyFlag(
      final long userId, final long moduleId, final String submittedFlag) {
    if (submittedFlag == null) {
      return Mono.error(new NullPointerException("Submitted flag cannot be null"));
    }

    log.trace(
        "Verifying flag "
            + submittedFlag
            + " submitted by userId "
            + userId
            + " to moduleId "
            + moduleId);

    // Get the module from the repository
    final Mono<Module> currentModule = moduleService.findById(moduleId);

    final Mono<Boolean> isValid =
        currentModule
            // If the module wasn't found, return exception
            .switchIfEmpty(
                Mono.error(
                    new ModuleIdNotFoundException("Module id " + moduleId + " was not found")))
            // Check if the flag is valid
            .flatMap(
                module -> {
                  if (module.isFlagStatic()) {
                    // Verifying an exact flag
                    return Mono.just(module.getStaticFlag().equalsIgnoreCase(submittedFlag));
                  } else {
                    // Verifying a dynamic flag
                    return getDynamicFlag(userId, moduleId).map(submittedFlag::equalsIgnoreCase);
                  }
                });

    // Do some logging. First, check if error occurred and then print logs
    final Mono<String> validText =
        isValid
            .onErrorReturn(false)
            .map(validFlag -> Boolean.TRUE.equals(validFlag) ? "valid" : "invalid");

    Mono.zip(userService.findDisplayNameById(userId), validText, currentModule.map(Module::getName))
        .map(
            tuple ->
                "User "
                    + tuple.getT1()
                    + " submitted "
                    + tuple.getT2()
                    + " flag "
                    + submittedFlag
                    + " to module "
                    + tuple.getT3())
        .subscribe(log::debug);

    return isValid;
  }

  public Mono<String> getDynamicFlag(final long userId, final long moduleId) {
    return getSaltedHmac(userId, moduleId, "flag", "HmacSHA512");
  }
}
