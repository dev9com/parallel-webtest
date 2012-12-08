package com.dynacrongroup.webtest.browser;

import com.dynacrongroup.webtest.driver.ScrollingChromeDriver;
import com.dynacrongroup.webtest.driver.ScrollingFirefoxDriver;
import com.dynacrongroup.webtest.driver.ScrollingInternetExplorerDriver;
import com.dynacrongroup.webtest.driver.ScrollingSafariDriver;
import com.dynacrongroup.webtest.util.Configuration;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.typesafe.config.Config;

public enum Browser {
    IEXPLORE(org.openqa.selenium.ie.InternetExplorerDriver.class, ScrollingInternetExplorerDriver.class),
    FIREFOX(org.openqa.selenium.firefox.FirefoxDriver.class, ScrollingFirefoxDriver.class),
    CHROME(org.openqa.selenium.chrome.ChromeDriver.class, ScrollingChromeDriver.class),
    SAFARI(org.openqa.selenium.safari.SafariDriver.class, ScrollingSafariDriver.class),
    OPERA(null, null),
    HTMLUNIT(org.openqa.selenium.htmlunit.HtmlUnitDriver.class, org.openqa.selenium.htmlunit.HtmlUnitDriver.class);

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