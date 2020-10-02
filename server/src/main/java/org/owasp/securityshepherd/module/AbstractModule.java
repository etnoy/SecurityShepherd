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

  @NonFinal
  private Long moduleId;
  
  public Long getModuleId() {
    if (moduleId == null) {
      throw new ModuleNotInitializedException("Must initialize module first");
    }

    return moduleId;
  }

  public Mono<Long> initialize() {
    return initialize(null);
  }

  public Mono<Long> initialize(final String staticFlag) {
    final Mono<Module> moduleMono = moduleService.create(this);
    return moduleMono.flatMap(
        module -> {
          this.moduleId = module.getId();
          if (staticFlag == null) {
            return moduleService.setDynamicFlag(moduleId).then(Mono.just(this.moduleId));
          } else {
            return moduleService.setStaticFlag(moduleId, staticFlag).then(Mono.just(this.moduleId));
          }
        });
  }

}
