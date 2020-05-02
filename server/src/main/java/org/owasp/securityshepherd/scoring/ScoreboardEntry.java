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

package org.owasp.securityshepherd.scoring;

import java.io.Serializable;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
@Table("scoreboard")
public class ScoreboardEntry implements Serializable {
  private static final long serialVersionUID = 902640084501001329L;

  @NonNull
  private Long rank;
  
  @NonNull
  private Long userId;

  @NonNull
  private Long score;
  
  @NonNull
  private Long goldMedals;
  
  @NonNull
  private Long silverMedals;
  
  @NonNull
  private Long bronzeMedals;
}