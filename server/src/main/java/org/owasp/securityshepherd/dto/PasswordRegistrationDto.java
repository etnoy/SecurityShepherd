package org.owasp.securityshepherd.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.owasp.securityshepherd.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class PasswordRegistrationDto implements Serializable {

  private static final long serialVersionUID = 8155074795022450359L;

  @NotNull
  @Size(min = 1, message = "{Size.userDto.displayName}")
  private String displayName;

  @NotNull
  @Size(min = 1, message = "{Size.userDto.userName}")
  private String userName;

  @ValidPassword
  @NotNull
  private String password;

}
