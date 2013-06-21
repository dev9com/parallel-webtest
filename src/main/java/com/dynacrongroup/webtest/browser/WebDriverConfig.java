package com.dynacrongroup.webtest.browser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Joiner;
import org.openqa.selenium.Platform;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


    public Type type = Type.LOCAL;

    public Browser browser = Browser.FIREFOX;
    public String version = "";
    public BrowserLocale browserLocale = new BrowserLocale();
    public Platform platform = Platform.getCurrent();
    public Map<String, Object> customCapabilities = new HashMap<String, Object>();
    public boolean isInternetExplorer() {
        return (this.browser.equals(Browser.IEXPLORE));
    }

    public boolean isFirefox() {
        return (this.browser.equals(Browser.FIREFOX));
    }

    public boolean isChrome() {
        return (this.browser.equals(Browser.CHROME));
    }

    public boolean isSafari() {
        return this.browser.equals(Browser.SAFARI);
    }

    public boolean isOpera() {
        return this.browser.equals(Browser.OPERA);
    }

    public boolean isPhantomJS() {
        return this.browser.equals(Browser.PHANTOMJS);
    }

    public boolean isClassLoaded() {
        return Type.LOCAL.equals(type);
    }

    public boolean isRemote() {
        return Type.REMOTE.equals(type);
    }

    public boolean isHtmlUnit() {
        return this.browser.equals(Browser.HTMLUNIT);
    }

    public boolean hasCustomCapabilities() {
        return customCapabilities != null && !customCapabilities.isEmpty();
    }

    public String humanReadable() {
        String platformString = platform == null ? "" : platform.toString();
        return Joiner.on('|').join(browser.name().toLowerCase(), version, platformString);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
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

    public void setCustomCapabilities(Map<String,Object> customCapabilities) { //TODO: worry about merging.
        this.customCapabilities.putAll(customCapabilities);
    }


    public BrowserLocale getBrowserLocale() {
        return browserLocale;
    }

    public void setBrowserLocale(BrowserLocale browserLocale) {
        this.browserLocale = browserLocale;
        addBrowserLocaleToCustomCapabilities();
    }

    public void setEnableNativeEvents() {

        if (customCapabilities.containsKey(FirefoxDriver.PROFILE)) {
            FirefoxProfile profile = (FirefoxProfile)customCapabilities.get(FirefoxDriver.PROFILE);
            profile.setEnableNativeEvents(true);
            customCapabilities.put(FirefoxDriver.PROFILE, profile);
        }
    }

    private void addBrowserLocaleToCustomCapabilities() {
        switch(browser) {
            case FIREFOX:   addBrowserLocaleForFirefox(); break;
            case CHROME:    addBrowserLocaleForChrome();  break;
        }
    }

    private void addBrowserLocaleForFirefox() {
        FirefoxProfile profile;
        if (customCapabilities.containsKey(FirefoxDriver.PROFILE)) {
            profile = (FirefoxProfile)customCapabilities.get(FirefoxDriver.PROFILE);
        }
        else {
            profile = new FirefoxProfile();
        }
        profile.setPreference("intl.accept_languages", browserLocale.toString());
        customCapabilities.put(FirefoxDriver.PROFILE, profile);
    }

    private void addBrowserLocaleForChrome() {
        List<String> chromeSwitches = new ArrayList<String>();
        chromeSwitches.add("--lang=" + browserLocale.toString());
        if (customCapabilities.containsKey("chrome.switches")) {
            ((List<String>)customCapabilities.get("chrome.switches")).addAll(chromeSwitches);
        }
        else {
            customCapabilities.put("chrome.switches", chromeSwitches);
        }
    }

    @Override
    public String toString() {
        return humanReadable();
    }
}
