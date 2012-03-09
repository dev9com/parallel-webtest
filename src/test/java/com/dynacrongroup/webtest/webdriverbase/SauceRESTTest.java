package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.SauceLabsCredentials;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.WebDriverUtilities;
import com.dynacrongroup.webtest.sauce.SauceREST;
import com.dynacrongroup.webtest.util.Path;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * Sample WebDriver test case showing how to use the rest interface.
 *
 */
@RunWith(ParallelRunner.class)
public class SauceRESTTest extends WebDriverBase {
	Path p = new Path("www.dynacrongroup.com", 80);

    private static final Logger LOG = LoggerFactory.getLogger(SauceRESTTest.class);


	public SauceRESTTest(String browser, String browserVersion) {
		super(browser, browserVersion);
	}

    @Before
    public void loadPage() {
        assumeTrue(this.getTargetWebBrowser().isRemote());
        if (!driver.getTitle().startsWith("Webtest")) {
            driver.get(p._("/webtest.html"));
        }
    }

    /**
     * Verify that a test can be stopped via the rest interface, using status from rest interface.
     * @throws Exception
     */
    @Test
    @Ignore("Calling this manually causes trouble for WebDriver lifecycle.")
    public void stopJobTest() throws Exception {

        getLogger().info("Starting test [{}]", name.getMethodName());
        assertTrue(WebDriverUtilities.isElementPresent(driver, By.tagName("h2")));

        LOG.info("Initial status: [{}] ", new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey()).getJobStatus(this.getJobId()).toString());

        LOG.info("Stop request result: [{}]", new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey()).stopJob(this.getJobId()).toString());

        JSONObject status = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey()).getJobStatus(this.getJobId());
        assertThat((String) status.get("status"), equalTo("complete"));

    }

    @Test
    public void jobsList() {
        LOG.info("Starting [{}]", name.getMethodName());

        JSONArray jobsList = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey()).getAllJobs();

        assertThat(jobsList.toString(), containsString(this.getJobId()));
    }
}
