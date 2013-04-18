package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.parameter.ParallelRunner;
import com.dynacrongroup.webtest.parameter.ParameterCombination;
import com.dynacrongroup.webtest.util.WebDriverUtilities;
import com.google.common.base.Function;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

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
    private final String url = "http://htmlpreview.github.io/?https://github.com/dynacron-group/parallel-webtest/blob/master/src/test/resources/webtest.html";


    public WebDriverUtilitiesTest(ParameterCombination parameterCombination) {
		super(parameterCombination);
	}

    @Before
    public void loadPage() {
        if (!driver.getTitle().startsWith("Webtest")) {
            driver.get(url);
            new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
        }
    }

	@Test
	public void isElementPresentTest() throws Exception {
        getLogger().info("Starting test [{}]", testName.getMethodName());
		assertTrue(WebDriverUtilities.isElementPresent(driver,
                By.tagName("h2")));
		assertFalse(WebDriverUtilities.isElementPresent(driver,
				By.id("magic-dragons")));
	}

	@Test
	public void waitForElementTest() throws Exception {
        getLogger().info("Starting test [{}]", testName.getMethodName());
        driver.navigate().refresh();
		WebDriverUtilities.waitForElement(driver, By.tagName("h2"));
	}

	@Test
	public void isTextPresentTest() throws Exception {
        getLogger().info("Starting test [{}]", testName.getMethodName());
		assertTrue(WebDriverUtilities.isTextPresent(driver, "Dynacron Group"));
		assertFalse(WebDriverUtilities.isTextPresent(driver,
				"ponies in a field"));
	}

    @Test
    public void isTextPresentInElementUsingDriverTest() throws Exception {
        getLogger().info("Starting test [{}]", testName.getMethodName());
        assertTrue(WebDriverUtilities.isTextPresentInElement(driver, By.tagName("h1"), "Header"));
        assertFalse(WebDriverUtilities.isTextPresentInElement(driver, By.tagName("h1"), "Montage"));
    }

    @Test
    public void isTestPresentInElementUsingElementTest() throws Exception {
        getLogger().info("Starting test [{}]", testName.getMethodName());

        WebElement element = driver.findElement(By.tagName("h1"));
        assertTrue(WebDriverUtilities.isTextPresentInElement(element, "Header"));
        assertFalse(WebDriverUtilities.isTextPresentInElement(element, "Montage"));
    }

    @Test
    public void reduceToOneWindowTest() throws Exception {
        getLogger().info("Starting test [{}]", testName.getMethodName());
        String testUrl = "http://www.google.com/";


        assertThat(driver.getWindowHandles().size(), equalTo(1));
        ((JavascriptExecutor)driver).executeScript("window.open(\"" + testUrl + "\")");
        assertThat(driver.getWindowHandles().size(), equalTo(2));
        WebDriverUtilities.reduceToOneWindow(driver);
        assertThat(driver.getWindowHandles().size(), equalTo(1));

    }

    @Test
    public void switchToNewPopUpTest() {
        getLogger().info("Starting test [{}]", testName.getMethodName());
        final String testUrl = "http://www.dynacrongroup.com/";

        ((JavascriptExecutor)driver).executeScript("window.open(\"" + testUrl + "\")");
        assertThat(driver.getWindowHandles().size(), equalTo(2));
        WebDriverUtilities.switchToNewPopUp(driver);
        driver.get(testUrl);    //redundant, but necessary for phantom-js due to its speed, to detect the page load.

        new FluentWait<WebDriver>(driver)
                .withTimeout(5, TimeUnit.SECONDS)
                .pollingEvery(250, TimeUnit.MILLISECONDS)
                .withMessage("Current url was not set to testUrl: " + testUrl)
                .until(new Function<WebDriver, String>() {
            public String apply(WebDriver driver) {
                String currentUrl = driver.getCurrentUrl();
                if (currentUrl.equalsIgnoreCase(testUrl)) {
                    return currentUrl;
                }
                return null;
            }
        });
    }

}
