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
package org.owasp.securityshepherd.module.csrf;

import lombok.RequiredArgsConstructor;
import org.owasp.securityshepherd.authentication.ControllerAuthentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/module/csrf-tutorial")
public class CsrfTutorialController {
  private final CsrfTutorial csrfTutorial;

  private final ControllerAuthentication controllerAuthentication;

  @GetMapping(path = "/")
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<CsrfTutorialResult> tutorial() {
    return controllerAuthentication.getUserId().flatMap(csrfTutorial::getTutorial);
  }

  @GetMapping(value = {"/activate/{pseudonym}"})
  @PreAuthorize("hasRole('ROLE_USER')")
  public Mono<CsrfTutorialResult> attack(@PathVariable(value = "pseudonym") String pseudonym) {
    return controllerAuthentication
        .getUserId()
        .flatMap(userId -> csrfTutorial.attack(userId, pseudonym));
  }
}
