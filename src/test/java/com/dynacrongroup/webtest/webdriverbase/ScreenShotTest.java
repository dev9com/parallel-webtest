package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.ParallelRunner;
import com.dynacrongroup.webtest.WebDriverBase;
import com.dynacrongroup.webtest.util.Path;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * Sample WebDriver test case.
 */
@RunWith(ParallelRunner.class)
public class ScreenShotTest extends WebDriverBase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    Path p = new Path("www.dynacrongroup.com", 80);

    private static final Logger LOG = LoggerFactory.getLogger(ScreenShotTest.class);

    public ScreenShotTest(String browser, String browserVersion) {
        super(browser, browserVersion);
    }

    @Before
    public void loadPage() {
        assumeTrue(this.getTargetWebBrowser().isRemote());
        if (!driver.getTitle().startsWith("Webtest")) {
            driver.get(p._("/webtest.html"));
        }
        //driver.get(new Path("www.yahoo.com", 80)._(""));
    }

    @Test
    public void verifyJobUrlContainsSessionID() throws IOException {
/*        if (!this.getTargetWebBrowser().isChrome()) {
            driver.manage().window().setSize(new Dimension(1024, 768));
        }*/

        LOG.info(this.getJobName());
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        // Now you can do whatever you need to do with it, for example copy somewhere

        File picture = folder.newFile();
        FileUtils.copyFile(scrFile, picture);
        assertTrue(picture.exists());
    }
}

