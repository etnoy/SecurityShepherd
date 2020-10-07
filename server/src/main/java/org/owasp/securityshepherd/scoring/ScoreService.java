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
package org.owasp.securityshepherd.scoring;

import lombok.RequiredArgsConstructor;
import org.owasp.securityshepherd.exception.InvalidRankException;
import org.owasp.securityshepherd.module.ModulePointRepository;
import org.owasp.securityshepherd.scoring.ModulePoint.ModulePointBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public final class ScoreService {

  private final ModulePointRepository modulePointRepository;

  private final ScoreboardRepository scoreboardRepository;

  public Mono<ModulePoint> setModuleScore(
      final String moduleName, final int rank, final int points) {

    if (rank < 0) {
      return Mono.error(new InvalidRankException("Rank must be zero or a positive integer"));
    }
    ModulePointBuilder builder =
        ModulePoint.builder().moduleName(moduleName).rank(rank).points(points);
    return modulePointRepository.save(builder.build());
  }

  public Flux<ScoreboardEntry> getScoreboard() {
    return scoreboardRepository.findAll();
  }
}
