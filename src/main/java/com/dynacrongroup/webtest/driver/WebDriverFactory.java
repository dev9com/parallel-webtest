package com.dynacrongroup.webtest.driver;

import com.dynacrongroup.webtest.browser.WebDriverConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: yurodivuie
 * Date: 3/13/12
 * Time: 2:55 PM
 */
public class WebDriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverFactory.class);

    private WebDriverFactory() {
        //utility class
    }

    public static WebDriver getDriver(String jobName, WebDriverConfig webDriverConfig) {

        LOG.debug("initializing {}", webDriverConfig);

        WebDriverLauncher launcher = new WebDriverLauncher();
        WebDriver driver;

        if (webDriverConfig.isHtmlUnit()) {
            driver = launcher.getHtmlUnitDriver(webDriverConfig);
        }
        else if (webDriverConfig.isClassLoaded()) {
            driver = launcher.getClassLoadedDriver(webDriverConfig);
        }
        else if (webDriverConfig.isRemote()) {
            driver = launcher.getRemoteDriver(jobName, webDriverConfig);
        }
        else {
            throw new WebDriverException(webDriverConfig + " not a valid driver.");
        }

        return driver;
    }


}
