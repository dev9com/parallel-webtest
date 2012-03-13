package com.dynacrongroup.webtest.rule;

import com.dynacrongroup.webtest.TestDriverConfiguration;
import com.dynacrongroup.webtest.WebDriverStorage;
import org.junit.runner.Description;

/**
 * Class notes the end of all tests and removes the driver for a specific class/browser combination.
 *
 * User: yurodivuie
 * Date: 3/11/12
 * Time: 9:56 AM
 */
public class DriverCleanUpRule extends ClassFinishRule {

    private final TestDriverConfiguration configuration;

    public DriverCleanUpRule(TestDriverConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void classFinished(Description description) {
        WebDriverStorage.removeDriver(configuration);
    }
}
