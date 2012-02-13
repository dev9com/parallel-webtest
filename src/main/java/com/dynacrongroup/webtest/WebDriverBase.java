package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.util.SauceREST;
import com.google.common.annotations.VisibleForTesting;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This is the base class for interfacing with WebDriver. It handles the
 * parallel execution of single or multiple browsers, running locally or in
 * SauceLabs. By using this test as a base class, you can create tests via the
 * Selenium recorder and/or by hand.
 */
public class WebDriverBase {

    /**
     * The main WebDriver interface.
     */
    public WebDriver driver = null;

    /**
     * This is a special bit of JUnit magic to get the name of the test
     */
    @Rule
    public TestName name = new TestName();

    /**
     * Transmits job pass/fail data.
     */
    private static final SauceREST sauceRest = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey());

    /**
     * Allows reuse of a browser throughout a single class run.
     */
    private static ThreadLocal<WebDriver> storedWebDriver = new ThreadLocal<WebDriver>();

    /**
     * Tracks the number of test methods to be executed, shutting down the
     * browser instance after the test is done.
     */
    private static ThreadLocal<Integer> methodsRemaining = new ThreadLocal<Integer>();

    /**
     * Tracks the jobId, if specified, for the job in Sauce Labs.
     */
    private static ThreadLocal<String> jobId = new ThreadLocal<String>();

    /**
     * Stores job pass/fail data for a given parameterized run.
     */
    private static ThreadLocal<Boolean> jobPassed = new ThreadLocal<Boolean>();

    /**
     * The logger associated with this specific browser test execution
     */
    private final Logger browserTestLog;

    /**
     * This is the target browser/version for this test. The values are a bit
     * different with Selenium 2/ WebDriver. See the WebDriverFactory and
     * WebDriverLauncher for more details.
     */
    private final TargetWebBrowser targetWebBrowser;

    /**
     * Used to track the timing for output to webdriver-timings.csv
     */
    private Timing timer;

    /**
     * JUnit rule that handles reporting failures and tear down after tests are complete.
     */
    @Rule
    public TestWatcher webDriverWatcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description d) {
            sendContextMessage("failed. " + e.getMessage());
            jobPassed.set(false);
        }

        @Override
        protected void succeeded(Description description) {
            sendContextMessage("passed.");
        }

        /**
         * Cleans up drivers after tests and reports on results.  Note that this is executed
         * after the failed/succeeded methods, allowing the watcher to send context updates
         * before destroying the driver.
         */
        @Override
        protected void finished(Description description) {
            reduceToOneWindow();
            timer.stop();
            methodsRemaining.set(methodsRemaining.get() - 1);
            browserTestLog.trace("Methods left after run: " + methodsRemaining.get());

            // Test class is complete
            if (methodsRemaining.get() == 0 && driver != null) {
                //If this job is running in Sauce Labs, send pass/fail information
                if (!targetWebBrowser.isClassLoaded()) {
                    if (jobPassed.get()) {
                        sauceRest.jobPassed(jobId.get());
                    }
                    else {
                        sauceRest.jobFailed(jobId.get());
                    }
                }
                WebDriverLeakCheck.remove(driver);
                driver = null;
                storedWebDriver.set(null);
            }
        }
    };



    /**
     * Used by the JUnit parameterized options to configure the parameterized
     * configuration options.
     */
    public WebDriverBase(String browser, String version) {
        this.targetWebBrowser = new TargetWebBrowser(browser, version);
        browserTestLog = LoggerFactory.getLogger(this.getClass()
                .getName() + "-" + this.targetWebBrowser.humanReadable());

        if (methodsRemaining.get() == null) {
            methodsRemaining.set(countTestMethods(this.getClass()));
        }

        if (jobPassed.get() == null) {
            jobPassed.set(true);
        }
    }

    /**
     * Feeds in the list of target browsers. This might be a single local
     * browser, HTMLUnit, or one or more remote SauceLabs instances.
     *
     * @see WebDriverFactory
     */
    @DescriptivelyParameterized.Parameters
    public static List<String[]> configureWebDriverTargets() throws IOException {
        return new WebDriverFactory().getDriverTargets();
    }

    /**
     * Starts up the WebDriver instance. Uses a combination of ThreadLocals and
     * other things to ensure that one browser instance is maintained per test.
     */
    @Before
    public void startWebDriver() {

        if (timer == null) {
            timer = new Timing(targetWebBrowser, this.getClass()
                    .getSimpleName() + "," + name.getMethodName());
        }

        if (storedWebDriver.get() != null) {
            browserTestLog.trace("Using existing threadLocal browser.");
            driver = storedWebDriver.get();
        } else {
            // Launches new WebDriver instance
            WebDriverLauncher launcher = new WebDriverLauncher();
            driver = launcher.getNewWebDriverInstance(this.getJobName(),
                    this.browserTestLog, targetWebBrowser);
            setJobId(WebDriverUtilities.getJobIdFromDriver(driver));

            browserTestLog.debug("WebDriver ready.");
            if (getJobURL() != null) {
                browserTestLog.info("View on SauceLabs at " + getJobURL());
            }
            storedWebDriver.set(driver);
            WebDriverLeakCheck.add(this.getClass(), driver);
        }
        sendContextMessage("started.");

        timer.start();
    }

    public final TargetWebBrowser getTargetWebBrowser() {
        return targetWebBrowser;
    }

    /**
     * Returns the SauceLabs job URL (if there is one).  Constructed dynamically.
     */
    public final String getJobURL() {
        String jobUrl = null;

        if (getJobId() != null && !this.targetWebBrowser.isClassLoaded()) {
            jobUrl = WebDriverUtilities.constructSauceJobUrl(jobId.get());
        }

        return jobUrl;
    }

    /**
     * Returns the SauceLabs job URL (if there is one).  Constructed dynamically.
     */
    public final String getJobId() {
        return jobId.get();
    }

    /**
     * The name of the job, as reported to SauceLabs. Includes the user name and
     * the name of the class.
     */
    public final String getJobName() {
        return SystemName.getSystemName() + "-"
                + this.getClass().getSimpleName();
    }

    /**
     * Simple utility method - sleeps for the specified number of milliseconds.
     */
    public void pause(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
            browserTestLog.debug("Thread sleep interrupted", e);
        }
    }

    /**
     * This is the logger you should use if you want per-browser instance piping
     * to work correctly. When you run tests in parallel, if you just use a
     * local logger, all of the parallel executions will get intermixed.
     */
    public Logger getLogger() {
        return browserTestLog;
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

    private final void setJobId(String id) {
        jobId.set(id);
    }

    private void reduceToOneWindow() {
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

    private void sendContextMessage(String message) {
        if (driver != null && !targetWebBrowser.isClassLoaded()) {
            ((JavascriptExecutor) driver).executeScript("sauce:context=// " + name.getMethodName() + " " + message);
        }
    }
}
