package com.dynacrongroup.webtest.rule;

import com.dynacrongroup.webtest.driver.WebDriverWrapper;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class manages the driver life cycle for a RemoteWebDriver.
 * <p/>
 * User: yurodivuie
 * Date: 3/11/12
 * Time: 9:56 AM
 */
public class ClassFinishDriverCloser extends AbstractClassFinishRule {

    private static final Logger LOG = LoggerFactory.getLogger(ClassFinishDriverCloser.class);

    WebDriverWrapper wrapper;

    public ClassFinishDriverCloser(WebDriverWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    protected void classFinished(Description description) {
        LOG.trace("Test class finished.");
        wrapper.killDriver();
    }
}
