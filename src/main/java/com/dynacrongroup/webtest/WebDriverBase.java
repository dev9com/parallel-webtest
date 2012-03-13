package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.rule.DriverCleanUpRule;
import com.dynacrongroup.webtest.rule.FinalTestStatusRule;
import com.dynacrongroup.webtest.rule.SauceLabsContextReportRule;
import com.dynacrongroup.webtest.rule.TimerRule;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.dynacrongroup.webtest.WebDriverUtilities.reduceToOneWindow;

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

    @Rule
    public TestRule localTestWatcherChain;

    /**
     * Stores job pass/fail data for a given parameterized run.
     */
    private static ThreadLocal<TestRule> testWatcherChain = new ThreadLocal<TestRule>();

    /**
     * Stores whether the class is on its first method.
     */
    private static ThreadLocal<Boolean> firstMethod = new ThreadLocal<Boolean>();

    /**
     * The logger associated with this specific browser test execution
     */
    private final Logger browserTestLog;

    /**
     * The configuration for the WebDriver used for these tests.
     */
    private final TestDriverConfiguration testDriverConfiguration;

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
        TargetWebBrowser targetWebBrowser = new TargetWebBrowser(browser, version);
        testDriverConfiguration = new TestDriverConfiguration(this.getClass(), targetWebBrowser, customCapabilities);

        browserTestLog = createTestLogger();
        driver = WebDriverStorage.getDriver(testDriverConfiguration);
        initializeJUnitRules();

        reportStartUp();
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

    @After
    public void cleanUpDriverWindows() {
        reduceToOneWindow(driver);
    }

    /**
     * This is the target browser/version for this test. The values are a bit
     * different with Selenium 2/ WebDriver. See the WebDriverFactory and
     * WebDriverLauncher for more details.
     */
    public final TargetWebBrowser getTargetWebBrowser() {
        return testDriverConfiguration.getTargetWebBrowser();
    }

    /**
     * Returns the SauceLabs job URL (if there is one).  Constructed dynamically.
     */
    public final String getJobURL() {
        String jobUrl = null;
        String currentJobId = getJobId();

        if (currentJobId != null && getTargetWebBrowser().isRemote()) {
            jobUrl = "https://saucelabs.com/jobs/" + currentJobId;
        }

        return jobUrl;
    }

    /**
     * Returns the SauceLabs job id (if there is one).
     */
    public final String getJobId() {
        String id = null;
        if (RemoteWebDriver.class.isAssignableFrom(driver.getClass())) {
            id = ((RemoteWebDriver) driver).getSessionId().toString();
        }
        return id;
    }

    /**
     * The name of the job, as reported to SauceLabs. Includes the user name and
     * the name of the class.
     */
    public final String getJobName() {
        return WebDriverLauncher.getJobName(this.getClass());
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

    private void setTestWatcherChain(TestRule rule) {
        testWatcherChain.set(rule);
    }

    private TestRule getTestWatcherChain() {
        return testWatcherChain.get();
    }

    private void reportStartUp() {
        if (firstMethod.get() == null || firstMethod.get()) {
            String message = "WebDriver ready.";
            if (getTargetWebBrowser().isRemote()) {
                 message += "  View on Sauce Labs at " + getJobURL();
            }
            browserTestLog.info(message);
            firstMethod.set(false);
        }
    }


    private void initializeJUnitRules() {
        localTestWatcherChain = getTestWatcherChain();
        if (localTestWatcherChain == null) {
            localTestWatcherChain = createTestWatcherChain();
            setTestWatcherChain(localTestWatcherChain);
        }
    }

    private TestRule createTestWatcherChain() {
        RuleChain chain = createStandardRuleChain();

        if (getTargetWebBrowser().isRemote()) {
            chain = attachRemoteReportingRules(chain);
        }

        return chain;
    }

    private RuleChain createStandardRuleChain() {
        DriverCleanUpRule driverCleanUpRule = new DriverCleanUpRule(testDriverConfiguration);    //Needs to be last; kills the driver.
        TimerRule timerRule = new TimerRule();

        return RuleChain.outerRule(driverCleanUpRule)   //Outer rule is executed last.
                .around(timerRule);
    }

    /**
     * Adds rules that are only applicable in Sauce Labs.
     *
     * @param chain
     */
    private RuleChain attachRemoteReportingRules(RuleChain chain) {
        FinalTestStatusRule finalTestStatusRule = new FinalTestStatusRule(getJobId());
        SauceLabsContextReportRule sauceLabsContextReportRule = new SauceLabsContextReportRule(driver);

        return chain
                .around(finalTestStatusRule)
                .around(sauceLabsContextReportRule);
    }



    private Logger createTestLogger() {
        String logName = String.format("%s-%s",
                this.getClass().getName(),
                getTargetWebBrowser().humanReadable());
        return LoggerFactory.getLogger(logName);
    }


}
