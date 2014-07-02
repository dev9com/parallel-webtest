package com.dev9.webtest.browser;

import com.dev9.webtest.driver.ScrollingChromeDriver;
import com.dev9.webtest.driver.ScrollingFirefoxDriver;
import com.dev9.webtest.driver.ScrollingInternetExplorerDriver;
import com.dev9.webtest.driver.ScrollingSafariDriver;
import com.dev9.webtest.util.Configuration;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.opera.core.systems.OperaDriver;
import com.typesafe.config.Config;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public enum Browser {
    CHROME(org.openqa.selenium.chrome.ChromeDriver.class, ScrollingChromeDriver.class),
    FIREFOX(org.openqa.selenium.firefox.FirefoxDriver.class, ScrollingFirefoxDriver.class),
    PHANTOMJS(PhantomJSDriver.class, PhantomJSDriver.class),
    HTMLUNIT(org.openqa.selenium.htmlunit.HtmlUnitDriver.class, org.openqa.selenium.htmlunit.HtmlUnitDriver.class),
    IEXPLORE(org.openqa.selenium.ie.InternetExplorerDriver.class, ScrollingInternetExplorerDriver.class),
    OPERA(OperaDriver.class, OperaDriver.class),
    SAFARI(org.openqa.selenium.safari.SafariDriver.class, ScrollingSafariDriver.class);

    private static final String SCROLL_SAFE_KEY = "scroll-safe";
    private static final Config CONF = Configuration.getConfig();
    public static final Boolean SCROLL_SAFE = CONF.getBoolean(SCROLL_SAFE_KEY);

    private Class driverClass;
    private Class scrollingDriverClass;

    public Class getDriverClass() {
        return (SCROLL_SAFE) ? scrollingDriverClass : driverClass;
    }

    private Browser(Class driverClass, Class scrollingDriverClass) {
        this.driverClass = driverClass;
        this.scrollingDriverClass = scrollingDriverClass;
    }

    @JsonCreator
    public static Browser fromJson(String text) {
        return valueOf(text.toUpperCase());
    }
}