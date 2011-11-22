package com.dynacrongroup.webtest.test;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.util.List;

import com.dynacrongroup.webtest.SauceLabsCredentials;
import com.dynacrongroup.webtest.util.ConfigurationValue;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.BeanToJsonConverter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InSauceTest {

	private static final Logger log = LoggerFactory
			.getLogger(InSauceTest.class);

	@Test
	public void testsaucelabsfirefox() throws Exception {

		DesiredCapabilities capabillities = new DesiredCapabilities("iexplore",
				"7", Platform.WINDOWS);
		capabillities.setCapability("name", "Hello, Sauce OnDemand!");
		capabillities.setCapability("tags", "test-tag");
		capabillities.setCapability("build", "12345");

		log.info("capabilities: "
				+ new BeanToJsonConverter().convert(capabillities));
 		WebDriver driver = new RemoteWebDriver(SauceLabsCredentials.getConnectionString(), capabillities);
		try {
			String jobId = ((RemoteWebDriver) driver).getSessionId().toString();
			log.info("Job ID: [{}]", jobId);
			driver.get("http://www.google.com");
			WebElement search = driver.findElement(By.name("q"));
			Actions builder = new Actions(driver);
			builder.moveToElement(search);
			builder.perform();
			search.sendKeys("Hello, WebDriver");
			search.submit();
			log.info(driver.getTitle());
		} finally {

			driver.quit();
		}
	}
}
