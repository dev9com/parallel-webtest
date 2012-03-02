package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.util.SauceREST;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Tests SauceREST functions that don't require a job.
 *
 */
public class StaticSauceRESTTest {

    private static final Logger LOG = LoggerFactory.getLogger(StaticSauceRESTTest.class);

    /**
     * This is a special bit of JUnit magic to get the name of the test
     */
    @Rule
    public TestName name = new TestName();

    @Test
    public void accountDetails() {
        LOG.info("Starting [{}]", name.getMethodName());

        JSONObject accountStatus = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey()).getAccountDetails();

        assertThat((String)accountStatus.get("id"), equalTo(SauceLabsCredentials.getUser()));
    }

    @Test
    public void usageData() {
        LOG.info("Starting [{}]", name.getMethodName());

        JSONObject accountUsage = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey()).getUsageData();

        assertThat((Map<String, Object>)accountUsage , hasKey("usage"));
    }

    @Test
    @Ignore("Not yet implemented in Sauce")
    public void sauceStatus() {
        LOG.info("Starting [{}]", name.getMethodName());

        JSONObject status = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey()).getSauceStatus();

        LOG.info(status.toString());
    }

    @Test
    public void sauceBrowsers() {
        LOG.info("Starting [{}]", name.getMethodName());

        JSONArray browsers = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey()).getSauceBrowsers();

        LOG.info(browsers.toString());

        assertThat(browsers.toString(), containsString("firefox"));
    }
}
