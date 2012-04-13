package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.WebDriverSuiteBase;
import com.dynacrongroup.webtest.suite.WebDriverParameterizedSuite;
import org.junit.runner.RunWith;

/**
 * User: yurodivuie
 * Date: 4/4/12
 * Time: 3:54 PM
 */

@RunWith(WebDriverParameterizedSuite.class)
public class SampleSingleBrowserSuite extends WebDriverSuiteBase {

    //Suite runs all tests extending WebDriverBase using a single browser instance per parameter

}
