package com.dev9.webtest.webdriverbase;

import com.dev9.webtest.parameter.ParallelRunner;
import com.dev9.webtest.parameter.ParameterCombination;
import com.dev9.webtest.WebDriverBase;
import com.dev9.webtest.util.Path;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Sample WebDriver test case.
 *
 */
@RunWith(ParallelRunner.class)
public class JobUrlTest extends WebDriverBase {
	Path p = new Path("www.google.com", 80);

    private static final Logger LOG = LoggerFactory.getLogger(JobUrlTest.class);

	public JobUrlTest(ParameterCombination parameterCombination) {
        super(parameterCombination);
	}

    @Before
    public void loadPage() {
        driver.get(p._(""));
    }

	@Test
    public void verifyJobUrlContainsSessionID() {
        if (this.getWebDriverConfig().isRemote()) {
            assertThat(this.getJobURL(), containsString(((RemoteWebDriver) driver).getSessionId().toString()));
        }
        else {
            assertThat(this.getJobURL(), nullValue());
        }
    }

    @Test
    public void verifyTwiceToEnsureNotNulledInConstructor() {
        this.verifyJobUrlContainsSessionID();
    }
}

