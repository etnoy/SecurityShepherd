package org.owasp.securityshepherd.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@Builder
@With
public class Module implements Serializable {
  private static final long serialVersionUID = 6391362512222766270L;

  @Id
  private Long id;

  @NonNull
  private String name;

  private String description;

  @JsonProperty("isFlagEnabled")
  private boolean isFlagEnabled;

  @JsonProperty("isFlagExact")
  private boolean isFlagExact;

  private String flag;

  private boolean isOpen;

}
