/**
 * This file is part of Security Shepherd.
 *
 * <p>Security Shepherd is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>Security Shepherd is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with Security
 * Shepherd. If not, see <http://www.gnu.org/licenses/>.
 */
package org.owasp.securityshepherd.authentication;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;
import lombok.NonNull;
import lombok.With;

@Value
@Builder
@With
public class SamlAuth implements Serializable {
  private static final long serialVersionUID = 211951930649985921L;

  @Id private Integer id;

  @NonNull private Integer userId;

  @NonNull private String samlId;
}
