package com.dynacrongroup.webtest.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.WebDriverUtilities;
import com.dynacrongroup.webtest.util.Path;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test was added to verify that using native mouse events would not crash the
 * browser.  It's still present for reference.
 * 
 * @author drakdr1
 */

@RunWith(ParallelRunner.class)
public class MouseOverTest extends WebDriverBase {

	private static final Logger log = LoggerFactory
			.getLogger(MouseOverTest.class);

    public MouseOverTest(String browser, String browserVersion) {
        super(browser, browserVersion);
    }

	@Test
	public void nativeEventTest() throws Exception {

        driver.get(new Path("www.google.com", 80)._(""));
		WebElement search = driver.findElement(By.name("q"));
		Actions builder = new Actions(driver);
		builder.moveToElement(search);
		builder.perform();
		search.sendKeys("Hello, WebDriver");
		search.submit();
		log.info(">" + driver.getTitle());

		assertNotNull(driver.getTitle());
	}
}

