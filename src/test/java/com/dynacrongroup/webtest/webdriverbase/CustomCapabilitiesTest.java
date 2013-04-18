package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.parameter.ParallelRunner;
import com.dynacrongroup.webtest.parameter.ParameterCombination;
import com.dynacrongroup.webtest.sauce.SauceREST;
import com.dynacrongroup.webtest.util.SauceLabsCredentials;
import com.dynacrongroup.webtest.util.WebDriverUtilities;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

/**
 * Sample WebDriver test case showing how to use custom capabilities.
 */
@RunWith(ParallelRunner.class)
public class CustomCapabilitiesTest extends WebDriverBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCapabilitiesTest.class);

    private final String url = "http://htmlpreview.github.io/?https://github.com/dynacron-group/parallel-webtest/blob/master/src/test/resources/webtest.html";

    public CustomCapabilitiesTest(ParameterCombination parameterCombination) {
        super(parameterCombination);
        LOGGER.info("name: {}", parameterCombination.getWebDriverConfig().customCapabilities.get("name"));
    }

    @Before
    public void loadPage() {
        assumeTrue(this.getWebDriverConfig().isRemote());
        if (!driver.getTitle().startsWith("Webtest")) {
            driver.get(url);
            new WebDriverWait(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
        }
    }

    /**
     * Test demonstrates that custom capabilities were received.
     *
     * @throws Exception
     */
    @Test
    public void capabilitySetTest() throws Exception {
        getLogger().info("Starting test [{}]", testName.getMethodName());
        assertThat(WebDriverUtilities.isElementPresent(driver,
                By.tagName("h2"))).isTrue();

        JSONObject status = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey()).getJobStatus(this.getJobId());

        assertThat((String) status.get("name")).isEqualTo("veryCustomName");
    }

}
