package com.dynacrongroup.webtest.rule;

import com.dynacrongroup.webtest.driver.WebDriverWrapper;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class manages the driver life cycle for a RemoteWebDriver.
 * <p/>
 * User: yurodivuie
 * Date: 3/11/12
 * Time: 9:56 AM
 */
public class SuiteFinishDriverCloser extends ExternalResource {

    private static final Logger LOG = LoggerFactory.getLogger(SuiteFinishDriverCloser.class);

    private WebDriverWrapper wrapper;

    public SuiteFinishDriverCloser() {
    }

    public void setWebDriverWrapper(WebDriverWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    protected void after() {
        LOG.info("Suite finished.");
        wrapper.killDriver();
    }
}
