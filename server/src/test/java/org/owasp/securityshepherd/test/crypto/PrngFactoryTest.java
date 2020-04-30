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

package org.owasp.securityshepherd.test.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import java.security.SecureRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.crypto.PrngFactory;
import reactor.core.publisher.Hooks;

@ExtendWith(MockitoExtension.class)
@DisplayName("PrngFactory unit test")
public class PrngFactoryTest {
  @BeforeAll
  private static void reactorVerbose() {
    // Tell Reactor to print verbose error messages
    Hooks.onOperatorDebug();
  }

  PrngFactory prngFactory;

  @Test
  public void getPrng_ReturnsSecureRandomInstance() throws Exception {
    assertThat(prngFactory.getPrng()).isInstanceOf(SecureRandom.class);
  }

  @BeforeEach
  private void setUp() {
    prngFactory = new PrngFactory();
  }
}
