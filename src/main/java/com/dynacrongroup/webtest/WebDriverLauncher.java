package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.driver.CapturingRemoteWebDriver;
import com.dynacrongroup.webtest.util.ConfigurationValue;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

/**
 * This class is responsible for launching single WebDriver instances. Note that
 * it uses the LOGger passed in - this allows for breaking up the LOGs by target
 * browser.
 */
public final class WebDriverLauncher {

    public static final int MAX_RETRIES = 3;
    /**
     * Unique identifier for this job run. Global value for entire suite
     * execution (i.e. corresponds to a single complete mvn clean verify)
     */
    protected static final String uniqueId = String.valueOf(UUID.randomUUID());

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverLauncher.class);


    private WebDriverLauncher() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Gets a new WebDriver instance matching the specified configuration.
     *
     * @param configuration Specifies the class making the request, the browser to use, and optional custom
     *                      capabilities.
     * @return
     */
    public static WebDriver getNewWebDriverInstance(TestDriverConfiguration configuration) {
        WebDriver driver = null;

        String jobName = getJobName(configuration.getTestClass());
        TargetWebBrowser target = configuration.getTargetWebBrowser();

        if (target.isHtmlUnit()) {
            driver = getHtmlUnitDriver();
        } else if (target.isClassLoaded()) {
            driver = getClassLoadedDriver(target.version);
        } else {
            driver = getRemoteDriver(jobName, target, configuration.getCustomCapabilities());
        }

        if (driver == null) {
            throw new ExceptionInInitializerError("Unable to initialize valid WebDriver.");
        }

        return driver;
    }

    /**
     * The name of the job, as reported to SauceLabs. Includes the user name and
     * the name of the class.
     */
    public static String getJobName(Class testClass) {
        return SystemName.getSystemName() + "-"
                + testClass.getSimpleName();
    }

    /**
     * Returns HtmlUnitDriver with necessary default configuration (JavaScript enabled)
     *
     * @return
     */
    private static WebDriver getHtmlUnitDriver() {
        return new HtmlUnitDriver(true);
    }

    private static WebDriver getClassLoadedDriver(String driverClass) {
        LOG.trace("Initializing WebDriver by specified class: {}", driverClass);
        WebDriver driver;
        try {
            driver = (WebDriver) Class.forName(driverClass).newInstance();
        } catch (Exception e) {
            LOG.error("Unable to load target WebDriver class: {}", e.getMessage());     //Sometimes caused by port locking in FF
            if (e.getMessage().contains("Unable to bind to locking port")) {
                LOG.error("Locking port error may be caused by ephemeral port exhaustion.  Try reducing the number of threads.");
            }
            driver = null;
        }
        return driver;
    }

    private static WebDriver getRemoteDriver(String jobName, TargetWebBrowser target, Map<String, Object> customCapabilities) {
        WebDriver driver = null;
        String server = SauceLabsCredentials.getServer();

        for (int attempt = 1; attempt <= MAX_RETRIES && driver == null; attempt++) {
            try {
                LOG.trace("RemoteWebDriver provisioning attempt {} for job {}", attempt, jobName);
                DesiredCapabilities capabilities = constructDefaultCapabilities(jobName, target);
                addCustomCapabilities(capabilities, customCapabilities);
                driver = new CapturingRemoteWebDriver(
                        SauceLabsCredentials.getConnectionString(),
                        capabilities);
                verifyDriverIsValid(driver);
            }
            catch (Exception e) {
                LOG.error("Unable to launch RemoteWebDriver for [{}]: {}", server, e.getMessage());
                driver = null;
            }
        }

        return driver;
    }

    private static void verifyDriverIsValid(WebDriver driver) throws WebDriverException {
        if (driver.getWindowHandle() != null) {
            LOG.debug("Successfully launched RemoteWebDriver");
        } else {
            throw new WebDriverException("driver.getWindowHandle() returned null.");
        }
    }

    private static DesiredCapabilities constructDefaultCapabilities(String jobName, TargetWebBrowser target) {
        String seleniumVersion = ConfigurationValue.getConfigurationValue("REMOTE_SERVER_VERSION", "2.19.0");

        DesiredCapabilities capabilities = new DesiredCapabilities(
                target.browser, target.version, getPlatform(target));
        capabilities.setCapability("name", jobName);
        capabilities.setCapability("tags", SystemName.getSystemName());
        capabilities.setCapability("build", uniqueId);
        capabilities.setCapability("selenium-version", seleniumVersion);

        return capabilities;
    }

    private static Platform getPlatform(TargetWebBrowser target) {
        Platform platform = Platform.WINDOWS;

        /** TODO SauceLabs-specific. May need to update in future. */
        if (target.isInternetExplorer() && target.version.contains("9")) {
            platform = Platform.VISTA;
        }

        return platform;
    }

    private static void addCustomCapabilities(DesiredCapabilities capabilities, Map<String, Object> customCapabilities) {
        if (customCapabilities != null) {
            for (String customCapability : customCapabilities.keySet()) {
                capabilities.setCapability(customCapability, customCapabilities.get(customCapability));
            }
        }
    }

}
