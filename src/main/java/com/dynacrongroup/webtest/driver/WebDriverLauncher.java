package com.dynacrongroup.webtest.driver;

import com.dynacrongroup.webtest.browser.WebDriverConfig;
import com.dynacrongroup.webtest.util.ConfigurationValue;
import com.dynacrongroup.webtest.util.SauceLabsCredentials;
import com.dynacrongroup.webtest.util.SystemName;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.UUID;

/**
 * This class is responsible for launching single WebDriver instances. Note that
 * it uses the logger passed in - this allows for breaking up the logs by target
 * browser.
 */
public class WebDriverLauncher {

    /**
     * Maximum retries for getting a remote web driver
     */
    public static final int MAX_RETRIES = 3;

    /**
     * Unique identifier for this job run. Global value for entire suite
     * execution (i.e. corresponds to a single complete mvn clean verify)
     */
    protected static final String uniqueId = String.valueOf(UUID.randomUUID());

    private Logger LOG = LoggerFactory.getLogger(WebDriverLauncher.class);

    private WebDriver driver = null;


    public WebDriverLauncher() {
    }

    /**
     * Returns HtmlUnitDriver with necessary default configuration (JavaScript enabled)
     *
     * @return
     */
    public WebDriver getHtmlUnitDriver(WebDriverConfig webDriverConfig) {
        if (webDriverConfig.hasCustomCapabilities()) {
            DesiredCapabilities capabilities = new DesiredCapabilities(webDriverConfig.getCustomCapabilities());
            capabilities.setJavascriptEnabled(true);
            driver = new HtmlUnitDriver(capabilities);
        }
        else {
            driver = new HtmlUnitDriver(true);
        }
        return driver;
    }

    /**
     * Returns a driver for a local, class-loaded browser.
     * @param webDriverConfig
     * @return
     */
    public WebDriver getClassLoadedDriver(WebDriverConfig webDriverConfig) {
        LOG.trace("Initializing WebDriver by specified class: {}", webDriverConfig.humanReadable());

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        if (webDriverConfig.getCustomCapabilities() != null) {
            desiredCapabilities = new DesiredCapabilities(webDriverConfig.getCustomCapabilities());
        }

        try {
            Class browserClass = webDriverConfig.getBrowser().getDriverClass();
            Constructor constructorWithCapabilities = browserClass.getDeclaredConstructor(Capabilities.class);
            driver = (WebDriver) constructorWithCapabilities.newInstance(desiredCapabilities);
        } catch (Exception e) {
            LOG.error("Exception while loading class-loaded driver", e);
            if (e.getMessage() != null && e.getMessage().contains("Unable to bind to locking port")) {
                LOG.error("Locking port error may be caused by ephemeral port exhaustion.  Try reducing the number of threads.");
            }
            throw new WebDriverException("Unable to load target WebDriver class: " + webDriverConfig.getBrowser(), e);
        }

        verifyDriverNotNull(driver);
        return driver;
    }

    /**
     * Returns a driver for a remote driver.
     * @param jobName   Name of the job as it will appear in the remote server
     * @param target
     * @return
     */
    public WebDriver getRemoteDriver(String jobName, WebDriverConfig target) {
        verifyHostNameIsSpecified();
        getRemoteDriverFromSauceLabs(jobName, target);
        verifyDriverNotNull(driver);
        return driver;
    }

    private void verifyHostNameIsSpecified() {
        String webTestHostName = ConfigurationValue.getConfigurationValue(SystemName.WEBTEST_HOSTNAME, null);
        if (webTestHostName == null) {
            throw new WebDriverException(
                    "No hostname is specified for remote test. Please specify a WEBTEST_HOSTNAME value.");
        }
    }

    private void verifyDriverNotNull(WebDriver driver) {
        if (driver == null) {
            throw new WebDriverException("Failed to provision WebDriver.");
        }
    }

    private void getRemoteDriverFromSauceLabs(String jobName, WebDriverConfig target) {
        for (int attempt = 1; attempt <= MAX_RETRIES && driver == null; attempt++) {
            LOG.trace("RemoteWebDriver provisioning attempt {} for job {}", attempt, jobName);
            try {
                buildDriverWithCapabilities(jobName, target);
                verifyDriverIsValid(driver);
            }
            catch (WebDriverException e) {
                LOG.error("Unable to launch RemoteWebDriver: {}", e.getMessage());
                driver = null;
            }
        }
    }

    private void buildDriverWithCapabilities(String jobName, WebDriverConfig target) {
        DesiredCapabilities capabilities = constructDefaultCapabilities(jobName, target);
        capabilities = mergeDefaultAndCustomCapabilities(capabilities, target.getCustomCapabilities());
        driver = new CapturingRemoteWebDriver(
                SauceLabsCredentials.getConnectionString(),
                capabilities);
    }

    private DesiredCapabilities constructDefaultCapabilities(String jobName, WebDriverConfig target) {
        //String seleniumVersion = ConfigurationValue.getConfigurationValue("REMOTE_SERVER_VERSION", "2.25.0");

        DesiredCapabilities capabilities = new DesiredCapabilities(
                target.getBrowser().name().toLowerCase(), target.getVersion(), target.getPlatform());
        capabilities.setCapability("name", jobName);
        capabilities.setCapability("tags", SystemName.getSystemName());
        capabilities.setCapability("build", uniqueId);
        //capabilities.setCapability("selenium-version", seleniumVersion);
        capabilities.setCapability("command-timeout", "60");    //default is 300 - may need to revisit.

        return capabilities;
    }

    private DesiredCapabilities mergeDefaultAndCustomCapabilities(DesiredCapabilities capabilities, Map<String, Object> customCapabilities) {
        if (customCapabilities != null) {
            for (Map.Entry<String, Object> customCapability : customCapabilities.entrySet()) {
                capabilities.setCapability(customCapability.getKey(), customCapability.getValue());
            }
        }
        return capabilities;
    }

    private void verifyDriverIsValid(WebDriver driver) throws WebDriverException {
        if (driver.getWindowHandle() == null ) {
            throw new WebDriverException("driver.getWindowHandle() returned null.");
        }
    }

}