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

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.owasp.securityshepherd.exception.InvalidFlagStateException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class BaseModule {
  @Getter @NonNull String moduleName;

  @Getter @NonNull ModuleService moduleService;

  @Getter @NonNull FlagHandler flagHandler;

  @NonNull Mono<Module> module;

  @Getter @NonNull Mono<Void> init;

  protected BaseModule(
      String moduleName, ModuleService moduleService, FlagHandler flagHandler, String staticFlag) {
    this.moduleName = moduleName;
    this.moduleService = moduleService;
    this.flagHandler = flagHandler;
    this.module = moduleService.create(moduleName);
    if (staticFlag != null) {
      this.init = Mono.when(this.module, moduleService.setStaticFlag(moduleName, staticFlag));
    } else {
      this.init = Mono.when(this.module);
    }
  }

  public Mono<String> getFlag() {
    return module.flatMap(
        m -> {
          if (m.isFlagStatic()) {
            return Mono.just(m.getStaticFlag());
          } else {
            return Mono.error(
                new InvalidFlagStateException("Cannot get dynamic flag without providing user id"));
          }
        });
  }

  public Mono<String> getFlag(final long userId) {
    return module.flatMap(
        m -> {
          if (m.isFlagStatic()) {
            return Mono.just(m.getStaticFlag());
          } else {
            return flagHandler.getDynamicFlag(userId, moduleName);
          }
        });
  }
}
