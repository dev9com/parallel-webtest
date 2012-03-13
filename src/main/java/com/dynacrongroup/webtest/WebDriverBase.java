package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.rule.DriverProviderRule;
import com.dynacrongroup.webtest.rule.FinalTestStatusRule;
import com.dynacrongroup.webtest.rule.SauceLabsContextReportRule;
import com.dynacrongroup.webtest.rule.TimerRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;
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

    /**
     * public version of the testWatcherChain, to satisfy annotation requirements.
     */
    @Rule
    public TestRule localTestWatcherChain;

    /**
     * Stores all rules for this class in the correct order of operation.
     */
    private static ThreadLocal<TestRule> testWatcherChain = new ThreadLocal<TestRule>();

    /**
     * Stores all rules for this class in the correct order of operation.
     */
    private static ThreadLocal<DriverProviderRule> driverProviderRule =
            new ThreadLocal<DriverProviderRule>();

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
        initializeJUnitRules();
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

    @Before
    public void getDriverFromProvider() {
        driver = getDriverProviderRule().getDriver();
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
        return getDriverProviderRule().getJobURL();
    }

    /**
     * Returns the SauceLabs job id (if there is one).
     */
    public final String getJobId() {
        return getDriverProviderRule().getJobId();
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

    public DriverProviderRule getDriverProviderRule() {
        return driverProviderRule.get();
    }

    public static void setDriverProviderRule(DriverProviderRule driverProviderRule) {
        WebDriverBase.driverProviderRule.set(driverProviderRule);
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
        TimerRule timerRule = new TimerRule();
        DriverProviderRule newDriverProviderRule = new DriverProviderRule(testDriverConfiguration, getLogger());    //Needs to be last; creates and kills the driver.
        setDriverProviderRule(newDriverProviderRule);

        return RuleChain.outerRule(newDriverProviderRule)   //Outer rule is executed last.
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
