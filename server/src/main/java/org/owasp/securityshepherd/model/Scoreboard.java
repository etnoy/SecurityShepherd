package org.owasp.securityshepherd.model;

import java.io.Serializable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class Scoreboard implements Serializable {

  private static final long serialVersionUID = 902640084501001329L;

  @NonNull
  private Long rank;
  
  @NonNull
  private Long userId;

  @NonNull
  private Long score;
}
