package org.owasp.securityshepherd.model;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class ModulePoints implements Serializable {
  private static final long serialVersionUID = 4548877736126023113L;

  @Id
  private Integer id;

  @NonNull
  private Integer moduleId;

  @Column("submission_rank")
  @NonNull
  private Integer rank;

  @NonNull
  private Integer points;
}
