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

package org.owasp.securityshepherd.controller;

import org.owasp.securityshepherd.model.Scoreboard;
import org.owasp.securityshepherd.model.Submission;
import org.owasp.securityshepherd.service.ScoreService;
import org.owasp.securityshepherd.service.SubmissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class ScoreboardController {

  private final ScoreService scoringService;

  private final SubmissionService submissionService;

  @GetMapping(path = "scoreboard")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<Scoreboard> scoreboard() {
    return scoringService.getScoreboard();
  }

  @GetMapping(path = "submissions/{userId}")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Flux<Submission> getById(@PathVariable final long userId) {
    return submissionService.findAllValidByUserId(userId);
  }
}
