package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.rule.ClassLoadedWebDriverProvider;
import com.dynacrongroup.webtest.rule.HtmlUnitWebDriverProvider;
import com.dynacrongroup.webtest.rule.RemoteWebDriverProvider;
import com.dynacrongroup.webtest.rule.WebDriverProvider;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * User: yurodivuie
 * Date: 3/13/12
 * Time: 2:55 PM
 */
public class WebDriverProviderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverProviderFactory.class);
    private WebDriverLauncher launcher;


    public WebDriverProviderFactory(Logger log) {
        this.launcher = new WebDriverLauncher(log);
    }

    public WebDriverProvider getProvider(String jobName, TargetWebBrowser targetWebBrowser, Map<String, Object> customCapabilities) {

        WebDriverProvider provider = null;

        if (targetWebBrowser.isHtmlUnit()) {
            WebDriver driver = launcher.getHtmlUnitDriver();
            provider = new HtmlUnitWebDriverProvider(driver);
        }
        else if (targetWebBrowser.isClassLoaded()) {
            WebDriver driver = launcher.getClassLoadedDriver(targetWebBrowser.version);
            provider = new ClassLoadedWebDriverProvider(driver);
        }
        else if (targetWebBrowser.isRemote()) {
            WebDriver driver = launcher.getRemoteDriver(jobName, targetWebBrowser, customCapabilities);
            provider = new RemoteWebDriverProvider(driver);
        }
        else {
            LOG.error("{} not recognized as a valid driver to provide.", targetWebBrowser.humanReadable());
        }

        return provider;
    }

}
