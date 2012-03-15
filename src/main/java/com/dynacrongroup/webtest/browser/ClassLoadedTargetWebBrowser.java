package com.dynacrongroup.webtest.browser;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.util.Map;

/**
 * Represents a target browser/browser version combination. With
 * parallel-webtest, the browser may either be specified by a name (e.g.
 * "firefox") when talking to SauceLabs, or it may be specified by a class name
 * to load a specific local WebDriver instance.
 */
public class ClassLoadedTargetWebBrowser implements TargetWebBrowser {

    /**
     * What browser?
     */
    private String browser;

    private Map<String, Object> customCapabilities;

    /**
     * Create a new target browser/version combination.
     *
     * @param className
     */
    public ClassLoadedTargetWebBrowser(String className, Map<String, Object> customCapabilities) {
        this.browser = className;
        this.customCapabilities = customCapabilities;
    }

    /**
     * Returns the driver class name.
     *
     * @return
     */
    @Override
    public String getBrowser() {
        return browser;
    }

    /**
     * Class loaded browsers have no specified version, so it always returns null.
     *
     * @return
     */
    @Override
    public String getVersion() {
        return null;
    }

    /**
     * Returns the custom capabilities, if any.
     *
     * @return
     */
    @Override
    public Map<String, Object> getCustomCapabilities() {
        return customCapabilities;
    }

    /**
     * Returns true if the browser is iexplore or the version contains the
     * InternetExplorerDriver.
     *
     * @return
     */
    @Override
    public boolean isInternetExplorer() {
        return (browser.endsWith(InternetExplorerDriver.class.getName()));
    }

    /**
     * Returns true if the browser is firefox or the version contains the
     * FirefoxDriver.
     *
     * @return
     */
    @Override
    public boolean isFirefox() {
        return (this.browser.endsWith(FirefoxDriver.class.getName()));
    }

    /**
     * Returns true if the browser is chrome or the version contains the
     * ChromeDriver.
     *
     * @return
     */
    @Override
    public boolean isChrome() {
        return (this.browser.endsWith(ChromeDriver.class.getName()));
    }

    /**
     * Returns false: there is no local safari driver at the moment.
     *
     * @return
     */
    @Override
    public boolean isSafari() {
        return false;
    }

    /**
     * Returns true if the target is class loaded (and hence run in a local browser).
     *
     * @return
     */
    @Override
    public boolean isClassLoaded() {
        return true;
    }

    /**
     * Returns true if the target is for a remote browser (run using a Selenium Server).
     *
     * @return
     */
    @Override
    public boolean isRemote() {
        return false;
    }

    /**
     * Returns true if the target is for a local htmlunit browser.
     *
     * @return
     */
    @Override
    public boolean isHtmlUnit() {
        return (this.browser.contains("htmlunit"));
    }

    /**
     * Returns whether any custom capabilities are specified.
     * @return
     */
    public boolean hasCustomCapabilities() {
        return (customCapabilities != null && !customCapabilities.isEmpty());
    }

    /**
     * Returns the browser name.
     *
     * @return A human-readable string representing the parameters used for test
     */
    @Override
    public String humanReadable() {
        String[] splitVersion = this.browser.split("\\.");
        return splitVersion[splitVersion.length - 1];
    }
}
