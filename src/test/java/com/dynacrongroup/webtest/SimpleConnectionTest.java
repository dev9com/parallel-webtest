package com.dynacrongroup.webtest;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleConnectionTest {

	private static final Logger log = LoggerFactory
			.getLogger(SimpleConnectionTest.class);

	private boolean isProxyNeeded() {
		boolean result = false;
		if (System.getProperty("http.proxyHost") != null
				&& !System.getProperty("http.proxyHost").isEmpty()) {
			result = true;
		}
		log.debug("Proxy needed:" + result);
		return result;
	}

	@Test
	public void htmlunittest() throws Exception {
		if (isProxyNeeded()) {
			return;
		}
		WebDriver driver = new HtmlUnitDriver();
		driver.get("http://www.google.com");
		WebElement search = driver.findElement(By.name("q"));
		search.sendKeys("Hello, WebDriver");
		search.submit();
		log.info(">" + driver.getTitle());

		assertNotNull(driver.getTitle());
		driver.quit();
	}

	@Test
	public void testGoogleConnection() throws Exception {
		if (isProxyNeeded()) {
			return;
		}
		download("http://www.google.com/");
	}

	@Test
	public void testSauceLabsConnection() throws Exception {
		if (isProxyNeeded()) {
			return;
		}
		download("http://ondemand.saucelabs.com/");
	}

	private void download(String path) throws Exception {
		log.info("Downloading " + path + "...");
		URL u;
		InputStream is = null;
		BufferedReader dis;
		String s;

		u = new URL(path);
		is = u.openStream(); // throws an IOException
		dis = new BufferedReader(new InputStreamReader(is));

		int max_lines = 5;

		s = dis.readLine();
		while (s != null && max_lines > 0) {
			log.trace(s);
			s = dis.readLine();
			max_lines--;
		}

		try {
			is.close();
		} catch (IOException ioe) {
			log.trace("close error", ioe);
		}
	}
}
