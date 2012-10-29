package com.dynacrongroup.webtest.browser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Joiner;
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

    public enum Browser {
        IEXPLORE(org.openqa.selenium.ie.InternetExplorerDriver.class),
        FIREFOX(org.openqa.selenium.firefox.FirefoxDriver.class),
        CHROME(org.openqa.selenium.chrome.ChromeDriver.class),
        SAFARI(org.openqa.selenium.safari.SafariDriver.class),
        OPERA(null),
        HTMLUNIT(org.openqa.selenium.htmlunit.HtmlUnitDriver.class);

        private Class driverClass;

        public Class getDriverClass() {
            return driverClass;
        }

        private Browser(Class driverClass) {
            this.driverClass = driverClass;
        }

        @JsonCreator
        public static Browser fromJson(String text) {
            return valueOf(text.toUpperCase());
        }
    }

    public Type type;
    public Browser browser;
    public String version = "";
    public String browserLocal = "";
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

    public String getBrowserLocal() {
        return browserLocal;
    }

    public void setBrowserLocal(String browserLocal) {
        this.browserLocal = browserLocal;
    }

    @Override
    public String toString() {
        return humanReadable();
    }
}
