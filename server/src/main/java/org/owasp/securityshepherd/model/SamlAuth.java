package org.owasp.securityshepherd.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.With;

@Value
@EqualsAndHashCode
@Builder
@With
public class SamlAuth implements Serializable {

  private static final long serialVersionUID = 211951930649985921L;

  @Id
  private int id;

  private int user;

  @NonNull
  private String samlId;

  public SamlAuth(final int id, final int user, @NonNull final String samlId) {
    this.id = id;
    this.user = user;
    this.samlId = samlId;
  }

}
