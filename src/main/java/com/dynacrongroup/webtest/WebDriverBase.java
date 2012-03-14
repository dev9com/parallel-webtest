package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.rule.SauceLabsFinalStatusReporter;
import com.dynacrongroup.webtest.rule.SauceLabsLogger;
import com.dynacrongroup.webtest.rule.MethodTimer;
import com.dynacrongroup.webtest.rule.WebDriverManager;
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
    private static ThreadLocal<WebDriverManager> webDriverManager =
            new ThreadLocal<WebDriverManager>();

    /**
     * The logger associated with this specific browser test execution
     */
    private final Logger browserTestLog;

    /**
     * The configuration for the browser used for these tests.
     */
    private final TargetWebBrowser targetWebBrowser;

    /**
     * Custom capabilities for this test (if a remote web driver test).
     */
    private final Map<String, Object> customCapabilities;

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
        this.browserTestLog = createTestLogger();
        this.customCapabilities = customCapabilities;
        initializeJUnitRules();
    }

    /**
     * Feeds in the list of target browsers. This might be a single local
     * browser, HTMLUnit, or one or more remote SauceLabs instances.
     *
     * @see WebDriverParameterFactory
     */
    @DescriptivelyParameterized.Parameters
    public static List<String[]> configureWebDriverTargets() throws IOException {
        return new WebDriverParameterFactory().getDriverTargets();
    }

    @Before
    public void loadDriverFromProvider() {
        driver = getDriverManager().getDriver();
    }

    @After
    public void cleanUpDriverWindows() {
        reduceToOneWindow(driver);
    }

    /**
     * This is the target browser/version for this test. The values are a bit
     * different with Selenium 2/ WebDriver. See the WebDriverParameterFactory and
     * WebDriverLauncher for more details.
     */
    public final TargetWebBrowser getTargetWebBrowser() {
        return targetWebBrowser;
    }

    /**
     * Returns the SauceLabs job URL (if there is one).  Constructed dynamically.
     */
    public final String getJobURL() {
        return getDriverManager().getJobURL();
    }

    /**
     * Returns the SauceLabs job id (if there is one).
     */
    public final String getJobId() {
        return getDriverManager().getJobId();
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
     * The name of the job, as reported to SauceLabs. Includes the user name and
     * the name of the class.
     */
    public String getJobName() {
        return SystemName.getSystemName() + "-"
                + this.getClass().getSimpleName();
    }

    private Logger createTestLogger() {
        String logName = String.format("%s-%s",
                this.getClass().getName(),
                getTargetWebBrowser().humanReadable());
        return LoggerFactory.getLogger(logName);
    }

    /***************************************
     * static ThreadLocal Accessors
     ***************************************/

    private void setTestWatcherChain(TestRule rule) {
        testWatcherChain.set(rule);
    }

    private TestRule getTestWatcherChain() {
        return testWatcherChain.get();
    }

    private void setDriverManager(WebDriverManager driverManager) {
        WebDriverBase.webDriverManager.set(driverManager);
    }

    private WebDriverManager getDriverManager() {
        return webDriverManager.get();
    }

    /***************************************
     * JUnit Rule Management
     ***************************************/

    /**
     * Sets the local test watcher chain, initializing if necessary.
     */
    private final void initializeJUnitRules() {
        localTestWatcherChain = getTestWatcherChain();
        if (localTestWatcherChain == null) {
            localTestWatcherChain = createTestWatcherChain();
            setTestWatcherChain(localTestWatcherChain);
        }
    }

    /**
     * Creates the rule chain that will be used for all tests
     * @return
     */
    private TestRule createTestWatcherChain() {
        RuleChain chain = createStandardRuleChain();

        if (getTargetWebBrowser().isRemote()) {
            chain = attachRemoteReportingRules(chain);
        }

        return chain;
    }

    /**
     * Creates the standard rule chain, which only tracks the driver and times test methods.
     * @return
     */
    private RuleChain createStandardRuleChain() {

        //WebDriverManager is created first and executed last, so that driver is available first and removed last.
        WebDriverManager manager = createDriverManager();
        MethodTimer methodTimer = new MethodTimer();

        return RuleChain.outerRule(manager)   //Outer rule is executed last.
                .around(methodTimer);
    }

    /**
     * Creates the driver manager with a new WebDriver driver.
     * @return A WebDriverManager, which provides the manager to the tests and handles cleanup
     *              after the tests have completed.
     */
    private WebDriverManager createDriverManager() {
        WebDriverManagerFactory managerFactory = new WebDriverManagerFactory(getLogger());
        WebDriverManager manager = managerFactory.getManager(getJobName(), targetWebBrowser, customCapabilities);
        setDriverManager(manager);
        return  manager;
    }

    /**
     * Adds rules that are only applicable in Sauce Labs.
     *
     * @param chain
     */
    private RuleChain attachRemoteReportingRules(RuleChain chain) {
        String sauceUser = SauceLabsCredentials.getUser();
        String sauceKey = SauceLabsCredentials.getKey();

        SauceLabsFinalStatusReporter sauceLabsFinalStatusReporter = new SauceLabsFinalStatusReporter(getJobId(), sauceUser, sauceKey);
        SauceLabsLogger sauceLabsLogger =
                new SauceLabsLogger(getDriverManager().getDriver());

        return chain
                .around(sauceLabsFinalStatusReporter)
                .around(sauceLabsLogger);
    }


}
