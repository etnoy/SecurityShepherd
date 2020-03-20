package org.owasp.securityshepherd.web.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.owasp.securityshepherd.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class PasswordUserRegistrationDto implements Serializable {

  private static final long serialVersionUID = 8155074795022450359L;

  @NotNull
  @Size(min = 1, message = "{Size.userDto.displayName}")
  private String displayName;

  @NotNull
  @Size(min = 1, message = "{Size.userDto.loginName}")
  private String loginName;

  @ValidPassword
  @NotNull
  private String password;

}
