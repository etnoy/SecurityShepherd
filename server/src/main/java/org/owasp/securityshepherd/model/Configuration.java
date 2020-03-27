package org.owasp.securityshepherd.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class Configuration implements Serializable {

  private static final long serialVersionUID = -3877275355721047824L;

  @Id
  private Integer id;

  @NonNull
  @Column("config_key")
  private String key;

  @NonNull
  private String value;

  Configuration(final Integer id, @NonNull final String key, final String value) {
    this.id = id;
    this.key = key;
    this.value = value;
  }

}
