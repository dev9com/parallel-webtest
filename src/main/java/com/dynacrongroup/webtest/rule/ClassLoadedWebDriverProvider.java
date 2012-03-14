package com.dynacrongroup.webtest.rule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class manages the driver life cycle.
 * <p/>
 * User: yurodivuie
 * Date: 3/11/12
 * Time: 9:56 AM
 */
public class ClassLoadedWebDriverProvider extends AbstractWebDriverProvider implements WebDriverProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ClassLoadedWebDriverProvider.class);

    public ClassLoadedWebDriverProvider(WebDriver driver) {
        super(driver);
    }

    /**
     * Returns the job id.
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
