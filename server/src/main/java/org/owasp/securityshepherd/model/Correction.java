package org.owasp.securityshepherd.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class Correction implements Serializable {

  private static final long serialVersionUID = -3672798577756177047L;

  @Id
  private Integer id;

  @NonNull
  private Integer userId;

  @NonNull
  private Integer amount;
  
  @NonNull
  private LocalDateTime time;
  
  private String description;
}
