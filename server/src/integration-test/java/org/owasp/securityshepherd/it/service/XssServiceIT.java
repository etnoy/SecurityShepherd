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
package org.owasp.securityshepherd.it.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.owasp.securityshepherd.module.xss.XssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"application.runner.enabled=false"})
@DisplayName("XssService integration test")
class XssServiceIT {

  @Autowired private XssService xssService;

  @Test
  void scriptAlert_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<script>alert('script-xss')</script>")).isEqualTo("script-xss");
  }

  @Test
  void noXss_ShouldNotShowAlert() throws Exception {
    assertThat(executeQuery("")).isNull();
  }

  @Test
  void noXss_PreviousFailure_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("")).isNull();
    // Test for the gotcha that previous versions of xssservice didn't clear between invocations
    assertThat(executeQuery("<script>alert('previousfail')</script>")).isEqualTo("previousfail");
  }

  @Test
  void noXss_PreviousSuccess_ShouldNotShowAlert() throws Exception {
    assertThat(executeQuery("<script>alert('success')</script>")).isEqualTo("success");
    // Test for the gotcha that previous versions of xssservice didn't clear between invocations
    assertThat(executeQuery("")).isNull();
  }

  @Test
  void imgOnLoad_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<img src=\"#\" onload=\"alert('img-onload')\" />"))
        .isEqualTo("img-onload");
  }

  @Test
  void submitButtonOnMouseOver_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<input type=\"submit\" onmouseover=\"alert('submit-mouseover')\"/>"))
        .isEqualTo("submit-mouseover");
  }

  @Test
  void submitButtonOnMouseDown_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<input type=\"submit\" onmousedown=\"alert('submit-mousedown')\"/>"))
        .isEqualTo("submit-mousedown");
  }

  @Test
  void aOnBlur_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<a onblur=alert('a-onblur') tabindex=1 id=x></a><input autofocus>"))
        .isEqualTo("a-onblur");
  }

  @Test
  void submitButtonOnClick_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<input type=\"submit\" onclick=\"alert('submit-onclick')\"/>"))
        .isEqualTo("submit-onclick");
  }

  @Test
  void inputButtonOnClick_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<input type=\"button\" onclick=\"alert('input-onclick')\"/>"))
        .isEqualTo("input-onclick");
  }

  private String executeQuery(final String query) throws IOException {
    final List<String> alerts =
        xssService.doXss(
            "<html><head><title>Alert</title></head><body><p>Result: "
                + query
                + "</p></body></html>");
    if (!alerts.isEmpty()) {
      return alerts.get(0);
    } else {
      return null;
    }
  }
}
