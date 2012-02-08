package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.WebDriverUtilities;
import com.dynacrongroup.webtest.util.Path;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

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
        if (!driver.getTitle().startsWith("Dynacron Group")) {
            driver.get(p._(""));
        }
    }

	@Test
	public void isElementPresentTest() throws Exception {
        getLogger().info("Starting test [{}]", name.getMethodName());
        getLogger().info("JobUrl is [{}]", getJobURL());
		assertTrue(WebDriverUtilities.isElementPresent(driver,
				By.id("site-title")));
		assertFalse(WebDriverUtilities.isElementPresent(driver,
				By.id("magic-dragons")));
	}

	@Test
//    @Ignore("getting connection refused from one browser; investigating")
	public void waitForElementTest() throws Exception {
        getLogger().info("Starting test [{}]", name.getMethodName());
        getLogger().info("JobUrl is [{}]", getJobURL());
		driver.findElement(By.id("site-title")).click();
		WebDriverUtilities.waitForElement(driver, By.id("site-title"));
	}

	@Test
//	@Ignore("timeout problem...?")
	public void isTextPresentTest() throws Exception {
        getLogger().info("Starting test [{}]", name.getMethodName());
        getLogger().info("JobUrl is [{}]", getJobURL());
		assertTrue(WebDriverUtilities.isTextPresent(driver, "Dynacron Group"));
		assertFalse(WebDriverUtilities.isTextPresent(driver,
				"ponies in a field"));
	}
}
