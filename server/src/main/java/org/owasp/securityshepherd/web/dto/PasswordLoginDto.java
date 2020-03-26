package org.owasp.securityshepherd.web.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class PasswordLoginDto  implements Serializable  {

  private static final long serialVersionUID = 225588142559080211L;

  @NotNull
  @Size(min = 1, message = "{Size.passwordLoginDto.userName}")
  private String userName;

  @NotNull
  @Size(min = 1, message = "{Size.passwordLoginDto.password}")
  private String password;
}
