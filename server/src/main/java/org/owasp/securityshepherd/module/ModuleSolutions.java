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

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.module.ModuleListItem.ModuleListItemBuilder;
import org.owasp.securityshepherd.scoring.SubmissionService;
import org.springframework.stereotype.Component;
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
        .findAllValidModuleNamesByUserId(userId)
        .flatMapMany(
            finishedModules ->
                // Get all modules
                moduleService
                    .findAllOpen()
                    .map(
                        module -> {
                          final String moduleName = module.getName();
                          // For each module, construct a module list item
                          moduleListItemBuilder.name(moduleName);
                          // Check if this module id is finished
                          moduleListItemBuilder.isSolved(finishedModules.contains(moduleName));
                          // Build the module list item and return
                          return moduleListItemBuilder.build();
                        }));
  }

  public Mono<ModuleListItem> findOpenModuleByIdWithSolutionStatus(
      final long userId, final String moduleName) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException("User id must be a strictly positive integer"));
    }
    if (moduleName == null) {
      return Mono.error(new NullPointerException("Module short name cannot be null"));
    }
    // TODO: check if module id string is empty
    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();

    final Mono<Module> moduleMono = moduleService.findByName(moduleName).filter(Module::isOpen);

    return moduleMono
        .map(Module::getName)
        // Find all valid submissions by this user
        .flatMap(openModuleName -> userHasSolvedThisModule(userId, openModuleName))
        .defaultIfEmpty(false)
        .zipWith(moduleMono)
        .map(
            tuple -> {
              final Module module = tuple.getT2();
              // For each module, construct a module list item
              moduleListItemBuilder.name(module.getName());
              moduleListItemBuilder.isSolved(tuple.getT1());
              // Build the module list item and return
              return moduleListItemBuilder.build();
            });
  }

  public Mono<ModuleListItem> findModuleByIdWithSolutionStatus(
      final long userId, final String moduleName) {

    final Mono<Module> moduleMono = moduleService.findByName(moduleName);

    final Mono<List<String>> finishedModulesMono =
        submissionService
            // Find all valid submissions by this user
            .findAllValidModuleNamesByUserId(userId);

    final ModuleListItemBuilder moduleListItemBuilder = ModuleListItem.builder();
    return moduleMono
        .zipWith(finishedModulesMono)
        .map(
            tuple -> {
              moduleListItemBuilder.name(tuple.getT1().getName());
              moduleListItemBuilder.isSolved(tuple.getT2().contains(moduleName));
              return moduleListItemBuilder.build();
            });
  }

  private Mono<Boolean> userHasSolvedThisModule(final long userId, final String moduleName) {
    return submissionService
        .findAllValidByUserIdAndModuleName(userId, moduleName)
        .map(u -> true)
        .defaultIfEmpty(false);
  }
}
