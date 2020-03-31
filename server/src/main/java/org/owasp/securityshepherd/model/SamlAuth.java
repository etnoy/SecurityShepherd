package org.owasp.securityshepherd.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;
import lombok.NonNull;
import lombok.With;

@Value
@Builder
@With
public class SamlAuth implements Serializable {
  private static final long serialVersionUID = 211951930649985921L;

  @Id
  private Integer id;

  @NonNull
  private Integer userId;

  @NonNull
  private String samlId;
}
