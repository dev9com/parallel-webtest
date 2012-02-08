package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.WebDriverUtilities;
import com.dynacrongroup.webtest.util.Path;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Sample WebDriver test case.
 * 
 */
@RunWith(ParallelRunner.class)
public class JobUrlTest extends WebDriverBase {
	Path p = new Path("www.google.com", 80);

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
            assertThat(this.getJobURL(), equalTo(""));
        }
    }

    @Test
    public void verifyTwiceToEnsureNotNulledInConstructor() {
        this.verifyJobUrlContainsSessionID();
    }
}
