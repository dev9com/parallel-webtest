package com.dynacrongroup.webtest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;
import static java.lang.String.format;

/**
 * 
 * @author David Drake
 */
public class WebDriverUtilities {

	/**
	 * Maximum number of seconds to wait when using waitForElement()
	 */
	public static Integer MAX_WAIT_SECONDS = 5;
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
	 * @param driver
	 * @param locator
	 *            Element to search for. If multiple elements for this locator
	 *            exist, this method will still return true.
	 * @return Whether or not the element currently exists and is visible on the
	 *         page.
	 */
	public static Boolean isElementPresent(WebDriver driver, By locator) {
		try {
			WebElement findElement = driver.findElement(locator);
			return findElement.isDisplayed();
		} catch (Exception ex) {
			LOG.trace("element with locator {} not found: {}",
					locator.toString(), ex.getMessage());
			return false;
		}
	}

	/**
	 * Waits for an element to appear on the page, up to MAX_WAIT_SECONDS. Fails
	 * if the element does not appear.
	 * 
	 * @param driver
	 * @param locator
	 *            Element to wait for.
	 */
	public static void waitForElement(WebDriver driver, By locator) {
		for (int i = 0; i < MAX_WAIT_SECONDS; i++) {
			if (isElementPresent(driver, locator)) {
				return;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				LOG.error(ex.getMessage());
			}
		}
		fail(format("Locator [%s] not found in [%s] seconds.",
				locator.toString(), MAX_WAIT_SECONDS));
	}

	public static void switchToNewPopUp(WebDriver driver) {
		String initialHandle = driver.getWindowHandle();
		LOG.debug("Current window handle: [{}]", driver.getWindowHandle());
		Integer handles = driver.getWindowHandles().size();
		LOG.debug("found {} handles; switching to last new one", handles);

		// Integer handles = driver.getWindowHandles().size();
		// LOG.debug("found {} handles; switching to last one", handles);
		// driver.switchTo().window((String)driver.getWindowHandles().toArray()[handles
		// - 1]);
		//
		// LOG.debug("New window handle: [{}]", driver.getWindowHandle());

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
			LOG.info("Call to switchToPopUp failed to find new window handle -"
					+ " staying on current window.");
		}
	}

	/**
	 * Verifies that text is present in the body of a page. Case sensitive.
	 * 
	 * @param driver
	 * @param text
	 *            Text to search for. Case sensitive, and white-space sensitive
	 * @return True if the text is contained in the body tag as text; otherwise
	 *         false.
	 */
	public static Boolean isTextPresent(WebDriver driver, String text) {
		return isTextPresentInElement(driver, By.tagName("body"), text);
	}

	/**
	 * Verifies that text is present in the given locator. Case sensitive.
	 * 
	 * @param driver
	 * @param locator
	 *            Locator to search within.
	 * @param text
	 *            Text to search for. Case sensitive, and white-space sensitive
	 * @return True if the text is contained in the given locator as text; otherwise
	 *         false.
	 */
	public static Boolean isTextPresentInElement(WebDriver driver, By locator,
			String text) {

		Boolean success = false;

		try {
			if (isElementPresent(driver, locator)) {
                WebElement webElement = driver.findElement(locator);
                success = isTextPresentInElement(webElement, text);
			} else {
				LOG.warn("Element [{}] not found; text [{}] not searched for",
						locator.toString(), text);
			}

		} catch (Exception ex) {
			LOG.trace(ex.getMessage());
		}

		return success;
	}

    /**
     * Verifies that text is present in the given element.  Case sensitive.
     * @param webElement
     *          element to search through
     * @param text
     *          Text to search for.  Case sensitive, and white-space
     *          sensitive
     * @return True if the text is contained in the element as text; otherwise
     * false.
     */
    public static Boolean isTextPresentInElement(WebElement webElement,
            String text) {

        return webElement.getText().contains(text);
    }
}
