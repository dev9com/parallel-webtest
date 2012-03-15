package com.dynacrongroup.webtest.browser;

import java.util.Map;

/**
 * Factory generates the target web browser object from the given parameters.
 *
 * User: yurodivuie
 * Date: 3/15/12
 * Time: 10:12 AM
 */
public final class TargetWebBrowserFactory {

    private TargetWebBrowserFactory() {
        throw new IllegalAccessError("Utility class should not be constructed.");
    }

    /**
     * Get the target web browser object for a given set of test case parameters.
     *
     * @param browser   Byclass, for local drivers, or the browser name, for remote browsers.
     * @param version   The class name, for local drivers, or the browser version, for remote browsers.
     * @param customCapabilities    Custom capabilities (if any).  Can be null.
     * @return
     */
    public static TargetWebBrowser getTargetWebBrowser(String browser,
                                                       String version,
                                                       Map<String, Object> customCapabilities) {

        TargetWebBrowser target;

        if (TargetWebBrowser.BYCLASS.equalsIgnoreCase(browser)) {
            target = new ClassLoadedTargetWebBrowser(version, customCapabilities);
        }
        else {
            target = new RemoteTargetWebBrowser(browser, version, customCapabilities);
        }

        return target;

    }

}
