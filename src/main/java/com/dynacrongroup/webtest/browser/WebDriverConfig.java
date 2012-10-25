package com.dynacrongroup.webtest.browser;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Platform;

import java.util.HashMap;
import java.util.Map;

/**
 * User: yurodivuie
 * Date: 3/15/12
 * Time: 10:03 AM
 */
public class WebDriverConfig {

    public enum Type {
        LOCAL, REMOTE;

        @JsonCreator
        public static Type fromJson(String text) {
            return valueOf(text.toUpperCase());
        }


    }

    public static final String INTERNET_EXPLORER = "iexplore";
    public static final String FIREFOX = "firefox";
    public static final String GOOGLE_CHROME = "chrome";
    public static final String SAFARI = "safari";
    public static final String HTMLUNIT = "htmlunit";

    public Type type;
    public String browser;
    public String version;
    public Platform platform;
    public Map<String, Object> customCapabilities = new HashMap<String, Object>();

    public boolean isInternetExplorer() {
        return (this.browser.contains(INTERNET_EXPLORER));
    }

    public boolean isFirefox() {
        return (this.browser.contains(FIREFOX));
    }

    public boolean isChrome() {
        return (this.browser.contains(GOOGLE_CHROME));
    }

    public boolean isSafari() {
        return this.browser.contains(SAFARI);
    }

    public boolean isClassLoaded() {
        return Type.LOCAL.equals(type);
    }

    public boolean isRemote() {
        return Type.REMOTE.equals(type);
    }

    public boolean isHtmlUnit() {
        return HTMLUNIT.equalsIgnoreCase(browser);
    }

    public boolean hasCustomCapabilities() {
        return customCapabilities != null && !customCapabilities.isEmpty();
    }

    public String humanReadable() {
        String[] args = {browser,version,platform.toString()};
        return StringUtils.join(args, '|');
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Map<String, Object> getCustomCapabilities() {
        return customCapabilities;
    }

    public void setCustomCapabilities(Map<String, Object> customCapabilities) {
        this.customCapabilities = customCapabilities;
    }
}
