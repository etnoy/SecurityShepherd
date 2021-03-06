/*
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
package org.owasp.securityshepherd.scoring;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class UserScoreDto implements Serializable {
  private static final long serialVersionUID = 8494108113674100571L;

  @NonNull private Long submissionId;

  @NonNull private String moduleName;

  @NonNull private Long rank;

  @NonNull private Long score;

  @NonNull private Long bonus;

  @NonNull private LocalDateTime time;

  @JsonProperty("isValid")
  @NonNull
  private Boolean isValid;

  @NonNull private String flag;
}
