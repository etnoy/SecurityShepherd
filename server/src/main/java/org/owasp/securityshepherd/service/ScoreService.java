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

package org.owasp.securityshepherd.service;

import org.owasp.securityshepherd.model.ModulePoint;
import org.owasp.securityshepherd.model.ModulePoint.ModulePointBuilder;
import org.owasp.securityshepherd.model.Scoreboard;
import org.owasp.securityshepherd.repository.ModulePointRepository;
import org.owasp.securityshepherd.repository.ScoreboardRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public final class ScoreService {

  private final ModulePointRepository modulePointRepository;

  private final ScoreboardRepository scoreboardRepository;

  public Mono<ModulePoint> setModuleScore(final long moduleId, final int rank, final int score) {
    ModulePointBuilder builder = ModulePoint.builder().moduleId(moduleId).rank(rank).points(score);
    return modulePointRepository.save(builder.build());
  }

  public Flux<Scoreboard> getScoreboard() {
    return scoreboardRepository.findAll();
  }

}
