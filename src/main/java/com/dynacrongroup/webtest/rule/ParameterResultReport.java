package com.dynacrongroup.webtest.rule;

import com.dynacrongroup.webtest.browser.WebDriverConfig;
import com.dynacrongroup.webtest.driver.WebDriverWrapper;
import com.dynacrongroup.webtest.util.WebDriverUtilities;
import org.apache.commons.lang.StringUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * User: yurodivuie
 * Date: 5/16/13
 * Time: 10:32 AM
 */
public class ParameterResultReport extends TestWatcher {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterResultReport.class);

    private final WebDriverConfig driverConfig;
    private final WebDriverWrapper driverWrapper;

    public ParameterResultReport(WebDriverConfig driverConfig, WebDriverWrapper driverWrapper) {
        this.driverConfig = driverConfig;
        this.driverWrapper = driverWrapper;
    }

    @Override
    protected void succeeded(Description description) {
        super.succeeded(description);
        logResult(description, "pass");
    }

    @Override
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);
        logResult(description, "fail");
    }

    private void logResult(Description description, String result) {
        LOG.info(StringUtils.join(buildCombinationData(description, result), ","));
    }

    private List<String> buildCombinationData(Description description, String finalStatus) {
        final String jobUrl = WebDriverUtilities.getJobUrl(driverConfig, driverWrapper.getDriver());
        return Arrays.asList(description.getDisplayName(),finalStatus, jobUrl);
    }
}
