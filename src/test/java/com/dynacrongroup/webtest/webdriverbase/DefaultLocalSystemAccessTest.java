package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.base.ParallelRunner;
import com.dynacrongroup.webtest.base.ParameterCombination;
import com.dynacrongroup.webtest.base.WebDriverBase;
import com.dynacrongroup.webtest.util.Path;
import org.junit.Before;
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

	public DefaultLocalSystemAccessTest(ParameterCombination parameterCombination) {
        super(parameterCombination);
	}

	String _(String path) {
		return p.translate(path);
	}

    @Before
    public void getPage() {
        if (!driver.getCurrentUrl().contains("dynacrong")) {
            driver.get(p._("/webtest.html"));
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
