package com.dynacrongroup.webtest;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * This TestWatcher reports on test progress in Sauce Labs using the JavascriptExecutor.
 *
 * User: yurodivuie
 * Date: 3/9/12
 * Time: 8:52 AM
 *
 *
 */
public class SauceLabsContextReporter extends TestWatcher {

    WebDriver driver;

    public SauceLabsContextReporter(WebDriver driver) {
        this.driver = driver;
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
     * @param message
     */
    void sendContextMessage(String message) {
        if (driver != null && WebDriverUtilities.isExecutedRemotely(driver)) {
            ((JavascriptExecutor) driver).executeScript("sauce:context=// " + message);
        }
    }

}
