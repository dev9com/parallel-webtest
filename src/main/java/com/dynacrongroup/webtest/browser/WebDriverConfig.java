package com.dynacrongroup.webtest.browser;

import com.dynacrongroup.webtest.util.Configuration;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.ConfigurationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WebDriverConfig {

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverConfig.class);
    private static final String MSG_CONFIGURING_PROXY = "Configuring proxy..";
    public static final String ERROR_MISSING_INITIAL_URL = "You must supply an initial browser url to successfully proxy InternetExplorerDriver.";

    private Proxy proxy;

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


    public void setEnableNativeEventsForFirefox() {

        FirefoxProfile profile;

        if (customCapabilities.containsKey(FirefoxDriver.PROFILE)) {
            profile = (FirefoxProfile)customCapabilities.get(FirefoxDriver.PROFILE);
            profile.setEnableNativeEvents(true);
        }
        else {
            profile = new FirefoxProfile();
        }
        customCapabilities.put(FirefoxDriver.PROFILE, profile);
    }

    public void configureProxySettings(WebDriverConfig webDriverConfig) throws ConfigurationException {

        Browser browser = webDriverConfig.browser;

        String proxyHost = Configuration.getConfig().getString("proxyHost");
        String proxyPort = Configuration.getConfig().getString("proxyPort");

        if(!proxyHost.isEmpty() && !proxyPort.isEmpty()){

            LOG.info(MSG_CONFIGURING_PROXY);

            if(browser.equals(Browser.FIREFOX)){

                configureFirefoxProxy(proxyHost, proxyPort);
            }
            else if(browser.equals(Browser.CHROME)){

                configureChromeProxy(proxyHost, proxyPort);
            }
            else if(browser.equals(Browser.IEXPLORE)){

            // TODO: Not working in RemoteIEDriver. See Saucelabs ticket #5666
                //configureInternetExplorerProxy(proxyHost, proxyPort);
            }
        }
    }

    @Override
    public String toString() {
        String platformString = platform == null ? "" : platform.toString();
        return Joiner.on('|').join(browser.name().toLowerCase(), version, platformString);
    }

    private void addBrowserLocaleToCustomCapabilities() {
        switch(browser) {
            case FIREFOX:   addBrowserLocaleForFirefox(); break;
            case CHROME:    addBrowserLocaleForChrome();  break;
        }
    }

    private void addBrowserLocaleForFirefox() {

        FirefoxProfile profile = getFirefoxProfile();
        profile.setPreference("intl.accept_languages", browserLocale.toString());
        customCapabilities.put(FirefoxDriver.PROFILE, profile);
    }

    private void configureChromeProxy(String proxyHost, String proxyPort) throws ConfigurationException{

        if(null == proxy){
            proxy = new Proxy();
        }

        proxy.setProxyAutoconfigUrl("http://".concat(proxyHost).concat(":").concat(proxyPort));
        customCapabilities.put(CapabilityType.BROWSER_NAME,DesiredCapabilities.chrome().getBrowserName());
        customCapabilities.put(CapabilityType.PROXY,proxy);
    }

    //TODO: Not working with Saucelabs. See saucelabs support ticket #5666
    private void configureInternetExplorerProxy(String proxyHost, String proxyPort) throws ConfigurationException {

        String initialUrl = Configuration.getConfig().getString("initialBrowserUrl");
        if(StringUtils.isEmpty(initialUrl)){
            throw new ConfigurationException(ERROR_MISSING_INITIAL_URL);
        }

        if(null == proxy){
            proxy = new Proxy();
        }

        String proxyUrl = proxyHost.concat(":").concat(proxyPort);
        proxy.setProxyType(Proxy.ProxyType.MANUAL);
        proxy.setHttpProxy(proxyUrl)
        .setHttpsProxy(proxyUrl)
        .setSslProxy(proxyUrl);

        customCapabilities.put(CapabilityType.ACCEPT_SSL_CERTS, true);
        customCapabilities.put("initialBrowserUrl",initialUrl);
        customCapabilities.put(CapabilityType.PROXY,proxy);
    }

    private void configureFirefoxProxy(String proxyHost, String proxyPort) {

        FirefoxProfile profile = getFirefoxProfile();
        profile.setPreference("network.proxy.http", proxyHost);
        profile.setPreference("network.proxy.https", proxyHost);
        profile.setPreference("network.proxy.http_port", Integer.valueOf(proxyPort));
        profile.setPreference("network.proxy.no_proxies_on", "");
        profile.setPreference("network.proxy.type", 1);
        customCapabilities.put(FirefoxDriver.PROFILE, profile);
    }

    private FirefoxProfile getFirefoxProfile() {
        FirefoxProfile profile;
        if (customCapabilities.containsKey(FirefoxDriver.PROFILE)) {
            profile = (FirefoxProfile)customCapabilities.get(FirefoxDriver.PROFILE);
        }
        else {
            profile = new FirefoxProfile();
        }
        return profile;
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
}
