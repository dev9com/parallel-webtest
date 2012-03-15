package com.dynacrongroup.webtest.browser;

import java.util.Map;

/**
 * User: yurodivuie
 * Date: 3/15/12
 * Time: 10:03 AM
 */
public interface TargetWebBrowser {

    static final String INTERNET_EXPLORER = "iexplore";
    static final String FIREFOX = "firefox";
    static final String GOOGLE_CHROME = "chrome";
    static final String SAFARI = "safari";
    static final String BYCLASS = "byclass";

    String getBrowser();

    String getVersion();

    Map<String, Object> getCustomCapabilities();

    boolean isInternetExplorer();

    boolean isFirefox();

    boolean isChrome();

    boolean isSafari();

    boolean isClassLoaded();

    boolean isRemote();

    boolean isHtmlUnit();

    boolean hasCustomCapabilities();

    String humanReadable();
}
