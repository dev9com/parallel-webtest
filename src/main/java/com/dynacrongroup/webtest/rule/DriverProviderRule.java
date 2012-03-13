package com.dynacrongroup.webtest.rule;

import com.dynacrongroup.webtest.TestDriverConfiguration;
import com.dynacrongroup.webtest.WebDriverLauncher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;

/**
 * Class manages the driver life cycle.
 * <p/>
 * User: yurodivuie
 * Date: 3/11/12
 * Time: 9:56 AM
 */
public class DriverProviderRule extends ClassFinishRule {

    private Logger log;
    private TestDriverConfiguration configuration;
    private WebDriver driver;

    public DriverProviderRule(TestDriverConfiguration configuration, Logger log) {
        this.log = log;
        this.configuration = configuration;
        this.driver = WebDriverLauncher.getNewWebDriverInstance(configuration);
        reportStartUp();
    }

    @Override
    protected void classFinished(Description description) {
        driver.quit();
        driver = null;
        reportShutDown();
    }

    /**
     * Provides WebDriver to tests using this rule to manage driver LifeCycle.
     * @return
     */
    public final WebDriver getDriver() {
        return driver;
    }

    /**
     * Returns the SauceLabs job URL (if there is one).  Constructed dynamically.
     */
    public final String getJobURL() {
        String jobUrl = null;
        String currentJobId = getJobId();

        if (currentJobId != null && configuration.getTargetWebBrowser().isRemote()) {
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

    private void reportStartUp() {
        String message = "WebDriver ready.";
        if (configuration.getTargetWebBrowser().isRemote()) {
            message += "  View on Sauce Labs at " + getJobURL();
        }
        log.info(message);
    }

    private void reportShutDown() {
        log.info("WebDriver shut down.");
    }


}
