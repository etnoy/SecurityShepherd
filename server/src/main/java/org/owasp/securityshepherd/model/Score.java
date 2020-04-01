package org.owasp.securityshepherd.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class Score implements Serializable {

  private static final long serialVersionUID = -3672798577756177047L;

  @Id
  private Integer id;

  @NonNull
  private Integer moduleId;
  
  @NonNull
  private Integer userId;

  @NonNull
  private Integer score;
  
  @NonNull
  @Column("user_rank")
  private Integer rank;
  
  @NonNull
  private LocalDateTime time;
}
