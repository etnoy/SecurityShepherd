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
package org.owasp.securityshepherd.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
import org.owasp.securityshepherd.module.xss.XssService;
import org.owasp.securityshepherd.module.xss.XssWebClientFactory;

@ExtendWith(MockitoExtension.class)
@DisplayName("XssService unit test")
class XssServiceTest {

  XssService xssService;

  @Mock XssWebClientFactory xssWebClientFactory;

  @Test
  void doXss_AlertHandlerFindsAlerts_ReturnsCollectedAlerts() throws Exception {
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

    final WebClient mockWebClient = mock(WebClient.class);
    final CollectingAlertHandler mockAlertHandler = mock(CollectingAlertHandler.class);
    when(xssWebClientFactory.createWebClient()).thenReturn(mockWebClient);
    when(xssWebClientFactory.createAlertHandler()).thenReturn(mockAlertHandler);

    when(mockWebClient.getPage(any(String.class))).thenReturn(mockPage);
    when(mockPage.getDomElementDescendants()).thenReturn(mockDomElements);
    when(mockAlertHandler.getCollectedAlerts()).thenReturn(alerts);
    assertThat(xssService.doXss(htmlPage)).isEqualTo(alerts);
    verify(mockWebClient, times(1)).getPage(any(String.class));
    verify(mockAlertHandler, times(1)).getCollectedAlerts();
  }

  @Test
  void doXss_GetPageThrowsIOException_ThrowsXssEvaluationException()
      throws FailingHttpStatusCodeException, MalformedURLException, IOException {
    final String htmlPage = "<html></html>";
    final WebClient mockWebClient = mock(WebClient.class);
    final CollectingAlertHandler mockAlertHandler = mock(CollectingAlertHandler.class);
    when(xssWebClientFactory.createWebClient()).thenReturn(mockWebClient);
    when(xssWebClientFactory.createAlertHandler()).thenReturn(mockAlertHandler);
    when(mockWebClient.getPage(any(String.class))).thenThrow(new IOException());
    assertThrows(XssEvaluationException.class, () -> xssService.doXss(htmlPage));
    verify(mockWebClient, times(1)).getPage(any(String.class));
  }

  @Test
  void doXss_PageInitializeThrowsIOException_ThrowsXssEvaluationException() throws Exception {
    final String htmlPage = "<html></html>";
    final HtmlPage mockPage = mock(HtmlPage.class);
    final WebClient mockWebClient = mock(WebClient.class);
    final CollectingAlertHandler mockAlertHandler = mock(CollectingAlertHandler.class);
    when(xssWebClientFactory.createWebClient()).thenReturn(mockWebClient);
    when(xssWebClientFactory.createAlertHandler()).thenReturn(mockAlertHandler);

    when(mockWebClient.getPage(any(String.class))).thenReturn(mockPage);
    doThrow(new IOException()).when(mockPage).initialize();
    assertThrows(XssEvaluationException.class, () -> xssService.doXss(htmlPage));
    verify(mockWebClient, times(1)).getPage(any(String.class));
    verify(mockPage, times(1)).initialize();
  }

  @BeforeEach
  private void setUp() {
    // Set up the system under test
    xssService = new XssService(xssWebClientFactory);
  }
}
