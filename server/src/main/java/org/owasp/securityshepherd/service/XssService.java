package org.owasp.securityshepherd.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.owasp.securityshepherd.exception.XssEvaluationException;
import org.springframework.stereotype.Component;
import com.gargoylesoftware.htmlunit.BrowserVersion;
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

  public List<String> doXssWithBrowserVersion(final String htmlPage,
      final BrowserVersion browserVersion) {
    WebClient webClient = new WebClient(browserVersion);
    MockWebConnection mockWebConnection = new MockWebConnection();
    final CollectingAlertHandler alertHandler = new CollectingAlertHandler();

    mockWebConnection.setDefaultResponse(htmlPage);
    webClient.setWebConnection(mockWebConnection);
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
      interactWithElements(page.getDomElementDescendants());
    } catch (FailingHttpStatusCodeException | IOException e) {
      throw new XssEvaluationException(e);
    }

    webClient.waitForBackgroundJavaScript(1000);

    return alertHandler.getCollectedAlerts();
  }

  public String doXss(final String htmlPage) {
    final BrowserVersion[] browserVersions = {BrowserVersion.FIREFOX, BrowserVersion.CHROME,
        BrowserVersion.INTERNET_EXPLORER, BrowserVersion.BEST_SUPPORTED};

    for (final BrowserVersion browserVersion : browserVersions) {
      final List<String> alertList = doXssWithBrowserVersion(htmlPage, browserVersion);
      if (!alertList.isEmpty()) {
        return alertList.get(0);
      }
    }
    return null;
  }

  private <T extends DomElement> void interactWithElements(Iterable<T> domElements)
      throws IOException {
    Iterator<T> domElementIterator = domElements.iterator();

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
