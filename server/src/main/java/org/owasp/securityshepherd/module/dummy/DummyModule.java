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

import org.owasp.securityshepherd.module.AbstractModule;
import org.owasp.securityshepherd.module.FlagHandler;
import org.owasp.securityshepherd.module.ModuleService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DummyModule extends AbstractModule {

  public Mono<String> getFlag(final long userId) {

    return flagHandler.getDynamicFlag(userId, getModuleId());
  }

  public DummyModule(final ModuleService moduleService, final FlagHandler flagHandler) {
    super("Dummy module", "dummy-module", "Dummy module", moduleService, flagHandler);
  }
}
