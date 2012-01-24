package com.dynacrongroup.webtest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class keeps track of the allocated WebDriver instances. It is used to
 * help "garbage collect" the instances after use. This is particularly of
 * interest when working with SauceLabs (in particular, to avoid paying for
 * wasted time) but is also helpful for tracking down leaks with local browsers.
 */
public class WebDriverLeakCheck {

    private final static Logger log = LoggerFactory
            .getLogger(WebDriverLeakCheck.class);
    private static Map<WebDriver, Tracker> trackedWebDriver = Collections
            .synchronizedMap(new HashMap<WebDriver, Tracker>());
    private static int totalOpened = 0;
    private static int totalClosed = 0;

    private WebDriverLeakCheck() {
        // Utility class (Checkstyle)
    }

    static class Tracker {

        WebDriver webdriver;
        Throwable owner;
        @SuppressWarnings("rawtypes")
        Class clazz;
    }

    public static int open() {
        return trackedWebDriver.size();
    }

    public static void add(@SuppressWarnings("rawtypes") Class clazz,
                           WebDriver wd) {
        Tracker t = new Tracker();
        t.webdriver = wd;
        t.clazz = clazz;
        t.owner = new Throwable();
        t.owner.fillInStackTrace();

        trackedWebDriver.put(wd, t);
        totalOpened++;
        log.trace("New WebDriver started, now " + trackedWebDriver.size()
                + " running.");
    }

    public static void remove(WebDriver s) {
        s.quit();
        trackedWebDriver.remove(s);
        log.trace("WebDriver shut down. " + WebDriverLeakCheck.open()
                + " still running.");
        totalClosed++;
    }

    public static void report() {
        log.trace("WebDriver instances open = " + trackedWebDriver.size());
        log.trace("Total sessions opened during run " + totalOpened);
        log.trace("Total sessions closed during run " + totalClosed);
        if (trackedWebDriver.isEmpty()) {
            return;
        }

        Iterator<WebDriver> it = trackedWebDriver.keySet().iterator();
        while (it.hasNext()) {
            WebDriver s = it.next();
            Tracker t = trackedWebDriver.get(s);
            log.trace("Session currently open from " + t.clazz.getName());
        }
    }
}
