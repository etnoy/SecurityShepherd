package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.Submission;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends ReactiveCrudRepository<Submission, Integer> {

}
