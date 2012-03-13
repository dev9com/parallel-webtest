package com.dynacrongroup.webtest;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

/**
 * Represents a target browser/browser version combination. With
 * parallel-webtest, the browser may either be specified by a name (e.g.
 * "firefox") when talking to SauceLabs, or it may be specified by a class name
 * to load a specific local WebDriver instance.
 */
public class TargetWebBrowser {
	private static final String INTERNET_EXPLORER = "iexplore";
	private static final String FIREFOX = "firefox";
	private static final String GOOGLE_CHROME = "chrome";
	private static final String SAFARI = "safari";
	private static final String BYCLASS = "byclass";

	/** What browser? */
	public String browser;
	/** What version? */
	public String version;

    /**
     * Create a new target browser/version combination.
     * @param browser
     * @param version
     */
	public TargetWebBrowser(String browser, String version) {
		this.browser = browser;
		this.version = version;
	}

    /**
     * Returns true if the browser is iexplore or the version contains the
     * InternetExplorerDriver.
     * @return
     */
	public boolean isInternetExplorer() {
		return (this.browser.contains(INTERNET_EXPLORER) || (this.browser
				.equalsIgnoreCase(BYCLASS) && this.version
				.endsWith(InternetExplorerDriver.class.getName())));
	}

    /**
     * Returns true if the browser is firefox or the version contains the
     * FirefoxDriver.
     * @return
     */
	public boolean isFirefox() {
		return (this.browser.contains(FIREFOX) || (this.browser
				.equalsIgnoreCase(BYCLASS) && this.version
				.endsWith(FirefoxDriver.class.getName())));
	}

    /**
     * Returns true if the browser is chrome or the version contains the
     * ChromeDriver.
     * @return
     */
	public boolean isChrome() {
		return (this.browser.contains(GOOGLE_CHROME) || (this.browser
				.equalsIgnoreCase(BYCLASS) && this.version
				.endsWith(ChromeDriver.class.getName())));
	}

    /**
     * Returns true if the browser is safari.
     * @return
     */
	public boolean isSafari() {
		return this.browser.contains(SAFARI);
	}

    /**
     * Returns true if the target is class loaded (and hence run in a local browser).
     * @return
     */
	public boolean isClassLoaded() {
		return this.browser.contains(BYCLASS);
	}

    /**
     * Returns true if the target is for a remote browser (run using a Selenium Server).
     * @return
     */
    public boolean isRemote() {
        return !isClassLoaded();
    }

    /**
     * Returns true if the target is for a local htmlunit browser.
     * @return
     */
	public boolean isHtmlUnit() {
		return (this.browser.equalsIgnoreCase(BYCLASS) && this.version
				.contains("htmlunit"));
	}

    /**
     * Returns version:browser in string form.  Removes package name from
     * class-loaded drivers.
     * @return  A human-readable string representing the parameters used for test
     */
	public String humanReadable() {
        String readable = browser + ":";

        if (isClassLoaded()) {
            String[] splitVersion = this.version.split("\\.");
            readable += splitVersion[splitVersion.length -1];
        }
        else {
            readable += version;
        }

        return readable;
	}
}
