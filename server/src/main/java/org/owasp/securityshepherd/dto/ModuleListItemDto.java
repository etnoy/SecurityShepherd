package org.owasp.securityshepherd.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ModuleListItemDto implements Serializable {

  private static final long serialVersionUID = -5011105798343266330L;

  @NotNull
  private Long id;
  
  @NotNull
  private String name;

  @NotNull
  private String shortName;
  
  private String description;

  private Boolean isSolved;
}
