package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.WebDriverUtilities;
import com.dynacrongroup.webtest.util.Path;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Sample WebDriver test case.
 *
 */
@RunWith(ParallelRunner.class)
public class WebDriverUtilitiesTest extends WebDriverBase {
	Path p = new Path("www.dynacrongroup.com", 80);

	public WebDriverUtilitiesTest(String browser, String browserVersion) {
		super(browser, browserVersion);
	}

    @Before
    public void loadPage() {
        if (!driver.getTitle().startsWith("Webtest")) {
            driver.get(p._("/webtest.html"));
        }
    }

	@Test
	public void isElementPresentTest() throws Exception {
        getLogger().info("Starting test [{}]", name.getMethodName());
		assertTrue(WebDriverUtilities.isElementPresent(driver,
                By.tagName("h2")));
		assertFalse(WebDriverUtilities.isElementPresent(driver,
				By.id("magic-dragons")));
	}

	@Test
	public void waitForElementTest() throws Exception {
        getLogger().info("Starting test [{}]", name.getMethodName());
        driver.navigate().refresh();
		WebDriverUtilities.waitForElement(driver, By.tagName("h2"));
	}

	@Test
	public void isTextPresentTest() throws Exception {
        getLogger().info("Starting test [{}]", name.getMethodName());
		assertTrue(WebDriverUtilities.isTextPresent(driver, "Dynacron Group"));
		assertFalse(WebDriverUtilities.isTextPresent(driver,
				"ponies in a field"));
	}

    @Test
    public void isTestPresentInElementUsingDriverTest() throws Exception {
        getLogger().info("Starting test [{}]", name.getMethodName());
        assertTrue(WebDriverUtilities.isTextPresentInElement(driver, By.tagName("h1"), "Header"));
        assertFalse(WebDriverUtilities.isTextPresentInElement(driver, By.tagName("h1"), "Montage"));
    }

    @Test
    public void isTestPresentInElementUsingElementTest() throws Exception {
        getLogger().info("Starting test [{}]", name.getMethodName());

        WebElement element = driver.findElement(By.tagName("h1"));
        assertTrue(WebDriverUtilities.isTextPresentInElement(element, "Header"));
        assertFalse(WebDriverUtilities.isTextPresentInElement(element, "Montage"));
    }

    @Test
    public void reduceToOneWindowTest() throws Exception {
        getLogger().info("Starting test [{}]", name.getMethodName());
        String testUrl = "http://www.google.com/";


        assertThat(driver.getWindowHandles().size(), equalTo(1));
        ((JavascriptExecutor)driver).executeScript("window.open(\"" + testUrl + "\")");
        assertThat(driver.getWindowHandles().size(), equalTo(2));
        WebDriverUtilities.reduceToOneWindow(driver);
        assertThat(driver.getWindowHandles().size(), equalTo(1));

    }

    @Test
    public void switchToNewPopUpTest() {
        getLogger().info("Starting test [{}]", name.getMethodName());
        String testUrl = "http://www.google.com/";

        ((JavascriptExecutor)driver).executeScript("window.open(\"" + testUrl + "\")");
        assertThat(driver.getWindowHandles().size(), equalTo(2));
        WebDriverUtilities.switchToNewPopUp(driver);
        assertThat(driver.getCurrentUrl(), equalTo(testUrl));
    }

}
