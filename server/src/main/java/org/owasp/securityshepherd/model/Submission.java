package org.owasp.securityshepherd.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;
import lombok.NonNull;
import lombok.With;

@Value
@Builder
@With
public class Submission implements Serializable {
  private static final long serialVersionUID = -5485881248601955741L;

  @Id
  private Long id;

  @NonNull
  private Long userId;

  @NonNull
  private Long moduleId;

  @NonNull
  private LocalDateTime time;

  @NonNull
  private Boolean isValid;

  @NonNull
  private String flag;
}
