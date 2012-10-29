package com.dynacrongroup.webtest.util;

import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * SauceLabs credentials. Using the standard values as defaults, with an option
 * to override via system properties (e.g. in a Maven profile).
 */
public final class SauceLabsCredentials {

	private static final String SAUCELABS_USER = "saucelabs.user";
	private static final String SAUCELABS_KEY = "saucelabs.key";
	private static final String SAUCELABS_SERVER = "saucelabs.server";

	private static final Logger log = LoggerFactory
			.getLogger(SauceLabsCredentials.class);

	private SauceLabsCredentials() {
        throw new IllegalAccessError("Utility class should not be constructed");
	}

    /**
     * Get the Sauce Labs user from the configuration.
     * @return
     */
	public static String getUser() {
		return Configuration.getConfig().getString(SAUCELABS_USER);
	}

    /**
     * Get the Sauce Labs api key from the configuration.
     * @return
     */
	public static String getKey() {
        return Configuration.getConfig().getString(SAUCELABS_KEY);
	}

	/** This almost never changes, but it can be used to send tests to any selenium server */
	public static String getServer() {
        return Configuration.getConfig().getString(SAUCELABS_SERVER);
	}

    /**
     * Gets the appropriate URL string for the configured credentials, as used by RemoteWebDriver.
     * @return
     */
	public static URL getConnectionString() {
        String user = getUser();
        String key = getKey();

        if (user == null || key == null) {
            throw new WebDriverException("saucelabs.user or saucelabs.key missing and required " +
                    "for Sauce Labs connection.  See README.txt for parallel-webtest library.");
        }

		try {
			return new URL("http://" + getUser() + ":" + getKey() + "@"
					+ getServer());
		} catch (MalformedURLException e) {
			log.error("Unable to parse remote selenium server connection information", e);
            return null;
		}
	}
}
