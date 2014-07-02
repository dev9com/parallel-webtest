package com.dev9.webtest.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.List;

/**
 * User: yurodivuie
 * Date: 12/8/12
 * Time: 10:33 AM
 */
public class ScrollingRemoteWebDriver extends CapturingRemoteWebDriver {

    /**
     * Pass through to RemoteWebDriver constructor.
     * @param executor
     * @param desiredCapabilities
     */
    public ScrollingRemoteWebDriver(CommandExecutor executor, Capabilities desiredCapabilities) {
        super(executor, desiredCapabilities);
    }

    /**
     * Pass through to RemoteWebDriver constructor.
     */
    public ScrollingRemoteWebDriver() {
        super();
    }

    /**
     * Pass through to RemoteWebDriver constructor.
     * @param desiredCapabilities
     */
    public ScrollingRemoteWebDriver(Capabilities desiredCapabilities) {
        super(desiredCapabilities);
    }

    /**
     * Pass through to RemoteWebDriver constructor.
     * @param connectionString
     * @param capabilities
     */
    public ScrollingRemoteWebDriver(URL connectionString, DesiredCapabilities capabilities) {
        super(connectionString, capabilities);
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
