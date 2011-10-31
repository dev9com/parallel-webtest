package com.dynacrongroup.webtest.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.WebDriverUtilities;
import com.dynacrongroup.webtest.util.Path;

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

	@Test
	public void isElementPresentTest() throws Exception {
		driver.get(p._("/"));
		assertTrue(WebDriverUtilities.isElementPresent(driver,
				By.cssSelector(".menu-container")));
		assertFalse(WebDriverUtilities.isElementPresent(driver,
				By.id("magic-dragons")));
	}

	@Test
	public void waitForElementTest() throws Exception {
		driver.get(p._("/"));
		driver.findElement(By.cssSelector("div#logo")).click();
		WebDriverUtilities.waitForElement(driver, By.id("righttopcolumn"));
	}

	@Test
//	@Ignore("timeout problem...?") //Didn't happen for me... suggest retesting in cloudbees...
	public void isTextPresentTest() throws Exception {
		driver.get(p._("/"));
		assertTrue(WebDriverUtilities.isTextPresent(driver, "Dynacron Group"));
		assertFalse(WebDriverUtilities.isTextPresent(driver,
				"ponies in a field"));
	}
}
