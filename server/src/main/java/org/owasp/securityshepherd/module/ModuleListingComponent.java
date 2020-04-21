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

import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.module.ModuleListItemDto.ModuleListItemDtoBuilder;
import org.owasp.securityshepherd.service.SubmissionService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public final class ModuleListingComponent {

  private final ModuleService moduleService;

  private final SubmissionService submissionService;

  public Flux<ModuleListItemDto> findAllOpenByUserId(final long userId) {
    if (userId <= 0) {
      return Flux.error(new InvalidUserIdException());
    }

    final ModuleListItemDtoBuilder moduleListItemDtoBuilder = ModuleListItemDto.builder();
    // TODO: Check if user id is valid and exists
    return submissionService
        // Find all valid submissions by this user
        .findAllSolvedModulesByUserId(userId).flatMapMany(finishedModules ->
        // Get all modules
        moduleService.findAll().map(module -> {
          // For each module, construct a module list item
          moduleListItemDtoBuilder.id(module.getId());
          moduleListItemDtoBuilder.name(module.getName());
          moduleListItemDtoBuilder.shortName(module.getShortName());
          moduleListItemDtoBuilder.description(module.getDescription());
          moduleListItemDtoBuilder.name(module.getName());
          // Check if this module id is finished
          moduleListItemDtoBuilder.isSolved(finishedModules.contains(module.getId()));
          // Build the module list item and return
          return moduleListItemDtoBuilder.build();
        }));
  }

  public Mono<ModuleListItemDto> findByUserIdAndShortName(final long userId,
      final String shortName) {
    if (userId <= 0) {
      return Mono.error(new InvalidUserIdException());
    }

    final ModuleListItemDtoBuilder moduleListItemDtoBuilder = ModuleListItemDto.builder();
    // TODO: Check if user id is valid and exists

    final Mono<Module> moduleMono = moduleService.findByShortName(shortName);

    return moduleMono.map(Module::getId)
        // Find all valid submissions by this user
        .flatMap(moduleId -> validSubmissionExistsByUserIdAndModuleId(userId, moduleId))
        .defaultIfEmpty(false).zipWith(moduleMono).map(tuple -> {
          final Module module = tuple.getT2();
          // For each module, construct a module list item
          moduleListItemDtoBuilder.id(module.getId());
          moduleListItemDtoBuilder.name(module.getName());
          moduleListItemDtoBuilder.shortName(module.getShortName());
          moduleListItemDtoBuilder.description(module.getDescription());
          moduleListItemDtoBuilder.name(module.getName());
          moduleListItemDtoBuilder.isSolved(tuple.getT1());
          // Build the module list item and return
          return moduleListItemDtoBuilder.build();
        });
  }

  private Mono<Boolean> validSubmissionExistsByUserIdAndModuleId(final long userId,
      final long moduleId) {
    return submissionService.findAllValidByUserIdAndModuleId(userId, moduleId).map(u -> true)
        .defaultIfEmpty(false).next();
  }
}
