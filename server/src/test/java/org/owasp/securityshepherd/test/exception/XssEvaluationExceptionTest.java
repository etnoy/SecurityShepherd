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

package org.owasp.securityshepherd.test.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.exception.XssEvaluationException;
import org.owasp.securityshepherd.test.util.TestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("XssEvaluationException unit test")
public class XssEvaluationExceptionTest {
  @Test
  void noArgsConstructor_NoArguments_ReturnsException() {
    assertThat(new XssEvaluationException(), is(instanceOf(XssEvaluationException.class)));
  }

  @Test
  void messageConstructor_ValidMessage_MessageIncluded() {
    for (final String message : TestUtils.STRINGS) {
      XssEvaluationException exception = new XssEvaluationException(message);
      assertThat(exception.getMessage(), is(message));
    }
  }

  @Test
  void messageExceptionConstructor_ValidMessageAndException_MessageIncluded() {
    for (final String message : TestUtils.STRINGS) {
      XssEvaluationException exception =
          new XssEvaluationException(message, new RuntimeException());
      assertThat(exception.getMessage(), is(message));
      assertThat(exception.getCause(), is(instanceOf(RuntimeException.class)));
    }
  }

  @Test
  void exceptionConstructor_ValidException_MessageIncluded() {
    XssEvaluationException exception = new XssEvaluationException(new RuntimeException());
    assertThat(exception.getCause(), is(instanceOf(RuntimeException.class)));
  }
}
