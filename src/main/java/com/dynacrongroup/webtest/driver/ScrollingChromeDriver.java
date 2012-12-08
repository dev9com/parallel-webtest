package com.dynacrongroup.webtest.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

/**
 * User: yurodivuie
 * Date: 12/8/12
 * Time: 1:31 PM
 */
public class ScrollingChromeDriver extends ChromeDriver {

    public ScrollingChromeDriver() {
    }

    public ScrollingChromeDriver(ChromeDriverService service) {
        super(service);
    }

    public ScrollingChromeDriver(Capabilities capabilities) {
        super(capabilities);
    }

    public ScrollingChromeDriver(ChromeOptions options) {
        super(options);
    }

    public ScrollingChromeDriver(ChromeDriverService service, Capabilities capabilities) {
        super(service, capabilities);
    }

    public ScrollingChromeDriver(ChromeDriverService service, ChromeOptions options) {
        super(service, options);
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
