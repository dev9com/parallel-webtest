package com.dynacrongroup.webtest.browser;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created with IntelliJ IDEA.
 * User: yurodivuie
 * Date: 10/30/12
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */


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