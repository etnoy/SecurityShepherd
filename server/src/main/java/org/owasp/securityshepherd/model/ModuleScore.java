package org.owasp.securityshepherd.model;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class ModuleScore implements Serializable {

  private static final long serialVersionUID = 4548877736126023113L;

  @Id
  private Integer id;

  private int moduleId;

  @Column("submission_rank")
  private int rank;

  private int score;

}
