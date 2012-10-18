package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.base.ParallelRunner;
import com.dynacrongroup.webtest.util.SauceLabsCredentials;
import com.dynacrongroup.webtest.base.WebDriverBase;
import com.dynacrongroup.webtest.util.WebDriverUtilities;
import com.dynacrongroup.webtest.sauce.SauceREST;
import com.dynacrongroup.webtest.util.Path;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * Sample WebDriver test case showing how to use custom capabilities.
 */
@RunWith(ParallelRunner.class)
public class CustomCapabilitiesTest extends WebDriverBase {
    public static Map<String, Object> customCapabilities = new HashMap<String, Object>();

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCapabilitiesTest.class);

    Path p = new Path("www.dynacrongroup.com", 80);


    @BeforeClass
    public static void setCapabilities() {
        LOGGER.info("Setting capabilities now.");

        customCapabilities.put("name", "veryCustomName");
        customCapabilities.put("fakeCapability", "What happens if I enter a bad capability?");

        Map<String, String> customData = new HashMap<String, String>();
        customData.put("release", "experimental");

        customCapabilities.put("custom-data", customData);
    }


    public CustomCapabilitiesTest(String browser, String browserVersion) {
        super(browser, browserVersion, customCapabilities);
        LOGGER.info("name: {}", customCapabilities.get("name"));

    }

    @Before
    public void loadPage() {
        assumeTrue(this.getTargetWebBrowser().isRemote());
        if (!driver.getTitle().startsWith("Webtest")) {
            driver.get(p._("/webtest.html"));
        }
    }

    /**
     * Test demonstrates that custom capabilities were received.
     *
     * @throws Exception
     */
    @Test
    public void capabilitySetTest() throws Exception {

        getLogger().info("Starting test [{}]", name.getMethodName());
        assertTrue(WebDriverUtilities.isElementPresent(driver,
                By.tagName("h2")));

        JSONObject status = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey()).getJobStatus(this.getJobId());

        assertThat((String) status.get("name"), equalTo("veryCustomName"));
    }

}
