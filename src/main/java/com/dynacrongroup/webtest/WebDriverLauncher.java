package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.driver.CapturingRemoteWebDriver;
import com.dynacrongroup.webtest.util.ConfigurationValue;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;

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

    private Logger log;


    public WebDriverLauncher(Logger log) {
        this.log = log;
    }

    /**
     * Returns HtmlUnitDriver with necessary default configuration (JavaScript enabled)
     *
     * @return
     */
    public WebDriver getHtmlUnitDriver() {
        return new HtmlUnitDriver(true);
    }

    public WebDriver getClassLoadedDriver(String driverClass) {
        log.trace("Initializing WebDriver by specified class: {}", driverClass);
        WebDriver driver;
        try {
            driver = (WebDriver) Class.forName(driverClass).newInstance();
        } catch (Exception e) {
            log.error("Unable to load target WebDriver class: {}", e.getMessage());     //Sometimes caused by port locking in FF
            if (e.getMessage().contains("Unable to bind to locking port")) {
                log.error("Locking port error may be caused by ephemeral port exhaustion.  Try reducing the number of threads.");
            }
            driver = null;
        }
        return driver;
    }

    public WebDriver getRemoteDriver(String jobName, TargetWebBrowser target, Map<String, Object> customCapabilities) {
        WebDriver driver = null;
        String server = SauceLabsCredentials.getServer();

        for (int attempt = 1; attempt <= MAX_RETRIES && driver == null; attempt++) {
            try {
                log.trace("RemoteWebDriver provisioning attempt {} for job {}", attempt, jobName);
                DesiredCapabilities capabilities = constructDefaultCapabilities(jobName, target);
                addCustomCapabilities(capabilities, customCapabilities);
                driver = new CapturingRemoteWebDriver(
                        SauceLabsCredentials.getConnectionString(),
                        capabilities);
                verifyDriverIsValid(driver);
            }
            catch (Exception e) {
                log.error("Unable to launch RemoteWebDriver for [{}]: {}", server, e.getMessage());
                driver = null;
            }
        }

        return driver;
    }

    private void verifyDriverIsValid(WebDriver driver) throws WebDriverException {
        if (driver.getWindowHandle() != null) {
            log.debug("Successfully launched RemoteWebDriver");
        } else {
            throw new WebDriverException("driver.getWindowHandle() returned null.");
        }
    }

    private DesiredCapabilities constructDefaultCapabilities(String jobName, TargetWebBrowser target) {
        String seleniumVersion = ConfigurationValue.getConfigurationValue("REMOTE_SERVER_VERSION", "2.19.0");

        DesiredCapabilities capabilities = new DesiredCapabilities(
                target.browser, target.version, getPlatform(target));
        capabilities.setCapability("name", jobName);
        capabilities.setCapability("tags", SystemName.getSystemName());
        capabilities.setCapability("build", uniqueId);
        capabilities.setCapability("selenium-version", seleniumVersion);

        return capabilities;
    }

    private Platform getPlatform(TargetWebBrowser target) {
        Platform platform = Platform.WINDOWS;

        /** TODO SauceLabs-specific. May need to update in future. */
        if (target.isInternetExplorer() && target.version.contains("9")) {
            platform = Platform.VISTA;
        }

        return platform;
    }

    private void addCustomCapabilities(DesiredCapabilities capabilities, Map<String, Object> customCapabilities) {
        if (customCapabilities != null) {
            for (String customCapability : customCapabilities.keySet()) {
                capabilities.setCapability(customCapability, customCapabilities.get(customCapability));
            }
        }
    }

}
