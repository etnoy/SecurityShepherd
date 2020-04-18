package org.owasp.securityshepherd.module.sqlinjection;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder
public class SqlInjectionTutorialRow implements Serializable {

  private static final long serialVersionUID = 6226793239207879891L;

  private String name;

  private String comment;

  private String error;
}
