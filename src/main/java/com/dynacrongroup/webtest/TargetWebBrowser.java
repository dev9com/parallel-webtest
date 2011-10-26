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

	public TargetWebBrowser(String browser, String version) {
		this.browser = browser;
		this.version = version;
	}

	public boolean isInternetExplorer() {
		return (this.browser.contains(INTERNET_EXPLORER) || (this.browser
				.equalsIgnoreCase(BYCLASS) && this.version
				.endsWith(InternetExplorerDriver.class.getName())));
	}

	public boolean isFirefox() {
		return (this.browser.contains(FIREFOX) || (this.browser
				.equalsIgnoreCase(BYCLASS) && this.version
				.endsWith(FirefoxDriver.class.getName())));
	}

	public boolean isChrome() {
		return (this.browser.contains(GOOGLE_CHROME) || (this.browser
				.equalsIgnoreCase(BYCLASS) && this.version
				.endsWith(ChromeDriver.class.getName())));
	}

	public boolean isSafari() {
		return this.browser.contains(SAFARI);
	}

	public boolean isClassLoaded() {
		return this.browser.contains(BYCLASS);
	}

	public boolean isHtmlUnit() {
		return (this.browser.equalsIgnoreCase(BYCLASS) && this.version
				.contains("htmlunit"));
	}

	public String humanReadable() {
		return this.browser + ":" + this.version;
	}
}
