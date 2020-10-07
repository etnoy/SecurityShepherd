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
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Value
@NonFinal
public abstract class BaseModule {
  @NonNull protected String moduleName;

  @NonNull protected ModuleService moduleService;

  @NonNull protected FlagHandler flagHandler;

  @NonNull protected Mono<Module> module;

  private Mono<Void> waitSignal;

  public BaseModule(
      String moduleName, ModuleService moduleService, FlagHandler flagHandler, String staticFlag) {
    this.moduleName = moduleName;
    this.moduleService = moduleService;
    this.flagHandler = flagHandler;
    this.module = moduleService.create(moduleName);
    if (staticFlag != null) {
      this.waitSignal =
          Mono.when(this.module.and(moduleService.setStaticFlag(moduleName, staticFlag)));
    } else {
      this.waitSignal = Mono.when(this.module);
    }
  }

  public Mono<Void> init() {
    return Mono.when(this.module);
  }

  public Mono<String> getFlag(final long userId) {
    return module.flatMap(
        m -> {
          if (m.isFlagStatic()) {
            return Mono.just(m.getStaticFlag());
          } else {
            return flagHandler.getDynamicFlag(userId, m.getName());
          }
        });
  }
}
