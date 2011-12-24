package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.util.ConnectionValidator;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;

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

    private String jobUrl = "";

    public String getJobUrl() {
        return jobUrl;
    }

    /**
     * Returns the actual web drivers. Requires the logger and target web
     * browser to be specified.
     */
    public WebDriver getNewWebDriverInstance(String jobName, Logger testLog,
                                             TargetWebBrowser target) {

        if (testLog == null) {
            throw new IllegalArgumentException(
                    "No logger specified for the WebDriverLauncher.");
        }

        if (target == null) {
            throw new IllegalArgumentException("No target browser specified.");
        }

        if (target.isHtmlUnit()) {
            testLog.debug("Initializing HTMLUnit: " + jobName);

            return new HtmlUnitDriver(true);

        }

        WebDriver driver = null;

        boolean validWebDriver = false;
        
        for (int attempt = 0; attempt < MAX_RETRIES && !validWebDriver; attempt++) {
            testLog.debug("WebDriver provisioning attempt [{}]",  attempt +1 );

            if (target.isClassLoaded()) {
                testLog.debug("Initializing WebDriver by specified class: "
                        + jobName);
                try {
                    driver = (WebDriver) Class.forName(target.version).newInstance();
                } catch (WebDriverException e) {
                    testLog.error("Unable to load target WebDriver class.", e);     //Sometimes caused by port locking in FF
                    if (e.getMessage().contains("Unable to bind to locking port")) {
                        testLog.error("Locking port error may be caused by ephemereal port exhaustion.  Try reducing the number of threads.");
                    }
                }
                // If this is not a WebDriverException caused by ephemereal port locking, it's a programmatic error that shouldn't be retried.
                catch (InstantiationException e) {
                    testLog.error("Unable to load target WebDriver class.", e);
                    break;
                } catch (IllegalAccessException e) {
                    testLog.error("Unable to load target WebDriver class.", e);
                    break;
                } catch (ClassNotFoundException e) {
                    testLog.error("Unable to load target WebDriver class.", e);
                    break;
                }

                validWebDriver = driver != null;
            } else {

                try {
                    testLog.debug("Initializing ondemand.saucelabs.com:80 job: "
                            + jobName + " [" + uniqueId + "]");

                    ConnectionValidator
                            .verifyConnection("http://ondemand.saucelabs.com/");

                    Platform platform = Platform.WINDOWS;

                    /** TODO SauceLabs-specific. May need to update in future. */
                    if (target.isInternetExplorer() && target.version.contains("9")) {
                        platform = Platform.VISTA;
                    }
                    DesiredCapabilities capabillities = new DesiredCapabilities(
                            target.browser, target.version, platform);
                    capabillities.setCapability("name", jobName);
                    capabillities.setCapability("tags", SystemName.getSystemName());
                    capabillities.setCapability("build", uniqueId);
                    driver = new RemoteWebDriver(
                            SauceLabsCredentials.getConnectionString(),
                            capabillities);

                    if (driver.getWindowHandle() != null) {
                        validWebDriver = true;
                        testLog.debug("Successfully launched SauceLabs WebDriver connection.");
                        jobUrl = "https://saucelabs.com/jobs/"
                                + ((RemoteWebDriver) driver).getSessionId()
                                .toString();
                        testLog.trace("Job url set to: " + jobUrl);
                    } else {
                        testLog.warn("Unable to establish communication with SauceLabs on attempt "
                                + attempt + " of " + MAX_RETRIES);
                    }
                } catch (Throwable e) {
                    testLog.error("Unable to connect with SauceLabs on attempt "
                            + attempt, e);
                }
            }
        }

        if (driver == null) {
            throw new IllegalAccessError(
                    "Unable to initialize valid WebDriver.");
        }
        return driver;
    }
}
