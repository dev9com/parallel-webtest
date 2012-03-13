package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.util.ConfigurationValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * SauceLabs credentials. Using the standard values as defaults, with an option
 * to override via system properties (e.g. in a Maven profile).
 */
public final class SauceLabsCredentials {

	private static final String SAUCELABS_USER = "SAUCELABS_USER";
	private static final String SAUCELABS_KEY = "SAUCELABS_KEY";
	private static final String SAUCELABS_SERVER = "SAUCELABS_SERVER";

	private static final Logger log = LoggerFactory
			.getLogger(SauceLabsCredentials.class);

	private SauceLabsCredentials() {
        throw new IllegalAccessError("Utility class should not be constructed");
	};

	public static String getUser() {
		return ConfigurationValue.getConfigurationValue(SAUCELABS_USER,
				"No User Set");
	}

	public static String getKey() {
		return ConfigurationValue.getConfigurationValue(SAUCELABS_KEY,
				"No Configuration Key Set");
	}

	/** This almost never changes, but it can be used to send tests to any selenium server */
	public static String getServer() {
		return ConfigurationValue.getConfigurationValue(SAUCELABS_SERVER,
				"ondemand.saucelabs.com/wd/hub");
	}

	public static URL getConnectionString() {
		try {
			return new URL("http://" + getUser() + ":" + getKey() + "@"
					+ getServer());
		} catch (MalformedURLException e) {
			log.error("Unable to parse remote selenium server connection information", e);
            return null;
		}
	}
}
