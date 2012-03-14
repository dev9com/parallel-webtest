package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.util.ConfigurationValue;

/**
 * Identifies the fully-qualified domain name of this machine.
 *
 * You will want to set the value to the fully-qualified domain name of the
 * machine. For example, if your machine is GHQ123, set the WEBTEST_HOSTNAME
 * value to GHQ123.domain.org. This domain name should resolve on your machine.
 */
public final class SystemName {

	static private String systemName = null;

	static private final String WEBTEST_HOSTNAME = "WEBTEST_HOSTNAME";

	/** Makes checkstyle happy */
	private SystemName() {
	}

	/**
	 * What is the fully-qualified domain name of this machine?
	 */
	public static String getSystemName() {
		init();
		return systemName;
	}

	private static void init() {
		if (systemName != null) {
			return;
		}
		String result = ConfigurationValue.getConfigurationValue(
				WEBTEST_HOSTNAME, null);
		if (result != null) {
			systemName = result;
			return;
		}
		throw new IllegalArgumentException(
				"No hostname is specified. Please specify a WEBTEST_HOSTNAME value.");
	}
}
