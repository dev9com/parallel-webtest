package com.dynacrongroup.webtest.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
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
 * browser.
 * 
 * This test suite is configured to use local browsers, not SauceLabs. This
 * makes this test suite highly environment dependent (for example, MSIE is not
 * available for Mac OS X or Linux).
 * 
 * If you are running this test suite on your local environment, you may find it
 * easier to just flag these tests as @Ignore wait until this test is refactored
 * to use SauceLabs.
 * 
 * @author drakdr1
 */
// TODO: Redo to use SauceLabs to remove environment dependencies.
public class MouseOverTest {

	private static final Logger log = LoggerFactory
			.getLogger(MouseOverTest.class);

	@Test
	@Ignore("Does not work on CI")
	public void iexploreTest() throws Exception {

		if (System.getProperty("mrj.version") != null) {
			log.warn("Running on Mac OS X, no iexplore available. Skipping test.");
			return;
		}
		WebDriver driver = new InternetExplorerDriver();
		driver.get("http://www.google.com");
		WebElement search = driver.findElement(By.name("q"));
		Actions builder = new Actions(driver);
		builder.moveToElement(search);
		builder.perform();
		search.sendKeys("Hello, WebDriver");
		search.submit();
		log.info(">" + driver.getTitle());

		assertNotNull(driver.getTitle());
		driver.quit();
	}

	/**
	 * If you are running on Mac OS X, the chromedriver is not available by
	 * default. You need to install and configure the chromedriver yourself.
	 */
	@Test
	@Ignore("Does not work on CI")
	public void chromeTest() throws Exception {

		if (System.getProperty("mrj.version") != null) {
			log.warn("Running on Mac OS X, chromedriver not available by default.  Skipping test.");
			return;
		}

		WebDriver driver = new ChromeDriver();
		driver.get("http://www.google.com");
		WebElement search = driver.findElement(By.name("q"));
		Actions builder = new Actions(driver);
		builder.moveToElement(search);
		builder.perform();
		search.sendKeys("Hello, WebDriver");
		search.submit();
		log.info(">" + driver.getTitle());

		assertNotNull(driver.getTitle());
		driver.quit();
	}

	@Test
	@Ignore("Does not work on CI")
	public void firefoxTestAlt() throws Exception {

		FirefoxProfile profile = new FirefoxProfile();
		profile.setEnableNativeEvents(false);

		WebDriver driver = new FirefoxDriver();
		driver.get("http://www.google.com");
		WebElement search = driver.findElement(By.name("q"));
		Actions builder = new Actions(driver);
		builder.moveToElement(search);
		builder.perform();
		search.sendKeys("Hello, WebDriver");
		search.submit();
		log.info(">" + driver.getTitle());

		assertNotNull(driver.getTitle());
		driver.quit();
	}

	@Test
	@Ignore("Does not work on CI")
	public void firefoxTest() throws Exception {
		WebDriver driver = new FirefoxDriver();
		driver.get("http://www.google.com");
		WebElement search = driver.findElement(By.name("q"));
		Actions builder = new Actions(driver);
		builder.moveToElement(search);
		builder.perform();
		search.sendKeys("Hello, WebDriver");
		search.submit();
		log.info(">" + driver.getTitle());

		assertNotNull(driver.getTitle());
		driver.quit();
	}
}
