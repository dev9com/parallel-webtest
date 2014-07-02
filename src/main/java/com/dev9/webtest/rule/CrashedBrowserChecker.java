package com.dev9.webtest.rule;

import com.dev9.webtest.driver.WebDriverWrapper;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks whether the browser appears to be crashed.  If so, it destroys the driver so that it will be recreated
 * when the constructor is executed.
 *
 * User: yurodivuie
 * Date: 4/5/12
 * Time: 5:12 PM
 */
public class CrashedBrowserChecker extends TestWatcher {

    private static final Logger LOG = LoggerFactory.getLogger(CrashedBrowserChecker.class);

    private WebDriverWrapper wrapper;

    public CrashedBrowserChecker(WebDriverWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    protected void starting(Description description) {
        if (wrapper.isCrashed()) {
            wrapper.rebuildDriver();
        }
    }

}
