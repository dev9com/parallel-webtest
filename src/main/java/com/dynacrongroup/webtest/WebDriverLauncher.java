package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.browser.TargetWebBrowser;
import com.dynacrongroup.webtest.driver.CapturingRemoteWebDriver;
import com.dynacrongroup.webtest.util.ConfigurationValue;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;

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

    private Logger log = null;

    private WebDriver driver = null;


    public WebDriverLauncher(Logger log) {
        this.log = log;
    }

    /**
     * Returns HtmlUnitDriver with necessary default configuration (JavaScript enabled)
     *
     * @return
     */
    public WebDriver getHtmlUnitDriver() {
        driver = new HtmlUnitDriver(true);
        return driver;
    }

    /**
     * Returns a driver for a local, class-loaded browser.
     * @param targetWebBrowser
     * @return
     */
    public WebDriver getClassLoadedDriver(TargetWebBrowser targetWebBrowser) {
        log.trace("Initializing WebDriver by specified class: {}", targetWebBrowser.humanReadable());

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        if (targetWebBrowser.getCustomCapabilities() != null) {
            desiredCapabilities = new DesiredCapabilities(targetWebBrowser.getCustomCapabilities());
        }

        try {
            Class browserClass = Class.forName(targetWebBrowser.getBrowser());
            Constructor constructorWithCapabilities = browserClass.getDeclaredConstructor(Capabilities.class);
            driver = (WebDriver) constructorWithCapabilities.newInstance(desiredCapabilities);
        } catch (Exception e) {
            log.error("Exception while loading class-loaded driver", e);
            if (e.getMessage() != null && e.getMessage().contains("Unable to bind to locking port")) {
                log.error("Locking port error may be caused by ephemeral port exhaustion.  Try reducing the number of threads.");
            }
            throw new WebDriverException("Unable to load target WebDriver class: " + targetWebBrowser.getBrowser(), e);
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
    public WebDriver getRemoteDriver(String jobName, TargetWebBrowser target) {
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

    private void getRemoteDriverFromSauceLabs(String jobName, TargetWebBrowser target) {
        for (int attempt = 1; attempt <= MAX_RETRIES && driver == null; attempt++) {
            log.trace("RemoteWebDriver provisioning attempt {} for job {}", attempt, jobName);
            try {
                buildDriverWithCapabilities(jobName, target);
                verifyDriverIsValid(driver);
            }
            catch (WebDriverException e) {
                log.error("Unable to launch RemoteWebDriver: {}", e.getMessage());
                driver = null;
            }
        }
    }

    private void buildDriverWithCapabilities(String jobName, TargetWebBrowser target) {
        DesiredCapabilities capabilities = constructDefaultCapabilities(jobName, target);
        capabilities = mergeDefaultAndCustomCapabilities(capabilities, target.getCustomCapabilities());
        driver = new CapturingRemoteWebDriver(
                SauceLabsCredentials.getConnectionString(),
                capabilities);
    }

    private DesiredCapabilities constructDefaultCapabilities(String jobName, TargetWebBrowser target) {
        String seleniumVersion = ConfigurationValue.getConfigurationValue("REMOTE_SERVER_VERSION", "2.19.0");

        DesiredCapabilities capabilities = new DesiredCapabilities(
                target.getBrowser(), target.getVersion(), getPlatform(target));
        capabilities.setCapability("name", jobName);
        capabilities.setCapability("tags", SystemName.getSystemName());
        capabilities.setCapability("build", uniqueId);
        capabilities.setCapability("selenium-version", seleniumVersion);

        return capabilities;
    }

    private Platform getPlatform(TargetWebBrowser target) {
        Platform platform = Platform.WINDOWS;

        /** TODO SauceLabs-specific. May need to update in future. */
        if (target.isInternetExplorer() && target.getVersion().equalsIgnoreCase("9")) {
            platform = Platform.VISTA;
        }

        return platform;
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
        if (driver.getWindowHandle() != null) {
            log.debug("Successfully launched RemoteWebDriver");
        } else {
            throw new WebDriverException("driver.getWindowHandle() returned null.");
        }
    }

}
