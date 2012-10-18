package com.dynacrongroup.webtest.driver;

import com.dynacrongroup.webtest.browser.TargetWebBrowser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dynacrongroup.webtest.util.WebDriverUtilities.getJobUrl;

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

    public static WebDriver getDriver(String jobName, TargetWebBrowser targetWebBrowser) {

        LOG.debug("procuring {}", targetWebBrowser.humanReadable());

        WebDriverLauncher launcher = new WebDriverLauncher();
        WebDriver driver;

        if (targetWebBrowser.isHtmlUnit()) {
            driver = launcher.getHtmlUnitDriver(targetWebBrowser);
        }
        else if (targetWebBrowser.isClassLoaded()) {
            driver = launcher.getClassLoadedDriver(targetWebBrowser);
        }
        else if (targetWebBrowser.isRemote()) {
            driver = launcher.getRemoteDriver(jobName, targetWebBrowser);
            LOG.info("View on Sauce Labs at {}", getJobUrl(targetWebBrowser, driver));
        }
        else {
            throw new WebDriverException(targetWebBrowser.humanReadable() + " not a valid driver.");
        }

        return driver;
    }


}
