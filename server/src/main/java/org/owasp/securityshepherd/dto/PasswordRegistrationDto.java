/**
 * This file is part of Security Shepherd.
 *
 * Security Shepherd is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Security Shepherd.
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
