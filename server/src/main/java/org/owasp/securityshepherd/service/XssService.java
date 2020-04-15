package org.owasp.securityshepherd.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
      final BrowserVersion browserVersion) throws IOException {
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
      throw new RuntimeException(e);
    } finally {
      webClient.close();
      mockWebConnection.close();
    }
    page.initialize();

    interactWithElements(page.getDomElementDescendants());

    webClient.waitForBackgroundJavaScript(60000);

    return alertHandler.getCollectedAlerts();
  }

  public List<String> doXss(final String htmlPage) throws IOException {
    final BrowserVersion[] browserVersions = {BrowserVersion.FIREFOX, BrowserVersion.CHROME,
        BrowserVersion.INTERNET_EXPLORER, BrowserVersion.BEST_SUPPORTED};

    for (final BrowserVersion browserVersion : browserVersions) {
      final List<String> alertList = doXssWithBrowserVersion(htmlPage, browserVersion);
      if (!alertList.isEmpty()) {
        return alertList;
      }
    }
    return new ArrayList<String>();
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
