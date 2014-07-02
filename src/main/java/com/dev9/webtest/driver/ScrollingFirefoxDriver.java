package com.dev9.webtest.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.List;

/**
 * User: yurodivuie
 * Date: 12/8/12
 * Time: 1:30 PM
 */
public class ScrollingFirefoxDriver extends FirefoxDriver {

    public ScrollingFirefoxDriver() {
    }

    public ScrollingFirefoxDriver(FirefoxProfile profile) {
        super(profile);
    }

    public ScrollingFirefoxDriver(Capabilities desiredCapabilities) {
        super(desiredCapabilities);
    }

    public ScrollingFirefoxDriver(Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
        super(desiredCapabilities, requiredCapabilities);
    }

    public ScrollingFirefoxDriver(FirefoxBinary binary, FirefoxProfile profile) {
        super(binary, profile);
    }

    public ScrollingFirefoxDriver(FirefoxBinary binary, FirefoxProfile profile, Capabilities capabilities) {
        super(binary, profile, capabilities);
    }

    public ScrollingFirefoxDriver(FirefoxBinary binary, FirefoxProfile profile, Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
        super(binary, profile, desiredCapabilities, requiredCapabilities);
    }

    @Override
    public List<WebElement> findElements(By by) {
        return ScrollingRemoteWebElement.convert(super.findElements(by));
    }

    @Override
    public WebElement findElement(By by) {
        return ScrollingRemoteWebElement.convert(super.findElement(by));
    }

    @Override
    public WebElement findElement(String by, String using) {
        return ScrollingRemoteWebElement.convert(super.findElement(by, using));
    }

    @Override
    public List<WebElement> findElements(String by, String using) {
        return ScrollingRemoteWebElement.convert(super.findElements(by, using));
    }

}
