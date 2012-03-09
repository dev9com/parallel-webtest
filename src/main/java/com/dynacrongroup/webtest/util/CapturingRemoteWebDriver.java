package com.dynacrongroup.webtest.util;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

/**
 * User: yurodivuie
 * Date: 3/7/12
 * Time: 11:09 AM
 */
public class CapturingRemoteWebDriver extends RemoteWebDriver implements TakesScreenshot {

    public CapturingRemoteWebDriver(CommandExecutor executor, Capabilities desiredCapabilities) {
        super(executor, desiredCapabilities);
    }

    public CapturingRemoteWebDriver() {
        super();
    }

    public CapturingRemoteWebDriver(Capabilities desiredCapabilities) {
        super(desiredCapabilities);
    }

    public CapturingRemoteWebDriver(URL connectionString, DesiredCapabilities capabilities) {
        super(connectionString, capabilities);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        if ((Boolean) getCapabilities().getCapability(CapabilityType.TAKES_SCREENSHOT)) {
            String base64Str = execute(DriverCommand.SCREENSHOT).getValue().toString();

            return target.convertFromBase64Png(base64Str);
        }
        return null;
    }
}
