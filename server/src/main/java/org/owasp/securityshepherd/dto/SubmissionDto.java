package org.owasp.securityshepherd.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class SubmissionDto  implements Serializable  {

  private static final long serialVersionUID = 8425777966286079418L;

  @NotNull
  private Integer moduleId;
  
  @NotNull
  private String flag;
}
