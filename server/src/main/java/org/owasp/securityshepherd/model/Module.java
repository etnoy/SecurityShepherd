package org.owasp.securityshepherd.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
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
  
  @java.beans.ConstructorProperties({"id", "name", "description", "isFlagEnabled", "isFlagExact", "flag", "isOpen"})
  @java.lang.SuppressWarnings("all")
  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(justification = "generated code")
  Module(final Long id, @NonNull final String name, final String description, @JsonProperty("isFlagEnabled") final boolean isFlagEnabled, @JsonProperty("isFlagExact") final boolean isFlagExact, final String flag, final boolean isOpen) {
    if (name == null) {
      throw new java.lang.NullPointerException("name is marked non-null but is null");
    }
    this.id = id;
    this.name = name;
    this.description = description;
    this.isFlagEnabled = isFlagEnabled;
    this.isFlagExact = isFlagExact;
    this.flag = flag;
    this.isOpen = isOpen;
  }

}
