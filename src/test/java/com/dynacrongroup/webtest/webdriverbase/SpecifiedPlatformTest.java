package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.base.ParallelRunner;
import com.dynacrongroup.webtest.base.ParameterCombination;
import com.dynacrongroup.webtest.util.SauceLabsCredentials;
import com.dynacrongroup.webtest.base.WebDriverBase;
import com.dynacrongroup.webtest.sauce.SauceREST;
import com.dynacrongroup.webtest.util.Path;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Sample WebDriver test case.
 *
 */
@RunWith(ParallelRunner.class)
public class SpecifiedPlatformTest extends WebDriverBase {
	Path p = new Path("www.google.com", 80);
    private static final Logger LOG = LoggerFactory.getLogger(SpecifiedPlatformTest.class);
    private Map<Platform, String> platformMap = new HashMap<Platform, String>();

	public SpecifiedPlatformTest(ParameterCombination parameterCombination) {
		super(parameterCombination);
        platformMap.put(Platform.WINDOWS, "Windows 2003");
        platformMap.put(Platform.VISTA, "Windows 2008");
        platformMap.put(Platform.LINUX, "Linux");
        platformMap.put(Platform.XP, "Windows 2003");
	}

    @Before
    public void loadPage() {
        driver.get(p._(""));
    }

	@Test
    public void verifyPlatform() {
        Platform platform = this.getWebDriverConfig().getPlatform();

        if (this.getWebDriverConfig().isRemote()) {
            SauceREST sauceREST = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey());
            JSONObject jobStatus = sauceREST.getJobStatus(this.getJobId());
            assertThat((String)jobStatus.get("os")).isEqualToIgnoringCase(platformMap.get(platform));
        }
        else {
            assertThat(platform.getPartOfOsName()).isEqualTo(Platform.getCurrent().getPartOfOsName());
        }
    }


}

