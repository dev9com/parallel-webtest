package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.util.Path;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Sample WebDriver test case.
 * 
 */
@RunWith(ParallelRunner.class)
public class DefaultLocalSystemAccessTest extends WebDriverBase {
	Path p = new Path("www.dynacrongroup.com", 80);

	public DefaultLocalSystemAccessTest(String browser, String browserVersion) {
		super(browser, browserVersion);
	}

	String _(String path) {
		return p.translate(path);
	}

	@Test
	public void firstTest() throws Exception {
		driver.get(_("/webtest.html"));
		assertTrue(driver.getPageSource().contains("Dynacron"));
	}

	@Test
	public void secondTest() throws Exception {
		driver.get(_("/webtest.html"));
		assertTrue(driver.getPageSource().contains("Group"));
	}

	@Test
	public void thirdTest() throws Exception {
		driver.get(_("/webtest.html"));
		assertTrue(driver.getPageSource().contains("Group"));
	}

	@Test
	public void browserCheck() throws Exception {
		getLogger().info("Browser: " + this.getTargetWebBrowser().browser);
		getLogger().info("Version: " + this.getTargetWebBrowser().version);
	}

	@Test
	public void flagByBrowser() throws Exception {
		if (this.getTargetWebBrowser().isInternetExplorer()) {
			getLogger().info("This is an Internet Explorer test!");
		}
		if (this.getTargetWebBrowser().isChrome()) {
			getLogger().info("This is a Chrome test!");
		}
		if (this.getTargetWebBrowser().isFirefox()) {
			getLogger().info("This is a Firefox test!");
		}
		if (this.getTargetWebBrowser().isHtmlUnit()) {
			getLogger().info("This is a HTMLUnit test!");
		}
		if (this.getTargetWebBrowser().isSafari()) {
			getLogger().info("This is a Safari test!");
		}
	}
}
