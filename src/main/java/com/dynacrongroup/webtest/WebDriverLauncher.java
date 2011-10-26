package com.dynacrongroup.webtest;

import java.util.UUID;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;

import com.dynacrongroup.webtest.util.ConnectionValidator;

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

		if (target.isClassLoaded()) {
			testLog.debug("Initializing WebDriver by specified class: "
					+ jobName);
			try {
				return (WebDriver) Class.forName(target.version).newInstance();
			} catch (InstantiationException e) {
				testLog.error("Unable to load target WebDriver class.", e);
			} catch (IllegalAccessException e) {
				testLog.error("Unable to load target WebDriver class.", e);
			} catch (ClassNotFoundException e) {
				testLog.error("Unable to load target WebDriver class.", e);
			}
		}

		WebDriver driver = null;

		boolean validSauceWebDriver = false;
		int attempt = 0;

		if (attempt < MAX_RETRIES && !validSauceWebDriver) {
			attempt++;

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
					validSauceWebDriver = true;
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

		if (driver == null) {
			throw new IllegalAccessError(
					"Unable to establish connection to SauceLabs.");
		}
		return driver;
	}
}
