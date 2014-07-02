package com.dev9.webtest.util;

import com.dev9.webtest.browser.WebDriverConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class that provides methods commonly used but missing from the WebDriver api.
 *
 * @author David Drake
 */
public final class WebDriverUtilities {

    /**
     * Maximum number of seconds to wait when using waitForElement()
     */
    public final static Integer MAX_WAIT_SECONDS = 10;
    private final static Logger LOG = LoggerFactory
            .getLogger(WebDriverUtilities.class);

    private WebDriverUtilities() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Static method for determining whether a given element is present, for
     * assertions. Does not wait for element existence; checks immediately.
     * Fails if element is invisible.
     *
     * @param driver  WebDriver used to execute commands.
     * @param locator Element to search for. If multiple elements for this locator
     *                exist, this method will still return true.
     * @return Whether or not the element currently exists and is visible on the
     *         page.
     */
    public static Boolean isElementPresent(WebDriver driver, By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /**
     * Waits for an element to appear on the page, up to MAX_WAIT_SECONDS. Fails
     * if the element does not appear.
     *
     * @param driver  WebDriver used to execute commands.
     * @param locator Element to wait for.
     */
    public static void waitForElement(WebDriver driver, By locator) {
        waitForElement(driver, locator, MAX_WAIT_SECONDS);
    }

    /**
     * Waits for an element to appear on the page, up to MAX_WAIT_SECONDS. Fails
     * if the element does not appear.  An alternative to using implicit wait.
     *
     * @param driver  WebDriver used to execute commands.
     * @param locator Element to wait for.
     * @param seconds Amount of time in seconds to wait for an element to appear.
     */
    public static void waitForElement(WebDriver driver, final By locator, Integer seconds) {
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(final WebDriver webDriver) {
                return isElementPresent(webDriver, locator);
            }
        });
    }



    /**
     * Utility that tries to find a new window handle and switch to it.
     *
     * @param driver WebDriver used to execute commands.
     */
    public static void switchToNewPopUp(WebDriver driver) {
        String initialHandle = driver.getWindowHandle();
        LOG.trace("Current window handle: [{}]", driver.getWindowHandle());
        Integer handles = driver.getWindowHandles().size();
        LOG.trace("found {} handles; switching to first new one", handles);

        // This code checks for any windows opened other than the current window
        // and switches to them. Will end up on the last window in list.
        if (handles > 1) {
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(initialHandle)) {
                    driver.switchTo().window(handle);
                }
            }
        }

        if (initialHandle.equals(driver.getWindowHandle())) {
            LOG.warn("Call to switchToPopUp failed to find new window handle -"
                    + " staying on current window.");
        }
    }

    /**
     * Verifies that text is present in the body of a page. Case sensitive.
     *
     * @param driver WebDriver used to execute commands.
     * @param text   Text to search for. Case sensitive, and white-space sensitive
     * @return True if the text is contained in the body tag as text; otherwise
     *         false.
     */
    public static Boolean isTextPresent(WebDriver driver, String text) {
        return isTextPresentInElement(driver, By.tagName("body"), text);
    }

    /**
     * Verifies that text is present in the given locator. Case sensitive.
     *
     * @param driver  WebDriver used to execute commands.
     * @param locator Locator to search within.
     * @param text    Text to search for. Case sensitive, and white-space sensitive
     * @return True if the text is contained in the given locator as text; otherwise
     *         false.
     */
    public static Boolean isTextPresentInElement(WebDriver driver, By locator,
                                                 String text) {

        Boolean success = false;

        if (isElementPresent(driver, locator)) {
            WebElement webElement = driver.findElement(locator);
            success = isTextPresentInElement(webElement, text);
        } else {
            LOG.warn("Element [{}] not found; text [{}] not searched for",
                    locator.toString(), text);
        }

        return success;
    }

    /**
     * Verifies that text is present in the given element.  Case sensitive.
     *
     * @param webElement element to search through
     * @param text       Text to search for.  Case sensitive, and white-space
     *                   sensitive
     * @return True if the text is contained in the element as text; otherwise
     *         false.
     */
    public static Boolean isTextPresentInElement(WebElement webElement,
                                                 String text) {
        return webElement.getText().contains(text);
    }

    public static void reduceToOneWindow(WebDriver driver) {
        if (driver != null && driver.getWindowHandles().size() > 1) {
            String firstHandle = (String) driver.getWindowHandles().toArray()[0];
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(firstHandle)) {
                    driver.switchTo().window(handle);
                    driver.close();
                }
            }
            driver.switchTo().window(firstHandle);
        }
    }

    public static String createJobName(Class testClass) {
        return SystemName.getSystemName() + "-" + testClass.getSimpleName();
    }

    public static String getJobId(WebDriver driver) {
        String jobId = null;
        if (RemoteWebDriver.class.isAssignableFrom(driver.getClass())) {
            jobId = ((RemoteWebDriver) driver).getSessionId().toString();
        }
        return jobId;
    }

    public static String getJobUrl(WebDriverConfig target, WebDriver driver) {
        String url = null;

        if (target.isRemote()) {
            url = getJobUrlFromId(getJobId(driver));
        }

        return url;
    }

    public static String getJobUrlFromId(String jobId) {
        return String.format("https://saucelabs.com/jobs/%s", jobId);
    }

}
