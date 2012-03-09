package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.util.CapturingRemoteWebDriver;
import com.dynacrongroup.webtest.util.ConfigurationValue;
import com.dynacrongroup.webtest.util.ConnectionValidator;
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

    public static int MAX_RETRIES = 3;
    /**
     * Unique identifier for this job run. Global value for entire suite
     * execution (i.e. corresponds to a single complete mvn clean verify)
     */
    protected static String uniqueId = String.valueOf(UUID.randomUUID());


    private WebDriverLauncher() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Returns the actual web drivers. Requires the logger and target web
     * browser to be specified.
     */
    public static WebDriver getNewWebDriverInstance(String jobName, Logger testLog,
                                             TargetWebBrowser target) {
        return getNewWebDriverInstance(jobName, testLog, target, null);
    }

    /**
     * Returns the actual web drivers. Requires the logger and target web
     * browser to be specified.  Custom capabilities are optional.
     */
    public static WebDriver getNewWebDriverInstance(String jobName, Logger testLog,
                                             TargetWebBrowser target, Map<String, Object> customCapabilities) {

        if (testLog == null) {
            throw new IllegalArgumentException(
                    "No logger specified for the WebDriverLauncher.");
        }

        if (target == null) {
            throw new IllegalArgumentException("No target browser specified.");
        }

        if (target.isHtmlUnit()) {
            testLog.trace("Initializing HTMLUnit: " + jobName);

            return new HtmlUnitDriver(true);

        }

        WebDriver driver = null;

        boolean validWebDriver = false;

        for (int attempt = 1; attempt <= MAX_RETRIES && !validWebDriver; attempt++) {
            testLog.trace("WebDriver provisioning attempt [{}]", attempt);

            if (target.isClassLoaded()) {
                testLog.trace("Initializing WebDriver by specified class: "
                        + jobName);
                try {
                    driver = (WebDriver) Class.forName(target.version).newInstance();
                } catch (WebDriverException e) {
                    testLog.error("Unable to load target WebDriver class.", e);     //Sometimes caused by port locking in FF
                    if (e.getMessage().contains("Unable to bind to locking port")) {
                        testLog.error("Locking port error may be caused by ephemeral port exhaustion.  Try reducing the number of threads.");
                    }
                }
                // If this is not a WebDriverException caused by ephemereal port locking, it's a programmatic error that shouldn't be retried.
                catch (Exception e) {
                    testLog.error("Unable to load target WebDriver class.", e);
                    driver = null;
                    break;
                }

                validWebDriver = driver != null;
            } else {

                String server = SauceLabsCredentials.getServer();

                try {
                    testLog.trace("Initializing remote job: "
                            + jobName + " [" + uniqueId + "]");

                    ConnectionValidator
                            .verifyConnection("http://ondemand.saucelabs.com/");

                    Platform platform = Platform.WINDOWS;

                    /** TODO SauceLabs-specific. May need to update in future. */
                    if (target.isInternetExplorer() && target.version.contains("9")) {
                        platform = Platform.VISTA;
                    }
                    DesiredCapabilities capabilities = new DesiredCapabilities(
                            target.browser, target.version, platform);
                    capabilities.setCapability("name", jobName);
                    capabilities.setCapability("tags", SystemName.getSystemName());
                    capabilities.setCapability("build", uniqueId);
                    capabilities.setCapability("selenium-version", ConfigurationValue.getConfigurationValue("REMOTE_SERVER_VERSION", "2.19.0"));
                    addCustomCapabilities(capabilities, customCapabilities, testLog);
                    driver = new CapturingRemoteWebDriver(
                            SauceLabsCredentials.getConnectionString(),
                            capabilities);

                    if (driver.getWindowHandle() != null) {
                        validWebDriver = true;
                        testLog.debug("Successfully launched RemoteWebDriver for [{}].", server);
                        String jobUrl = WebDriverUtilities.constructSauceJobUrl(
                                WebDriverUtilities.getJobIdFromDriver(driver));
                        testLog.trace("Job url set to: {}", jobUrl);
                    } else {
                        testLog.warn("Unable to launch RemoteWebDriver for [{}] on attempt {} of {}.",
                                new Object[]{server, attempt, MAX_RETRIES});
                    }
                } catch (Exception e) {
                    testLog.error("Unable to launch RemoteWebDriver on attempt "
                            + attempt, e);
                }
            }
        }

        if (driver == null) {
            throw new ExceptionInInitializerError("Unable to initialize valid WebDriver.");
        }
        return driver;
    }

    private static void addCustomCapabilities(DesiredCapabilities capabilities, Map<String, Object> customCapabilities, Logger testLog) {
        if (customCapabilities != null) {
            for (String customCapability : customCapabilities.keySet()) {
                testLog.debug("Adding capability [{}] - [{}]", customCapability, customCapabilities.get(customCapability));
                capabilities.setCapability(customCapability, customCapabilities.get(customCapability));
            }
        }
    }

}
