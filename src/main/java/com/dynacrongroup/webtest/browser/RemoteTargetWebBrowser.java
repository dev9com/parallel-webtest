package com.dynacrongroup.webtest.browser;

import java.util.Map;

/**
 * Represents a target browser/browser version combination. With
 * parallel-webtest, the browser may either be specified by a name (e.g.
 * "firefox") when talking to SauceLabs, or it may be specified by a class name
 * to load a specific local WebDriver instance.
 */
public class RemoteTargetWebBrowser implements TargetWebBrowser {

    /**
     * What browser?
     */
    private String browser;

    /**
     * What version?
     */
    private String version;

    private Map<String, Object> customCapabilities;

    /**
     * Create a new target browser/version combination.
     *
     * @param browser
     * @param version
     */
    public RemoteTargetWebBrowser(String browser, String version, Map<String, Object> customCapabilities) {
        this.browser = browser;
        this.version = version;
        this.customCapabilities = customCapabilities;
    }

    @Override
    public String getBrowser() {
        return browser;
    }

    @Override
    public String getVersion() {
        return version;
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
        return (this.browser.contains(INTERNET_EXPLORER));
    }

    /**
     * Returns true if the browser is firefox or the version contains the
     * FirefoxDriver.
     *
     * @return
     */
    @Override
    public boolean isFirefox() {
        return (this.browser.contains(FIREFOX));
    }

    /**
     * Returns true if the browser is chrome or the version contains the
     * ChromeDriver.
     *
     * @return
     */
    @Override
    public boolean isChrome() {
        return (this.browser.contains(GOOGLE_CHROME));
    }

    /**
     * Returns true if the browser is safari.
     *
     * @return
     */
    @Override
    public boolean isSafari() {
        return this.browser.contains(SAFARI);
    }

    /**
     * Returns true if the target is class loaded (and hence run in a local browser).
     *
     * @return
     */
    @Override
    public boolean isClassLoaded() {
        return false;
    }

    /**
     * Returns true if the target is for a remote browser (run using a Selenium Server).
     *
     * @return
     */
    @Override
    public boolean isRemote() {
        return true;
    }

    /**
     * Returns true if the target is for a local htmlunit browser.
     *
     * @return
     */
    @Override
    public boolean isHtmlUnit() {
        return false;
    }

    /**
     * Returns whether any custom capabilities are specified.
     * @return
     */
    public boolean hasCustomCapabilities() {
        return (customCapabilities != null && !customCapabilities.isEmpty());
    }

    /**
     * Returns browser:version in string form.
     *
     * @return A human-readable string representing the parameters used for test
     */
    @Override
    public String humanReadable() {
        return browser + ":" + version;
    }
}
