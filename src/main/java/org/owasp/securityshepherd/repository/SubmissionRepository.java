package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.Submission;
import org.springframework.data.repository.CrudRepository;

public interface SubmissionRepository extends CrudRepository<Submission, Long> {

}