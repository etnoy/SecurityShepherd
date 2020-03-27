package org.owasp.securityshepherd.service;

import java.util.ArrayList;
import java.util.List;
import org.owasp.securityshepherd.exception.InvalidUserIdException;
import org.owasp.securityshepherd.model.Module;
import org.owasp.securityshepherd.model.Submission;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public final class ScoringService {

  private final ModuleService moduleService;

  private final SubmissionService submissionService;

  public Mono<Integer> computeScore(final int userId) {

    if (userId <= 0) {

      return Mono.error(new InvalidUserIdException());

    }

    final Flux<Module> modules = moduleService.findAll();

    final Mono<List<Integer>> moduleIds = modules.map(module -> module.getId()).collectList();

    // Get a list per module
    final Mono<List<Mono<List<Submission>>>> listOfSubmissions = moduleIds.map(idList -> {
      List<Mono<List<Submission>>> submissionList = new ArrayList<>();
      for (final int id : idList) {
        submissionList.add(submissionService.findAllByModuleId(id).collectList());
      }
      return submissionList;
    });

 

    return Mono.just(3);

  }

}
