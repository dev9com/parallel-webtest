package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.parameter.ParallelRunner;
import com.dynacrongroup.webtest.parameter.ParameterCombination;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertTrue;

/**
 * Sample WebDriver test case.
 *
 */
@RunWith(ParallelRunner.class)
public class DefaultLocalSystemAccessTest extends WebDriverBase {
    private final String url = "http://htmlpreview.github.io/?https://github.com/dynacron-group/parallel-webtest/blob/master/src/test/resources/webtest.html";

	public DefaultLocalSystemAccessTest(ParameterCombination parameterCombination) {
        super(parameterCombination);
	}

    @Before
    public void getPage() {
        if (!driver.getCurrentUrl().contains("dynacrong")) {
            driver.get(url);
            new WebDriverWait(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
        }
        driver.navigate().refresh();
    }

	@Test
	public void firstTest() throws Exception {
		assertTrue(driver.getPageSource().contains("Dynacron"));
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
		getLogger().info("Browser: " + getWebDriverConfig().humanReadable());
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
