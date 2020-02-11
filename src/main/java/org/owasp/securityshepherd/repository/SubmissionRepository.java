package org.owasp.securityshepherd.repository;

import org.owasp.securityshepherd.model.Submission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends CrudRepository<Submission, Integer> {

}