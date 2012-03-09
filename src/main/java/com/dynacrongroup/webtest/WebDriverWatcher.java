package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.sauce.SauceREST;
import com.dynacrongroup.webtest.util.CapturingRemoteWebDriver;
import com.google.common.annotations.VisibleForTesting;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import java.lang.reflect.Method;

import static com.dynacrongroup.webtest.WebDriverUtilities.getJobIdFromDriver;

/**
 * User: yurodivuie
 * Date: 3/9/12
 * Time: 8:52 AM
 */
public class WebDriverWatcher extends TestWatcher {

    private static final SauceREST sauceRest = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey());

    WebDriver driver;
    Logger log;
    Integer methodsRemaining;
    Boolean jobPassed;

    public WebDriverWatcher(Class testClass, WebDriver driver, Logger browserTestLog) {
        this.methodsRemaining = countTestMethods(testClass);
        this.driver = driver;
        this.log = browserTestLog;
        this.jobPassed = true;
        WebDriverLeakCheck.add(this.getClass(), driver);
    }

    public WebDriver getDriver() {
        return driver;
    }

    @Override
    protected void starting(Description description) {
        sendContextMessage(description.getMethodName() + " started.");
    }

    @Override
    protected void failed(Throwable e, Description description) {
        sendContextMessage(description.getMethodName() + " failed. " + e.getMessage());
        jobPassed = false;
    }

    @Override
    protected void succeeded(Description description) {
        sendContextMessage(description.getMethodName() + " passed.");
    }

    /**
     * Cleans up drivers after tests and reports on results.  Note that this is executed
     * after the failed/succeeded methods, allowing the watcher to send context updates
     * before destroying the driver.
     */
    @Override
    protected void finished(Description description) {
        methodsRemaining--;
        log.trace("Methods remaining after test finished: [{}]", methodsRemaining);
        reduceToOneWindow();

        // Test class is complete
        if (methodsRemaining == 0 && driver != null) {
            //If this job is running in Sauce Labs, send pass/fail information
            if (isExecutedRemotely()) {
                if (jobPassed) {
                    sauceRest.jobPassed(getJobIdFromDriver(driver));
                }
                else {
                    sauceRest.jobFailed(getJobIdFromDriver(driver));
                }
            }
            WebDriverLeakCheck.remove(driver);
            driver = null;
        }
    }

    /**
     * This method counts the number of test methods. This counter is used to
     * help shut down the browsers when the test is complete.
     */
    @VisibleForTesting
    static int countTestMethods(
            @SuppressWarnings("rawtypes") Class clazz) {
        int count = 0;
        for (Method m : clazz.getMethods()) {
            if ((m.getAnnotation(Test.class) != null)
                    && (m.getAnnotation(Ignore.class) == null)) {
                count++;
            }
        }
        return count;
    }


    void reduceToOneWindow() {
        if (driver != null && driver.getWindowHandles().size() > 1) {
            String firstHandle = (String) driver.getWindowHandles().toArray()[0];
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(firstHandle)) {
                    driver.switchTo().window(handle);
                    driver.close();
                }
            }
            driver.switchTo().window(firstHandle);
        }
    }

    /**
     * Sends a message to Sauce Labs that will be visible in the logs.
     * @param message
     */
    void sendContextMessage(String message) {
        if (driver != null && isExecutedRemotely()) {
            ((JavascriptExecutor) driver).executeScript("sauce:context=// " + message);
        }
    }

    /**
     * Verifies that the driver is for a remote browser.
     * @return
     */
    Boolean isExecutedRemotely() {
        Boolean remote = false;
        if (driver != null && CapturingRemoteWebDriver.class.isAssignableFrom(driver.getClass())) {
            remote = true;
        }
        return remote;
    }
}
