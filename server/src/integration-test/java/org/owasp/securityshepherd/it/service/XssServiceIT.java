package org.owasp.securityshepherd.it.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.owasp.securityshepherd.service.XssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Execution(ExecutionMode.SAME_THREAD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("XssService integration test")
public class XssServiceIT {

  @Autowired
  private XssService xssService;

  @Test
  public void scriptAlert_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<script>alert('XSS')</script>"), is("XSS"));
  }

  @Test
  public void imgOnLoad_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<img src=\"#\" onload=\"alert('XSS')\" />"), is("XSS"));
  }

  @Test
  public void submitButtonOnMouseOver_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<input type=\"submit\" onmouseover=\"alert('XSS')\"/>"), is("XSS"));
  }

  @Test
  public void submitButtonOnMouseDown_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<input type=\"submit\" onmousedown=\"alert('XSS')\"/>"), is("XSS"));
  }

  @Test
  public void aOnBlur_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<a onblur=alert('XSS') tabindex=1 id=x></a><input autofocus>"),
        is("XSS"));
  }

  @Test
  public void submitButtonOnclick_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<input type=\"submit\" onclick=\"alert('XSS')\"/>"), is("XSS"));
  }

  @Test
  public void inputButtonOnclick_ShouldShowAlert() throws Exception {
    assertThat(executeQuery("<input type=\"button\" onclick=\"alert('XSS')\"/>"), is("XSS"));
  }

  private String executeQuery(final String query) throws IOException {
    return xssService.doXss(
        "<html><head><title>Alert</title></head><body><p>Result: " + query + "</p></body></html>");
  }
}
