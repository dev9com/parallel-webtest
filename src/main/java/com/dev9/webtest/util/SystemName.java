package com.dev9.webtest.util;

/**
 * Identifies the fully-qualified domain name of this machine.
 *
 * You will want to set the value to the fully-qualified domain name of the
 * machine. For example, if your machine is GHQ123, set the webtest.hostname
 * value to GHQ123.domain.org. This domain name should resolve on your machine.
 */
public final class SystemName {

    public static final String WEBTEST_HOSTNAME = "webtest.hostname";

	private static String systemName = null;


	/** Makes checkstyle happy */
	private SystemName() {
        throw new IllegalAccessError("Utility class should not be constructed");
	}

	/**
	 * What is the fully-qualified domain name of this machine?
	 */
	public static String getSystemName() {
		init();
		return systemName;
	}

	private static void init() {
		if (systemName == null) {
            systemName = Configuration.getConfig().getString(WEBTEST_HOSTNAME);
		}
	}
}
