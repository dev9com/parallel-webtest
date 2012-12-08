package com.dynacrongroup.webtest.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ScrollingRemoteWebElement extends RemoteWebElement {

    private static final Logger LOG = LoggerFactory.getLogger(ScrollingRemoteWebElement.class);
    private static final String VISIBILITY_EXCEPTION = "Element is not clickable at point";

    /**
     * Initialize from a given RemoteWebElement
     *
     * @param initializingElement a given RemoteWebElement which is essentially copied.
     */
    public ScrollingRemoteWebElement(RemoteWebElement initializingElement) {
        super();

        this.setId(initializingElement.getId());
        this.setParent((RemoteWebDriver) initializingElement.getWrappedDriver());
        this.setFileDetector(parent.getFileDetector());
    }

    public static List<WebElement> convert(List<WebElement> elements) {
        List<WebElement> scrollingElements = new ArrayList<WebElement>();
        for (WebElement element: elements) {
            scrollingElements.add(convert(element));
        }
        return scrollingElements;
    }

    public static WebElement convert(WebElement element) {
        if (element instanceof RemoteWebElement) {
            element = new ScrollingRemoteWebElement((RemoteWebElement)element);
        }
        return element;
    }

    @Override
    protected Response execute(String command, Map<String, ?> parameters) {
        Response response;
        try {
            response = super.execute(command, parameters);
        } catch (WebDriverException webDriverException) {
            if (isVisibilityProblem(webDriverException)) {
                scrollToElement();
                response = super.execute(command, parameters);
            } else {
                throw webDriverException;
            }
        }
        return response;
    }

    @Override
    public List<WebElement> findElements(By by) {
        return ScrollingRemoteWebElement.convert(super.findElements(by));
    }

    @Override
    public WebElement findElement(By by) {
        return ScrollingRemoteWebElement.convert(super.findElement(by));
    }

    @Override
    public WebElement findElement(String by, String using) {
        return ScrollingRemoteWebElement.convert(super.findElement(by, using));
    }

    @Override
    public List<WebElement> findElements(String by, String using) {
        return ScrollingRemoteWebElement.convert(super.findElements(by, using));
    }

    private boolean isVisibilityProblem(WebDriverException webDriverException) {
        return (webDriverException.getMessage() != null
                && webDriverException.getMessage().contains(VISIBILITY_EXCEPTION));
    }

    private void scrollToElement() {
        Integer scrollHeight = getHeightPlacingElementInPageCenter();

        String script = "window.scrollTo(0," + scrollHeight + ")";
        LOG.debug("Attempting recovery from failure to scroll to element: {}", script);
        parent.executeScript(script);
    }

    private Integer getHeightPlacingElementInPageCenter() {
        Dimension size = parent.manage().window().getSize();

        Integer halfWindow = size.getHeight() / 2;
        Integer scrollHeight = this.getLocation().getY() - halfWindow;
        if (scrollHeight < 0) {
            scrollHeight = 0;
        }
        return scrollHeight;
    }
}
