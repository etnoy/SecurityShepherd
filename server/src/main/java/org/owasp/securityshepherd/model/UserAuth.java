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

package org.owasp.securityshepherd.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@Builder
@With
public final class UserAuth implements Serializable {
  
  private static final long serialVersionUID = -1511082836956413928L;

  @Id
  private Long id;

  @NonNull
  private Long userId;

  @JsonProperty("isEnabled")
  private boolean isEnabled;

  private int badLoginCount;

  @JsonProperty("isAdmin")
  private boolean isAdmin;

  private LocalDateTime suspendedUntil;

  private String suspensionMessage;

  private LocalDateTime lastLogin;

  private String lastLoginMethod;
}
