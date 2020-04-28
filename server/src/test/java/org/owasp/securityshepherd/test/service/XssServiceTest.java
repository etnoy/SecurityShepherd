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

package org.owasp.securityshepherd.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.securityshepherd.exception.XssEvaluationException;
import org.owasp.securityshepherd.service.XssService;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@ExtendWith(MockitoExtension.class)
@DisplayName("XssService unit test")
public class XssServiceTest {

  private XssService xssService;

  @Mock
  private WebClient webClient;

  @Mock
  private CollectingAlertHandler alertHandler;

  @Test
  public void NoArgsConstructor_ValidData_ConstructsXssService()
      throws FailingHttpStatusCodeException, MalformedURLException, IOException {
    assertDoesNotThrow(() -> new XssService());
  }

  @Test
  public void doXss_GetPageThrowsIOException_ThrowsXssEvaluationException()
      throws FailingHttpStatusCodeException, MalformedURLException, IOException {
    final String htmlPage = "<html></html>";
    when(webClient.getPage(any(String.class))).thenThrow(new IOException());
    assertThrows(XssEvaluationException.class, () -> xssService.doXss(htmlPage));
    verify(webClient, times(1)).getPage(any(String.class));
  }

  @Test
  public void doXss_AlertHandlerFindsAlerts_ReturnsCollectedAlerts() throws Exception {
    final String htmlPage = "<html></html>";
    final HtmlPage mockPage = mock(HtmlPage.class);
    final DomElement mockElement1 = mock(DomElement.class);
    final DomElement mockElement2 = mock(DomElement.class);
    final DomElement mockElement3 = mock(DomElement.class);

    final List<String> alerts = Arrays.asList(new String[] {"XSS", "Hello World"});
    final List<DomElement> mockDomElements =
        Arrays.asList(new DomElement[] {mockElement1, mockElement2, mockElement3});

    when(mockElement1.isDisplayed()).thenReturn(true);
    when(mockElement2.isDisplayed()).thenReturn(false);
    when(mockElement3.isDisplayed()).thenReturn(true);

    when(webClient.getPage(any(String.class))).thenReturn(mockPage);
    when(mockPage.getDomElementDescendants()).thenReturn(mockDomElements);
    when(alertHandler.getCollectedAlerts()).thenReturn(alerts);
    assertThat(xssService.doXss(htmlPage), is(alerts));
    verify(webClient, times(1)).getPage(any(String.class));
    verify(alertHandler, times(1)).getCollectedAlerts();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    xssService = new XssService(webClient, alertHandler);
  }

  @Test
  public void doXss_PageInitializeThrowsIOException_ThrowsXssEvaluationException()
      throws Exception {
    final String htmlPage = "<html></html>";
    final HtmlPage mockPage = mock(HtmlPage.class);
    when(webClient.getPage(any(String.class))).thenReturn(mockPage);
    doThrow(new IOException()).when(mockPage).initialize();
    assertThrows(XssEvaluationException.class, () -> xssService.doXss(htmlPage));
    verify(webClient, times(1)).getPage(any(String.class));
    verify(mockPage, times(1)).initialize();
  }
}