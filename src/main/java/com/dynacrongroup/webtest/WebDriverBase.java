package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.browser.WebDriverConfig;
import com.dynacrongroup.webtest.driver.WebDriverWrapper;
import com.dynacrongroup.webtest.parameter.ParameterCombination;
import com.dynacrongroup.webtest.parameter.ParameterCombinationFactory;
import com.dynacrongroup.webtest.parameter.ParameterCombinationRunner;
import com.dynacrongroup.webtest.rule.*;
import com.dynacrongroup.webtest.util.SauceLabsCredentials;
import com.dynacrongroup.webtest.util.WebDriverUtilities;
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

import static com.dynacrongroup.webtest.util.WebDriverUtilities.createJobName;
import static com.dynacrongroup.webtest.util.WebDriverUtilities.reduceToOneWindow;

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
    public TestName testName = new TestName();

    /**
     * public version of the testWatcherChain, to satisfy annotation requirements.
     */
    @Rule
    public TestRule localTestWatcherChain;

    /**
     * Stores all rules for this class in the correct order of operation.
     */
    private static ThreadLocal<TestRule> testWatcherChain = new ThreadLocal<TestRule>();

    private static ThreadLocal<WebDriverWrapper> threadLocalWebDriverWrapper = new ThreadLocal<WebDriverWrapper>();

    /**
     * The logger associated with this specific browser test execution
     */
    private final Logger browserTestLog;

    /**
     * The configuration for the browser used for these tests.
     */
    private final WebDriverConfig webDriverConfig;

    /**
     * Contains all parameters for the test
     */
    private final ParameterCombination parameterCombination;

    /**
     *
     * @param parameterCombination
     */
    public WebDriverBase(ParameterCombination parameterCombination) {
        this.parameterCombination = parameterCombination;
        this.webDriverConfig = parameterCombination.getWebDriverConfig();
        this.browserTestLog = createTestLogger();
        initializeJUnitRules();
    }

    /**
     * `
     * Feeds in the list of target browsers. This might be a single local
     * browser, HTMLUnit, or one or more remote SauceLabs instances.
     *
     * @see com.dynacrongroup.webtest.parameter.ParameterCombinationFactory
     */
    @ParameterCombinationRunner.Parameters
    public static <T extends ParameterCombination> List<T> configureParameters(Class testClass) throws IOException {
        return new ParameterCombinationFactory(testClass).make();
    }

    @Before
    public void provideDriverForTests() {
        this.driver = getDriver();
    }

    @After
    public void cleanUpDriverWindows() {
        reduceToOneWindow(getDriver());
    }

    /**
     * This is the target browser/version for this test. The values are a bit
     * different with Selenium 2/ WebDriver. See the ParameterCombinationFactory and
     * WebDriverLauncher for more details.
     */
    public final WebDriverConfig getWebDriverConfig() {
        return webDriverConfig;
    }

    /**
     * Returns the SauceLabs job URL (if there is one).  Constructed dynamically.
     */
    public final String getJobURL() {
        return WebDriverUtilities.getJobUrl(webDriverConfig, getDriver());
    }

    /**
     * Returns the SauceLabs job id (if there is one).
     */
    public final String getJobId() {
        return WebDriverUtilities.getJobId(getDriver());
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
        return createJobName(this.getClass());
    }

    private Logger createTestLogger() {
        String logName = String.format("%s-%s",
                this.getClass().getName(),
                parameterCombination.toString());
        return LoggerFactory.getLogger(logName);
    }

    /**
     * ************************************
     * static ThreadLocal Accessors
     * *************************************
     */

    private void setTestWatcherChain(TestRule rule) {
        testWatcherChain.set(rule);
    }

    private TestRule getTestWatcherChain() {
        return testWatcherChain.get();
    }

    private WebDriver getDriver() {
        return getDriverWrapper().getDriver();
    }

    private WebDriverWrapper getDriverWrapper() {
        WebDriverWrapper wrapper = threadLocalWebDriverWrapper.get();
        if (wrapper == null) {
            wrapper = new WebDriverWrapper(getJobName(), webDriverConfig);
            threadLocalWebDriverWrapper.set(wrapper);
        }
        return wrapper;
    }

    /***************************************
     * JUnit Rule Management
     ***************************************/

    /**
     * Sets the local test watcher chain, initializing if necessary.
     */
    private void initializeJUnitRules() {
        localTestWatcherChain = getTestWatcherChain();
        if (localTestWatcherChain == null) {
            localTestWatcherChain = createTestWatcherChain();
            setTestWatcherChain(localTestWatcherChain);
        }
    }

    /**
     * Creates the rule chain that will be used for all tests
     *
     * @return
     */
    private TestRule createTestWatcherChain() {
        RuleChain chain = createStandardRuleChain();

        if (getWebDriverConfig().isRemote()) {
            chain = attachRemoteReportingRules(chain);
        }

        return chain;
    }

    /**
     * Creates the standard rule chain, which only tracks the driver and times test methods.
     *
     * @return
     */
    private RuleChain createStandardRuleChain() {

        RuleChain ruleChain = RuleChain.outerRule(new MethodTimer())      //Timer is wrapped around all other rules
                //.around(new CrashedBrowserChecker(getDriverWrapper()))   //After all rules using the driver are run, check if the browser has crashed.
                .around(new ClassFinishDriverCloser(getDriverWrapper()))
                .around(new ParameterResultReport(webDriverConfig, getDriverWrapper()));

        return ruleChain;
    }

    /**
     * Adds rules that are only applicable in Sauce Labs.
     *
     * @param chain
     */
    private RuleChain attachRemoteReportingRules(RuleChain chain) {
        String sauceUser = SauceLabsCredentials.getUser();
        String sauceKey = SauceLabsCredentials.getKey();

        SauceLabsFinalStatusReporter sauceLabsFinalStatusReporter =
                new SauceLabsFinalStatusReporter(getLogger(), getJobId(), sauceUser, sauceKey);
        SauceLabsLogger sauceLabsLogger =
                new SauceLabsLogger(getDriverWrapper());

        return chain
                .around(sauceLabsFinalStatusReporter)
                .around(sauceLabsLogger);               //The innermost rule logs test results, if possible.
    }


}
