package com.dynacrongroup.webtest.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;

import java.util.List;

/**
 * User: yurodivuie
 * Date: 12/8/12
 * Time: 1:31 PM
 */
public class ScrollingInternetExplorerDriver extends InternetExplorerDriver {

    public ScrollingInternetExplorerDriver() {
    }

    public ScrollingInternetExplorerDriver(Capabilities capabilities) {
        super(capabilities);
    }

    public ScrollingInternetExplorerDriver(int port) {
        super(port);
    }

    public ScrollingInternetExplorerDriver(InternetExplorerDriverService service) {
        super(service);
    }

    public ScrollingInternetExplorerDriver(InternetExplorerDriverService service, Capabilities capabilities) {
        super(service, capabilities);
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
