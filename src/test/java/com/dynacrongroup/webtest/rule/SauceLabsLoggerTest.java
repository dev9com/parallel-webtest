package com.dynacrongroup.webtest.rule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * User: yurodivuie
 * Date: 3/13/12
 * Time: 9:51 AM
 */
public class SauceLabsLoggerTest {

    String fakeMethod = "contextTest";
    SauceLabsLogger rule;
    Description description;
    WebDriver driver;

    @Before
    public void prepareMocksInRule() {
        driver = mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class));
        description = mock(Description.class);
        when(description.getMethodName()).thenReturn(fakeMethod);
        rule = new SauceLabsLogger(driver);
    }

    @Test
    public void testStarting() {
        rule.starting(description);
        verify((JavascriptExecutor)driver).executeScript(contains(fakeMethod + " started"));
    }

    @Test
    public void testPassing() {
        rule.succeeded(description);
        verify((JavascriptExecutor)driver).executeScript(contains(fakeMethod + " passed"));
    }

    @Test
    public void testFailing() {
        rule.failed(new Throwable(), description);
        verify((JavascriptExecutor)driver).executeScript(contains(fakeMethod + " failed"));
    }
}
