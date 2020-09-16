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

import org.owasp.securityshepherd.exception.EmptyModuleShortNameException;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.module.ModuleListItem.ModuleListItemBuilder;
import org.owasp.securityshepherd.scoring.SubmissionService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public final class ModuleSolutions {

  private final ModuleService moduleService;

  private final SubmissionService submissionService;

  public Flux<ModuleListItem> findOpenModulesByUserIdWithSolutionStatus(final long userId) {
    if (userId <= 0) {
      return Flux.error(new InvalidUserIdException("User id must be a strictly positive integer"));
    }

    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();
    return submissionService
        // Find all valid submissions by this user
        .findAllValidIdsByUserId(userId)
        .flatMapMany(
            finishedModules ->
                // Get all modules
                moduleService
                    .findAllOpen()
                    .map(
                        module -> {
                          final long moduleId = module.getId();
                          // For each module, construct a module list item
                          moduleListItemBuilder.id(moduleId);
                          moduleListItemBuilder.name(module.getName());
                          moduleListItemBuilder.shortName(module.getShortName());
                          moduleListItemBuilder.description(module.getDescription());
                          // Check if this module id is finished
                          moduleListItemBuilder.isSolved(finishedModules.contains(moduleId));
                          // Build the module list item and return
                          return moduleListItemBuilder.build();
                        }));
  }

  public Mono<ModuleListItem> findOpenModuleByShortNameWithSolutionStatus(
      final long userId, final String shortName) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException("User id must be a strictly positive integer"));
    }
    if (shortName == null) {
      return Mono.error(new NullPointerException("Module short name cannot be null"));
    }
    if (shortName.isEmpty()) {
      return Mono.error(new EmptyModuleShortNameException("Module short name cannot be empty"));
    }
    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();

    final Mono<Module> moduleMono = moduleService.findByShortName(shortName).filter(Module::isOpen);

    return moduleMono
        .map(Module::getId)
        // Find all valid submissions by this user
        .flatMap(moduleId -> userHasSolvedThisModule(userId, moduleId))
        .defaultIfEmpty(false)
        .zipWith(moduleMono)
        .map(
            tuple -> {
              final Module module = tuple.getT2();
              // For each module, construct a module list item
              moduleListItemBuilder.id(module.getId());
              moduleListItemBuilder.name(module.getName());
              moduleListItemBuilder.shortName(module.getShortName());
              moduleListItemBuilder.description(module.getDescription());
              moduleListItemBuilder.isSolved(tuple.getT1());
              // Build the module list item and return
              return moduleListItemBuilder.build();
            });
  }

  private Mono<Boolean> userHasSolvedThisModule(final long userId, final long moduleId) {
    return submissionService
        .findAllValidByUserIdAndModuleId(userId, moduleId)
        .map(u -> true)
        .defaultIfEmpty(false);
  }
}
