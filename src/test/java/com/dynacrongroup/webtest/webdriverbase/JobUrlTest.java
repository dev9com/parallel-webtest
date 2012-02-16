package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.util.Path;
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

	public JobUrlTest(String browser, String browserVersion) {
		super(browser, browserVersion);
	}
    
    @Before
    public void loadPage() {
        driver.get(p._(""));
    }

	@Test
    public void verifyJobUrlContainsSessionID() {
        if (!this.getTargetWebBrowser().isClassLoaded()) {
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

