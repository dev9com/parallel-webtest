package com.dynacrongroup.webtest;

import com.google.common.annotations.VisibleForTesting;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
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
     * Allows reuse of a browser throughout a single class run.
     */
    private static ThreadLocal<WebDriver> storedWebDriver = new ThreadLocal<WebDriver>();

    /**
     * Tracks the number of test methods to be executed, shutting down the
     * browser instance after the test is done.
     */
    private static ThreadLocal<Integer> methodsRemaining = new ThreadLocal<Integer>();

    /**
     * Tracks the jobUrl, if specified, for the job in Sauce Labs.
     */
    private static ThreadLocal<String> jobUrl = new ThreadLocal<String>();

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
        this.targetWebBrowser = new TargetWebBrowser(browser, version);
        browserTestLog = LoggerFactory.getLogger(this.getClass()
                .getName() + "-" + this.targetWebBrowser.humanReadable());

        if (methodsRemaining.get() == null) {
            methodsRemaining.set(countTestMethods(this.getClass()));
        }

        if (getJobURL() == null) {
            setJobUrl("");
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
            setJobUrl(launcher.getJobUrl());

            browserTestLog.debug("WebDriver ready.");
            if (getJobURL().length() > 1) {
                browserTestLog.info("View on SauceLabs at " + getJobURL());
            }
            storedWebDriver.set(driver);
            WebDriverLeakCheck.add(this.getClass(), driver);
        }

        timer.start();
    }

    @After
    public void noLongerUsingWebDriver() {
        reduceToOneWindow();
        timer.stop();
        methodsRemaining.set(methodsRemaining.get() - 1);
        browserTestLog.trace("Methods left after run: " + methodsRemaining.get());

        if (methodsRemaining.get() == 0 && driver != null) {
            WebDriverLeakCheck.remove(driver);
            driver = null;
            storedWebDriver.set(null);
        }
    }

    public final TargetWebBrowser getTargetWebBrowser() {
        return targetWebBrowser;
    }

    /**
     * Returns the SauceLabs job URL (if there is one).  Constructed dynamically.
     */
    public final String getJobURL() {
        return jobUrl.get();
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

    private final void setJobUrl(String url) {
        jobUrl.set(url);
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
}
