package com.dynacrongroup.webtest;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class serves WebDrivers to classes that consume them.  The lifecycle is as follows:
 * <p/>
 * 1. A new WebDriver is created during construction of the WebDriverBase class, at its request. <br />
 * 2. When WebDriveBase or one of its rules requests a driver, it is requested from here.
 * 3. When the driver is destroyed, after the class is finished,
 */
public class WebDriverStorage {

    private final static Logger LOG = LoggerFactory
            .getLogger(WebDriverStorage.class);

    private static Map<String, WebDriver> testDrivers =
            Collections.synchronizedMap(new HashMap<String, WebDriver>());
    private static int totalOpened = 0;
    private static int totalClosed = 0;

    private WebDriverStorage() {
        // Utility class (Checkstyle)
    }


    public static WebDriver getDriver(TestDriverConfiguration configuration) {
        WebDriver driver;
        driver = getStoredDriver(configuration);
        if (driver == null) {
            driver = WebDriverLauncher.getNewWebDriverInstance(configuration);
            addNewDriverToTestDrivers(configuration, driver);
        }
        return driver;
    }

    public static void removeDriver(TestDriverConfiguration configuration) {
        synchronized (testDrivers) {
            removeDriverFromTestDrivers(configuration);
        }
    }

    public static void report() {
        final List<String> openDrivers = listOpenDrivers();

        LOG.trace("WebDriver instances open = " + openDrivers.size());
        LOG.trace("Total sessions opened during run " + totalOpened);
        LOG.trace("Total sessions closed during run " + totalClosed);

        for (String openDriver : openDrivers) {
            LOG.trace("Session currently open from {}", openDriver);
        }
    }

    private static List<String> listOpenDrivers() {
        List<String> openDrivers = new ArrayList<String>();

        synchronized (testDrivers) {
            for (String configString : testDrivers.keySet()) {
                openDrivers.add(configString);
            }
        }

        return openDrivers;
    }

    private static WebDriver getStoredDriver(TestDriverConfiguration configuration) {
        String configString = configuration.toString();
        WebDriver driver = null;
        if (testDrivers.containsKey(configString)) {
            driver = testDrivers.get(configString);
        }
        return driver;
    }

    private static void addNewDriverToTestDrivers(TestDriverConfiguration configuration, WebDriver driver) {
        String configString = configuration.toString();
        if (!testDrivers.containsKey(configString)) {
            testDrivers.put(configString, driver);
            totalOpened++;
        } else {
            LOG.warn("Driver already exists for configuration {}", configString);
        }
    }

    private static void removeDriverFromTestDrivers(TestDriverConfiguration configuration) {
        String configString = configuration.toString();
        if (testDrivers.containsKey(configString)) {
            WebDriver driver = testDrivers.get(configString);
            if (driver != null) {
                driver.quit();
            }
            testDrivers.remove(configString);
            totalClosed++;
            LOG.trace("WebDriver shut down. " + listOpenDrivers().size()
                    + " still running.");
        }
    }

}
