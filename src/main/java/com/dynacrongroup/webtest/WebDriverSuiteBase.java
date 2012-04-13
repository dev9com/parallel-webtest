package com.dynacrongroup.webtest;

import com.dynacrongroup.webtest.annotation.Experimental;
import com.dynacrongroup.webtest.browser.TargetWebBrowser;
import com.dynacrongroup.webtest.driver.WebDriverWrapper;
import com.dynacrongroup.webtest.rule.SuiteFinishDriverCloser;
import com.dynacrongroup.webtest.util.ConfigurationValue;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * User: yurodivuie
 * Date: 4/3/12
 * Time: 4:54 PM
 */

// TODO: Figure out how to parallelize this.
@Experimental
public class WebDriverSuiteBase {

    private static final Logger LOG = LoggerFactory.getLogger(WebDriverSuiteBase.class);

    private static WebDriverWrapper WEBDRIVER_WRAPPER;

    @ClassRule
    public static final SuiteFinishDriverCloser SUITE_DRIVEN_DRIVER_CLOSER =
            new SuiteFinishDriverCloser();

    @BeforeClass
    public static void setSuiteDriven() {
        LOG.info("Running a suite.");
        System.getProperties().setProperty("SUITE_DRIVEN", Boolean.TRUE.toString());
    }

    public static Boolean inSuiteRun() {
        String suiteDrivenConfig = ConfigurationValue.getConfigurationValue("SUITE_DRIVEN", Boolean.FALSE.toString());
        return Boolean.valueOf(suiteDrivenConfig);
    }

    public static WebDriverWrapper getDriverWrapper(TargetWebBrowser targetWebBrowser) {
        if (WEBDRIVER_WRAPPER == null) {
            WEBDRIVER_WRAPPER = new WebDriverWrapper( getNewSuiteJobName(), targetWebBrowser);
            SUITE_DRIVEN_DRIVER_CLOSER.setWebDriverWrapper(WEBDRIVER_WRAPPER);
        }

        return WEBDRIVER_WRAPPER;
    }

    private static String getNewSuiteJobName() {
        return SystemName.getSystemName() + "-" + new Date().getTime();
    }
}
