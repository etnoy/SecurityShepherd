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

package org.owasp.securityshepherd.module.xss;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.owasp.securityshepherd.exception.XssEvaluationException;
import org.springframework.stereotype.Component;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class XssService {
  private final XssWebClientFactory xssWebClientFactory;

  public List<String> doXss(final String htmlPage) {
    MockWebConnection mockWebConnection = new MockWebConnection();

    mockWebConnection.setDefaultResponse(htmlPage);
    final WebClient webClient = xssWebClientFactory.createWebClient();
    webClient.setWebConnection(mockWebConnection);

    final CollectingAlertHandler alertHandler = xssWebClientFactory.createAlertHandler();
    
    webClient.setAlertHandler(alertHandler);

    HtmlPage page = null;
    webClient.setAjaxController(new NicelyResynchronizingAjaxController());
    try {
      // We make a dummy call to our mocked url
      page = webClient.getPage("http://www.example.com/");
    } catch (FailingHttpStatusCodeException | IOException e) {
      throw new XssEvaluationException(e);
    } finally {
      webClient.close();
      mockWebConnection.close();
    }

    try {
      page.initialize();
      interactWithPage(page);
    } catch (FailingHttpStatusCodeException | IOException e) {
      throw new XssEvaluationException(e);
    }

    webClient.waitForBackgroundJavaScript(1000);

    return alertHandler.getCollectedAlerts();
  }

  private void interactWithPage(final HtmlPage page) throws IOException {
    Iterator<DomElement> domElementIterator = page.getDomElementDescendants().iterator();

    while (domElementIterator.hasNext()) {
      final DomElement domElement = domElementIterator.next();
      if (domElement.isDisplayed()) {
        domElement.click();
        domElement.dblClick();
        domElement.focus();
        domElement.mouseDown();
        domElement.mouseMove();
        domElement.mouseOut();
        domElement.mouseOver();
        domElement.mouseUp();
        domElement.rightClick();
      }
    }
  }
}
