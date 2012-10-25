package com.dynacrongroup.webtest.driver;

import com.dynacrongroup.webtest.browser.WebDriverConfig;
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

    public static final Integer MAX_ALLOWABLE_CRASHES = 5;

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverWrapper.class);

    private static Integer crashCount = 0;

    private final String jobName;
    private final WebDriverConfig webDriverConfig;

    private WebDriver driver;

    public WebDriverWrapper(String jobName, WebDriverConfig webDriverConfig) {
        this.jobName = jobName;
        this.webDriverConfig = webDriverConfig;

        LOG.debug("Created WebDriverWrapper for {} - {}", jobName, webDriverConfig.humanReadable());
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

    public WebDriverConfig getWebDriverConfig() {
        return webDriverConfig;
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
                LOG.debug("Driver crashed - {}:{}.", jobName, webDriverConfig.humanReadable());
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
            LOG.debug("Killing driver for {}:{}.", jobName, webDriverConfig.humanReadable());
            try {
                driver.quit();
            } catch (WebDriverException exception) {
                LOG.warn("Failed to kill driver; likely cause is crashed driver: {}", exception.getMessage());
            }
        }
        driver = null;
    }

    private void getNewDriver() {
        if (!tooManyCrashes()) {
            driver = WebDriverFactory.getDriver(jobName, webDriverConfig);
        } else {
            throw new WebDriverException("Giving up on provisioning driver; crashed [" + crashCount + "] times.");
        }
    }

    private void recordCrash() {
        crashCount++;
    }

    private boolean tooManyCrashes() {
        return crashCount > MAX_ALLOWABLE_CRASHES;
    }


}
