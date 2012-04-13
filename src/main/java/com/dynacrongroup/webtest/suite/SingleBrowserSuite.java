package com.dynacrongroup.webtest.suite;

import com.dynacrongroup.webtest.SystemName;
import com.dynacrongroup.webtest.browser.TargetWebBrowser;
import com.dynacrongroup.webtest.browser.TargetWebBrowserFactory;
import com.dynacrongroup.webtest.driver.WebDriverWrapper;
import com.dynacrongroup.webtest.util.ConfigurationValue;
import org.apache.commons.lang.StringUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: yurodivuie
 * Date: 4/13/12
 * Time: 10:50 AM
 */
public class SingleBrowserSuite extends Suite {

    String[] parameters;

    private static Map<String, WebDriverWrapper> wrappers =
            Collections.synchronizedMap(new HashMap<String, WebDriverWrapper>());


    public SingleBrowserSuite(String[] parameters, Class<?>[] classes) throws InitializationError {
        super(new WebDriverSuiteRunnerBuilder(parameters), classes);
        this.parameters = parameters;
        System.getProperties().setProperty("SUITE_DRIVEN", Boolean.TRUE.toString());
    }

    public static WebDriverWrapper getWrapper(TargetWebBrowser targetWebBrowser) {
        if (!wrappers.containsKey(targetWebBrowser.humanReadable())) {
            wrappers.put(targetWebBrowser.humanReadable(), new WebDriverWrapper(getNewSuiteJobName(), targetWebBrowser));
        }
        return wrappers.get(targetWebBrowser.humanReadable());
    }

    public static Boolean inSuiteRun() {
        String suiteDrivenConfig = ConfigurationValue.getConfigurationValue("SUITE_DRIVEN", Boolean.FALSE.toString());
        return Boolean.valueOf(suiteDrivenConfig);
    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);    //To change body of overridden methods use File | Settings | File Templates.
        wrappers.get(TargetWebBrowserFactory.getTargetWebBrowser(parameters[0], parameters[1], null).humanReadable()).killDriver();
    }

    @Override
    protected String getName() {
        String formattedParams;
        if (parameters[1].contains("Driver")) {
            String driver = parameters[1];
            formattedParams = driver.substring(driver.lastIndexOf(".") + 1, driver.lastIndexOf("Driver"));
        } else {

            formattedParams = StringUtils.join(parameters, "|");
        }
        return formattedParams;
    }

    private static String getNewSuiteJobName() {
        return SystemName.getSystemName() + "-" + new Date().getTime();
    }
}
