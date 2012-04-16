package com.dynacrongroup.webtest.driver;

import com.dynacrongroup.webtest.WebDriverFactory;
import com.dynacrongroup.webtest.browser.TargetWebBrowser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a consistent reference as drivers are created and destroyed by rules.
 * <p/>
 * User: yurodivuie
 * Date: 4/6/12
 * Time: 1:48 PM
 */
public class WebDriverWrapper {

    public static final Integer MAX_ALLOWABLE_CRASHES = 4;
    public static Boolean TOO_MANY_CRASHES = false;

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverWrapper.class);

    private static Integer crashCount = 0;

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
     * Check if we can make a simple call to the url.
     */
    public Boolean isCrashed() {

        Boolean crashed = false;

        if (driver != null) {
            try {
                crashed = driver.getCurrentUrl() == null;
            } catch (Exception exception) {
                LOG.debug("Driver crashed - {}:{}.", jobName, targetWebBrowser.humanReadable());
                crashed = true;
            }
        }

        if (crashed) {
            recordCrash();
        }

        return crashed;
    }

    /**
     * Generally used after driver has crashed.
     */
    public void rebuildDriver() {
        killDriver();
        getNewDriver();
    }

    /**
     * Quit the driver if it exists.
     */
    public void killDriver() {
        if (driver != null) {
            LOG.debug("Killing driver for {}:{}.", jobName, targetWebBrowser.humanReadable());
            try {
                driver.quit();
            } catch (WebDriverException exception) {
                LOG.warn("Failed to kill driver; likely cause is crashed driver: {}", exception.getMessage());
            }
        }
        driver = null;
    }

    private void getNewDriver() {
        if (!TOO_MANY_CRASHES) {
            driver = WebDriverFactory.getDriver(jobName, targetWebBrowser);
        } else {
            throw new WebDriverException("Giving up on provisioning driver; crashed [" + crashCount + "] times.");
        }
    }

    private void recordCrash() {
        crashCount++;
        if (crashCount > MAX_ALLOWABLE_CRASHES) {
            TOO_MANY_CRASHES = true;
        }
    }


}
