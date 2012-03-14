package com.dynacrongroup.webtest.driver;

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
 * Simple extension of RemoteWebDriver that implements the "TakesScreenshot" interface.
 *
 * User: yurodivuie
 * Date: 3/7/12
 * Time: 11:09 AM
 */
public class CapturingRemoteWebDriver extends RemoteWebDriver implements TakesScreenshot {

    /**
     * Pass through to RemoteWebDriver constructor.
     * @param executor
     * @param desiredCapabilities
     */
    public CapturingRemoteWebDriver(CommandExecutor executor, Capabilities desiredCapabilities) {
        super(executor, desiredCapabilities);
    }

    /**
     * Pass through to RemoteWebDriver constructor.
     */
    public CapturingRemoteWebDriver() {
        super();
    }

    /**
     * Pass through to RemoteWebDriver constructor.
     * @param desiredCapabilities
     */
    public CapturingRemoteWebDriver(Capabilities desiredCapabilities) {
        super(desiredCapabilities);
    }

    /**
     * Pass through to RemoteWebDriver constructor.
     * @param connectionString
     * @param capabilities
     */
    public CapturingRemoteWebDriver(URL connectionString, DesiredCapabilities capabilities) {
        super(connectionString, capabilities);
    }

    /**
     * Takes a screenshot using the selected selenium output type.
     * @param target
     * @param <X>
     * @return
     * @throws WebDriverException
     */
    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        if ((Boolean) getCapabilities().getCapability(CapabilityType.TAKES_SCREENSHOT)) {
            String base64Str = execute(DriverCommand.SCREENSHOT).getValue().toString();

            return target.convertFromBase64Png(base64Str);
        }
        return null;
    }
}
