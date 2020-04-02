package org.owasp.securityshepherd.controller;

import org.owasp.securityshepherd.model.Scoreboard;
import org.owasp.securityshepherd.service.ScoringService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class ScoreboardController {

  private final ScoringService scoringService;

  @PostMapping(path = "scoreboard")
  public Flux<Scoreboard> scoreboard() {
    return scoringService.getScoreboard();
  }
}
