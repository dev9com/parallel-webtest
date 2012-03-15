package com.dynacrongroup.webtest.rule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class manages the driver life cycle for a RemoteWebDriver.
 * <p/>
 * User: yurodivuie
 * Date: 3/11/12
 * Time: 9:56 AM
 */
public class RemoteWebDriverManager extends AbstractWebDriverManager implements WebDriverManager {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteWebDriverManager.class);

    /**
     * Constructs a new RemoteWebDriverManager (typically used for Sauce Labs browsers).
     * @param driver
     */
    public RemoteWebDriverManager(WebDriver driver) {
        super(driver);
    }

    /**
     * Returns the SauceLabs job URL.  Constructed dynamically.
     */
    @Override
    public final String getJobURL() {
        return "https://saucelabs.com/jobs/" + getJobId();
    }

    /**
     * Returns the SauceLabs job id.
     */
    @Override
    public final String getJobId() {
        return ((RemoteWebDriver) getDriver()).getSessionId().toString();
    }

    void reportStartUp() {
        LOG.info("RemoteWebDriver ready.  View on Sauce Labs at {}", getJobURL());
    }

    void reportShutDown() {
        LOG.info("RemoteWebDriver shut down.");
    }
}
