package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.sauce.SauceREST;
import com.google.common.annotations.VisibleForTesting;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import java.lang.reflect.Method;

import static com.dynacrongroup.webtest.WebDriverUtilities.getJobIdFromDriver;

/**
 * User: yurodivuie
 * Date: 3/9/12
 * Time: 8:52 AM
 *
 * This TestWatcher extension manages the driver and reports pass/fail when the test completes.
 */
public class WebDriverManager extends TestWatcher {

    private static final SauceREST sauceRest = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey());

    WebDriver driver;
    Logger log;
    Integer methodsRemaining;

    // The job is assumed to have passed until proven otherwise.
    Boolean jobPassed = true;

    public WebDriverManager(Class testClass, WebDriver driver, Logger browserTestLog) {
        this.methodsRemaining = countTestMethods(testClass);
        this.driver = driver;
        this.log = browserTestLog;
        WebDriverLeakCheck.add(testClass, driver);
    }

    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Record test failure for final report to Sauce Labs, if applicable.
     *
     * @param throwable     not used.
     * @param description   not used.
     */
    @Override
    protected void failed(Throwable throwable, Description description) {
        jobPassed = false;
    }

    /**
     * Cleans up drivers after tests and reports on results.  Note that this is executed
     * after the failed/succeeded methods, allowing the manager to send a final update for the job
     * before destroying the driver.
     */
    @Override
    protected void finished(Description description) {
        methodsRemaining--;
        log.trace("Methods remaining after test finished: [{}]", methodsRemaining);
        WebDriverUtilities.reduceToOneWindow(driver);

        // Test class is complete
        if (methodsRemaining == 0 && driver != null) {
            //If this job is running in Sauce Labs, send pass/fail information
            reportFinalStatus(driver, jobPassed);
            WebDriverLeakCheck.remove(driver);
            driver = null;
        }
    }

    static void reportFinalStatus(WebDriver driver, Boolean jobPassed) {
        if (WebDriverUtilities.isExecutedRemotely(driver)) {
            String jobId = getJobIdFromDriver(driver);
            if (jobPassed) {
                sauceRest.jobPassed(jobId);
            }
            else {
                sauceRest.jobFailed(jobId);
            }
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
}
