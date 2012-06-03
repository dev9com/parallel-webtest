package com.dynacrongroup.webtest.browser;

import org.openqa.selenium.Platform;

import java.util.Map;

/**
 * User: yurodivuie
 * Date: 3/15/12
 * Time: 10:03 AM
 */
public interface TargetWebBrowser {

    String INTERNET_EXPLORER = "iexplore";
    String FIREFOX = "firefox";
    String GOOGLE_CHROME = "chrome";
    String SAFARI = "safari";
    String BYCLASS = "byclass";

    String getBrowser();

    String getVersion();

    Platform getPlatform();

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
