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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.owasp.securityshepherd.exception.ModuleNotInitializedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Value
@RequiredArgsConstructor
@NonFinal
public abstract class AbstractModule {
  @NonNull protected final String name;

  @NonNull protected final String shortName;

  @NonNull protected final String description;

  @NonNull protected final ModuleService moduleService;

  @NonNull protected final FlagHandler flagHandler;

  @NonFinal protected Module module;

  public Mono<Long> initialize() {
    // Initialize with dynamic flag
    final Mono<Module> moduleMono = moduleService.create(this);
    return moduleMono.flatMap(
        createdModule -> {
          this.module = createdModule;
          return moduleService
              .setDynamicFlag(createdModule.getId())
              .then(Mono.just(createdModule.getId()));
        });
  }

  public Mono<Long> initialize(final String staticFlag) {
    // Initialize with static flag
    final Mono<Module> moduleMono = moduleService.create(this);
    return moduleMono.flatMap(
        createdModule -> {
          this.module = createdModule;
          return moduleService
              .setStaticFlag(createdModule.getId(), staticFlag)
              .then(Mono.just(createdModule.getId()));
        });
  }
  
  private void checkIfInitialized() {
    if (this.module == null) {
      throw new ModuleNotInitializedException("Must initialize module first");
    }
  }

  public Long getModuleId() {
    checkIfInitialized();
    return this.module.getId();
  }

  public Module getModule() {
    checkIfInitialized();
    return this.module;
  }

  public Mono<String> getFlag(final long userId) {
    checkIfInitialized();
    if (module.isFlagStatic()) {
      return Mono.just(module.getStaticFlag());
    } else {
      return flagHandler.getDynamicFlag(userId, module.getId());
    }
  }
}
