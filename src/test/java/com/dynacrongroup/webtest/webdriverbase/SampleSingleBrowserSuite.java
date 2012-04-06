package com.dynacrongroup.webtest.webdriverbase;

import com.dynacrongroup.webtest.WebDriverSuite;
import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.runner.RunWith;

/**
 * User: yurodivuie
 * Date: 4/4/12
 * Time: 3:54 PM
 */

@RunWith(ClasspathSuite.class)
@ClasspathSuite.ClassnameFilters(".*Test")
public class SampleSingleBrowserSuite extends WebDriverSuite {

    //Suite runs all tests ending in "Test" using a single browser.

}
