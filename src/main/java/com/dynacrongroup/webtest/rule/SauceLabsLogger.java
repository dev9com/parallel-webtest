package com.dynacrongroup.webtest.rule;

import com.dynacrongroup.webtest.driver.WebDriverWrapper;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.JavascriptExecutor;

/**
 * This TestWatcher reports on test progress in Sauce Labs using the JavascriptExecutor.
 * <p/>
 * User: yurodivuie
 * Date: 3/9/12
 * Time: 8:52 AM
 */
public class SauceLabsLogger extends TestWatcher {

    WebDriverWrapper wrapper;

    public SauceLabsLogger(WebDriverWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    protected void starting(Description description) {
        sendContextMessage(description.getMethodName() + " started.");
    }

    @Override
    protected void failed(Throwable e, Description description) {
        sendContextMessage(description.getMethodName() + " failed. " + e.getMessage());
    }

    @Override
    protected void succeeded(Description description) {
        sendContextMessage(description.getMethodName() + " passed.");
    }

    /**
     * Sends a message to Sauce Labs that will be visible in the logs.
     *
     * @param message
     */
    void sendContextMessage(String message) {
        ((JavascriptExecutor) wrapper.getDriver()).executeScript("sauce:context=// " + message);
    }

}
