package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.WebDriverUtilities;
import com.dynacrongroup.webtest.util.Path;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * Sample WebDriver test case showing how to use custom capabilities.
 *
 */
@RunWith(ParallelRunner.class)
public class CustomCapabilitiesTest extends WebDriverBase {
	Path p = new Path("www.dynacrongroup.com", 80);

    public static Map<String, Object> customCapabilities;

    @BeforeClass
    public static void setCapabilities() {
        customCapabilities = new HashMap<String, Object>();
        customCapabilities.put("name", "veryCustomName");
        customCapabilities.put("fakeCapability", "What happens if I enter a bad capability?");

        Map<String, String> customData = new HashMap<String,String>();
        customData.put("release", "experimental");

        customCapabilities.put("custom-data", customData);
    }


	public CustomCapabilitiesTest(String browser, String browserVersion) {
		super(browser, browserVersion, customCapabilities);
	}

    @Before
    public void loadPage() {
        assumeTrue(!this.getTargetWebBrowser().isClassLoaded());
        if (!driver.getTitle().startsWith("Webtest")) {
            driver.get(p._("/webtest.html"));
        }
    }

    /**
     * Test must be manually verified, since capabilities are sent to Sauce and not queryable.
     * @throws Exception
     */
	@Test
	public void capabilitySetTest() throws Exception {

        getLogger().info("Starting test [{}]", name.getMethodName());
		assertTrue(WebDriverUtilities.isElementPresent(driver,
                By.tagName("h2")));
    }
}
