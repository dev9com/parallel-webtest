package com.dynacrongroup.webtest.driver;

import com.dynacrongroup.webtest.WebDriverFactory;
import com.dynacrongroup.webtest.browser.TargetWebBrowser;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a consistent reference as drivers are created and destroyed by rules.
 *
 * User: yurodivuie
 * Date: 4/6/12
 * Time: 1:48 PM
 */
public class WebDriverWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverWrapper.class);

    private final String jobName;
    private final TargetWebBrowser targetWebBrowser;

    private WebDriver driver;

    public WebDriverWrapper(String jobName, TargetWebBrowser targetWebBrowser) {
        this.jobName = jobName;
        this.targetWebBrowser = targetWebBrowser;

        LOG.debug("Created WebDriverWrapper for {} - {}", jobName, targetWebBrowser.humanReadable());
    }

    public WebDriver getDriver() {
        if (driver == null) {
            getNewDriver();
        }

        return driver;
    }

    public String getJobName() {
        return jobName;
    }

    public TargetWebBrowser getTargetWebBrowser() {
        return targetWebBrowser;
    }

    /**
     * If we can't make a simple call for the url, the driver is toast, so quit and null it.
     */
    public void checkState() {

        if (driver != null) {
            try {
                driver.getCurrentUrl();
            }
            catch (Exception exception) {
                LOG.warn("Driver failed health check.");
                killDriver();
            }
        }
    }

    public void killDriver() {
        LOG.debug("Killing driver for {}:{}.");
        driver.quit();
        driver = null;
    }

    private void getNewDriver() {
        driver = WebDriverFactory.getDriver(jobName, targetWebBrowser);
    }

}
