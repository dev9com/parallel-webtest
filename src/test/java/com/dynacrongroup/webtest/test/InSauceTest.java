package com.dynacrongroup.webtest.test;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.util.List;

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
	@Ignore("Need to be updated now that we don't hard-code sauce config")
	public void testsaucelabsfirefox() throws Exception {

		ProxySelector aDefault = ProxySelector.getDefault();
		
		//TODO: Refactor based on standard config values
		List<Proxy> select = aDefault
				.select(new URI("<to do add sauce config>"));

		for (Proxy proxy : select) {
			log.info("proxy [{}]: [{}]", proxy.toString(), proxy.address());
			proxy.toString();
		}

		DesiredCapabilities capabillities = new DesiredCapabilities("iexplore",
				"7", Platform.WINDOWS);
		capabillities.setCapability("name", "Hello, Sauce OnDemand!");
		capabillities.setCapability("tags", "test-tag");
		capabillities.setCapability("build", "12345");

		log.info("capabilities: "
				+ new BeanToJsonConverter().convert(capabillities));
		//TODO: Refactor based on standard config values
		WebDriver driver = new RemoteWebDriver(new URL(
				"<to do add sauce config>"), capabillities);
		try {
			String jobId = ((RemoteWebDriver) driver).getSessionId().toString();
			log.info(jobId);
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
