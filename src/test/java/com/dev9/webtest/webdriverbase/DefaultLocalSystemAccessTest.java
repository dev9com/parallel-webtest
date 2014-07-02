package com.dev9.webtest.webdriverbase;

import com.dev9.webtest.WebDriverBase;
import com.dev9.webtest.parameter.ParallelRunner;
import com.dev9.webtest.parameter.ParameterCombination;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * Sample WebDriver test case.
 *
 */
@RunWith(ParallelRunner.class)
public class DefaultLocalSystemAccessTest extends WebDriverBase {
    private final String url = "http://htmlpreview.github.io/?https://github.com/dev9com/parallel-webtest/blob/master/src/test/resources/webtest.html";

	public DefaultLocalSystemAccessTest(ParameterCombination parameterCombination) {
        super(parameterCombination);
	}

    @Before
    public void getPage() {
        assumeTrue(!getWebDriverConfig().isHtmlUnit());
        if (!driver.getCurrentUrl().contains("del9")) {
            driver.get(url);
        }
        else {
            driver.navigate().refresh();
        }
        new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
    }

	@Test
	public void firstTest() throws Exception {
		assertTrue(driver.getPageSource().contains("Dev9"));
	}

	@Test
	public void secondTest() throws Exception {
		assertTrue(driver.getPageSource().contains("Group"));
	}

	@Test
	public void thirdTest() throws Exception {
		assertTrue(driver.getPageSource().contains("Group"));
	}

	@Test
	public void browserCheck() throws Exception {
		getLogger().info("Browser: " + getWebDriverConfig());
	}

	@Test
	public void flagByBrowser() throws Exception {
		if (this.getWebDriverConfig().isInternetExplorer()) {
			getLogger().info("This is an Internet Explorer test!");
		}
		if (this.getWebDriverConfig().isChrome()) {
			getLogger().info("This is a Chrome test!");
		}
		if (this.getWebDriverConfig().isFirefox()) {
			getLogger().info("This is a Firefox test!");
		}
		if (this.getWebDriverConfig().isHtmlUnit()) {
			getLogger().info("This is a HTMLUnit test!");
		}
		if (this.getWebDriverConfig().isSafari()) {
			getLogger().info("This is a Safari test!");
		}
	}
}
