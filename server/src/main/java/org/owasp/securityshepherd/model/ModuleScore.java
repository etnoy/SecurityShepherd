package org.owasp.securityshepherd.model;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class ModuleScore implements Serializable {

  private static final long serialVersionUID = -3672798577756177047L;

  @Id
  private Integer id;

  @NonNull
  private Integer moduleId;
  
  @NonNull
  private Integer userId;

  @NonNull
  private Integer points;
}
