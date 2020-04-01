package org.owasp.securityshepherd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public final class DatabaseService {
  @Autowired
  UserService userService;

  @Autowired
  ClassService classService;

  @Autowired
  ModuleService moduleService;

  @Autowired
  SubmissionService submissionService;

  @Autowired
  ScoringService scoringService;

  public Mono<Void> clearAll() {
    // Clear all users and modules from repository before every test
    return scoringService.deleteAll().then(submissionService.deleteAll())
        .then(classService.deleteAll()).then(moduleService.deleteAll())
        .then(userService.deleteAll());
  }
}
