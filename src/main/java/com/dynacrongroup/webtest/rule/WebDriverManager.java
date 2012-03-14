package com.dynacrongroup.webtest.rule;

import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;

/**
 * Interface for a WebDriverManager Test Rule.
 *
 * User: yurodivuie
 * Date: 3/13/12
 * Time: 3:01 PM
 */
public interface WebDriverManager extends TestRule {
    WebDriver getDriver();

    String getJobURL();

    String getJobId();
}
