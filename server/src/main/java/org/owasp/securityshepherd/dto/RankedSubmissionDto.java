package org.owasp.securityshepherd.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RankedSubmissionDto  implements Serializable  {

  private static final long serialVersionUID = -9193325182487383605L;

  @NotNull
  private Integer userId;
  
  @NotNull
  private Integer rank;
  
  @NotNull
  private LocalDateTime time;
}
