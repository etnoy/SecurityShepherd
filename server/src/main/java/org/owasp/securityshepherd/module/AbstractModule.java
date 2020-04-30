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

package org.owasp.securityshepherd.module;

import org.owasp.securityshepherd.exception.ModuleNotInitializedException;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractModule implements SubmittableModule {

  protected Long moduleId;

  @Override
  public final long getModuleId() {
    if (this.moduleId == null) {
      throw new ModuleNotInitializedException("Module must be initialized first");
    }
    return this.moduleId;
  }
}
