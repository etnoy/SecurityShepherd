package org.owasp.securityshepherd.persistence.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class Module implements Serializable {

  private static final long serialVersionUID = 6391362512222766270L;

  @Id
  private int id;

  @NonNull
  private String name;

  private String description;

  private boolean isFlagEnabled;

  private boolean isFlagExact;

  private String flag;

  private boolean isOpen;

}
