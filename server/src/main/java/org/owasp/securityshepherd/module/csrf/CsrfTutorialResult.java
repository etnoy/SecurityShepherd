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

package org.owasp.securityshepherd.module.csrf;

import java.io.Serializable;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CsrfTutorialResult implements Serializable {

	private static final long serialVersionUID = 6619669574336403439L;

	private String name;

	private String comment;

	private String error;

	CsrfTutorialResult(final String name, final String comment, final String error) {
		if (name == null && comment == null && error == null) {
			throw new NullPointerException("Name, comment, and error can't all be null");
		}
		this.name = name;
		this.comment = comment;
		this.error = error;
	}
}
