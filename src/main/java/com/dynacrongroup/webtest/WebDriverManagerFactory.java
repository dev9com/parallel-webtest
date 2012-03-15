package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.browser.TargetWebBrowser;
import com.dynacrongroup.webtest.rule.ClassLoadedWebDriverManager;
import com.dynacrongroup.webtest.rule.HtmlUnitWebDriverManager;
import com.dynacrongroup.webtest.rule.RemoteWebDriverManager;
import com.dynacrongroup.webtest.rule.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;

import java.util.Map;

/**
 * User: yurodivuie
 * Date: 3/13/12
 * Time: 2:55 PM
 */
class WebDriverManagerFactory {

    private WebDriverLauncher launcher;
    private Logger log;


    WebDriverManagerFactory(Logger log) {
        this.launcher = new WebDriverLauncher(log);
        this.log = log;
    }

    WebDriverManager getManager(String jobName, TargetWebBrowser targetWebBrowser) {

        log.debug("building {}", targetWebBrowser.humanReadable());
        WebDriverManager manager;

        if (targetWebBrowser.isHtmlUnit()) {
            WebDriver driver = launcher.getHtmlUnitDriver();
            manager = new HtmlUnitWebDriverManager(driver);
        }
        else if (targetWebBrowser.isClassLoaded()) {
            WebDriver driver = launcher.getClassLoadedDriver(targetWebBrowser);
            manager = new ClassLoadedWebDriverManager(driver);
        }
        else if (targetWebBrowser.isRemote()) {
            WebDriver driver = launcher.getRemoteDriver(jobName, targetWebBrowser);
            manager = new RemoteWebDriverManager(driver);
        }
        else {
            throw new WebDriverException(targetWebBrowser.humanReadable() + " not a valid driver.");
        }

        return manager;
    }

}
