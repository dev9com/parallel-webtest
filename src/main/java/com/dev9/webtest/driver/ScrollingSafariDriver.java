package com.dev9.webtest.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;

import java.util.List;

/**
 * User: yurodivuie
 * Date: 12/8/12
 * Time: 1:31 PM
 */
public class ScrollingSafariDriver extends SafariDriver {

    public ScrollingSafariDriver() {
    }

    public ScrollingSafariDriver(Capabilities desiredCapabilities) {
        super(desiredCapabilities);
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
