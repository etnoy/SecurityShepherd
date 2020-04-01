package org.owasp.securityshepherd.model;

import java.io.Serializable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Scoreboard implements Serializable {

  private static final long serialVersionUID = 902640084501001329L;

  @NonNull
  private Integer rank;
  
  @NonNull
  private Integer userId;

  @NonNull
  private Long score;
}
