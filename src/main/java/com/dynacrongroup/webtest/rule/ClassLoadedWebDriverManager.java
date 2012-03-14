package com.dynacrongroup.webtest.rule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class manages the driver life cycle for a local, class-loaded WebDriver
 * <p/>
 * User: yurodivuie
 * Date: 3/11/12
 * Time: 9:56 AM
 */
public class ClassLoadedWebDriverManager extends AbstractWebDriverManager implements WebDriverManager {

    private static final Logger LOG = LoggerFactory.getLogger(ClassLoadedWebDriverManager.class);

    /**
     * Creates a new ClassLoadedWebDriverManager with the specified driver.
     *
     * @param driver
     */
    public ClassLoadedWebDriverManager(WebDriver driver) {
        super(driver);
    }

    /**
     * Returns the job id (not generally useful for class-loaded drivers, but accessible).
     */
    @Override
    public final String getJobId() {
        return ((RemoteWebDriver) driver).getSessionId().toString();
    }

    void reportStartUp() {
        LOG.info("WebDriver ready");
    }

    void reportShutDown() {
        LOG.info("WebDriver shut down");
    }


}
