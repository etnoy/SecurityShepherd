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
package org.owasp.securityshepherd.module.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.securityshepherd.exception.ModuleNotInitializedException;
import org.owasp.securityshepherd.module.AbstractModule;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.Module;
import org.owasp.securityshepherd.module.ModuleService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class DummyModule extends AbstractModule {

  private final ModuleService moduleService;

  private final FlagHandler flagHandler;

  @Override
  public String getDescription() {
    return "Dummy module that returns a flag)";
  }

  @Override
  public String getName() {
    return "Dummy module";
  }

  @Override
  public String getShortName() {
    return "dummy-module";
  }

  public Mono<Long> initialize() {
    final Mono<Module> moduleMono = moduleService.create(this);
    return moduleMono.flatMap(
        module -> {
          this.moduleId = module.getId();
          return moduleService.setDynamicFlag(moduleId).then(Mono.just(this.moduleId));
        });
  }

  public Mono<String> getFlag(final long userId) {
    if (this.moduleId == null) {
      return Mono.error(new ModuleNotInitializedException("Must initialize module first"));
    }

    return flagHandler.getDynamicFlag(userId, this.moduleId);
  }
}
