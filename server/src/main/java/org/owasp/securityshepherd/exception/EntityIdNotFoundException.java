package org.owasp.securityshepherd.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class EntityIdNotFoundException extends EntityIdException {

  /**
   * 
   */
  private static final long serialVersionUID = 1988282591649679197L;

  public EntityIdNotFoundException(final String message) {
    super(message);
  }

  public EntityIdNotFoundException(final String message, final Exception e) {
    super(message, e);
  }


}
