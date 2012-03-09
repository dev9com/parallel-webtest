package com.dynacrongroup.webtest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
     * JUnit rule that handles reporting failures and managing WebDriver teardown
     */
    @Rule
    public WebDriverWatcher webDriverWatcher;

    /**
     * Tracks the jobId, if specified, for the job in Sauce Labs.
     */
    private static ThreadLocal<String> jobId = new ThreadLocal<String>();

    /**
     * Stores job pass/fail data for a given parameterized run.
     */
    private static ThreadLocal<WebDriverWatcher> localWatcher = new ThreadLocal<WebDriverWatcher>();

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
     * Used by the JUnit parameterized options to configure the parameterized
     * configuration options.
     */
    public WebDriverBase(String browser, String version) {
        this(browser, version, null);
    }

    /**
     * Alternate parameterized constructor for supplying custom capabilities.
     */
    public WebDriverBase(String browser, String version, Map<String, Object> customCapabilities) {
        this.targetWebBrowser = new TargetWebBrowser(browser, version);
        browserTestLog = LoggerFactory.getLogger(this.getClass()
                .getName() + "-" + this.targetWebBrowser.humanReadable());

        //webDriverWatcher tracks the driver lifecycle and reports on results to sauce labs.
        if (localWatcher.get() == null) {

            driver = WebDriverLauncher.getNewWebDriverInstance(
                    this.getJobName(),
                    browserTestLog,
                    targetWebBrowser,
                    customCapabilities);

            setJobId(WebDriverUtilities.getJobIdFromDriver(driver));

            browserTestLog.debug("WebDriver ready.");
            if (getJobURL() != null) {
                browserTestLog.info("View on SauceLabs at " + getJobURL());
            }

            localWatcher.set(new WebDriverWatcher(this.getClass(), this.driver, this.browserTestLog));
        }
        webDriverWatcher = localWatcher.get();
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
        if (driver == null) {
            driver = webDriverWatcher.getDriver();
        }

        if (timer == null) {
            timer = new Timing(targetWebBrowser, this.getClass()
                    .getSimpleName() + "," + name.getMethodName());
        }

        timer.start();
    }

    @After
    public void stopTimer() {
        timer.stop();
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
     * Returns the SauceLabs job id (if there is one).
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

    private final void setJobId(String id) {
        jobId.set(id);
    }
}
