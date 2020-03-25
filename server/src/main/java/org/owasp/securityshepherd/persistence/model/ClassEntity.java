package org.owasp.securityshepherd.persistence.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Table("class")
@Builder(builderClassName = "ClassBuilder")
@With
public class ClassEntity implements Serializable {

  private static final long serialVersionUID = 7375094814834027958L;

  @Id
  private final int id;

  @NonNull
  private final String name;

  ClassEntity(final int id, final String name) {
    this.id = id;
    this.name = name;
  }

}
