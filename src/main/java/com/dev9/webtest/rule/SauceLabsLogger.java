package com.dev9.webtest.rule;

import com.dev9.webtest.driver.WebDriverWrapper;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This TestWatcher reports on test progress in Sauce Labs using the JavascriptExecutor.
 * <p/>
 * User: yurodivuie
 * Date: 3/9/12
 * Time: 8:52 AM
 */
public class SauceLabsLogger extends TestWatcher {

    WebDriverWrapper wrapper;

    private static final Logger LOG = LoggerFactory.getLogger(SauceLabsLogger.class);

    public SauceLabsLogger(WebDriverWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    protected void starting(Description description) {
        sendContextMessage(getTestName(description) + " started.");
    }

    @Override
    protected void failed(Throwable e, Description description) {
        sendContextMessage(getTestName(description) + " failed: " + e.getMessage());
    }

    @Override
    protected void succeeded(Description description) {
        sendContextMessage(getTestName(description) + " passed.");
    }

    /**
     * Sends a message to Sauce Labs that will be visible in the logs.
     *
     * @param message
     */
    void sendContextMessage(String message) {
        try {
            ((JavascriptExecutor) wrapper.getDriver()).executeScript("sauce:context=// " + message);
        } catch (WebDriverException exception) {
            LOG.warn("Failed to update sauce labs context: {}", exception.getMessage());
        }
    }

    String getTestName(Description description) {
        return description.getTestClass().getSimpleName() + "." + description.getMethodName();
    }

}
