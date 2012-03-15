package com.dynacrongroup.webtest.browser;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Sample WebDriver test case.
 *
 */
public class TargetWebBrowserTest {
    @Test
    public void testIEDriver() {
        TargetWebBrowser tar = TargetWebBrowserFactory.getTargetWebBrowser("byclass", "org.openqa.selenium.ie.InternetExplorerDriver", null);
        assertTrue(tar.isInternetExplorer());
        assertFalse(tar.isChrome());
        assertTrue(tar.isClassLoaded());
    }

    @Test
    public void testFFDriver() {
        TargetWebBrowser tar = TargetWebBrowserFactory.getTargetWebBrowser("byclass","org.openqa.selenium.firefox.FirefoxDriver", null );
        assertTrue(tar.isFirefox());
        assertFalse(tar.isChrome());
        assertTrue(tar.isClassLoaded());
    }

    @Test
    public void testChromeDriver() {
        TargetWebBrowser tar = TargetWebBrowserFactory.getTargetWebBrowser("byclass","org.openqa.selenium.chrome.ChromeDriver", null );
        assertTrue(tar.isChrome());
        assertFalse(tar.isInternetExplorer());
        assertTrue(tar.isClassLoaded());
    }

    @Test
    public void testHtmlDriver() {
        TargetWebBrowser tar = TargetWebBrowserFactory.getTargetWebBrowser("byclass","org.openqa.selenium.htmlunit.HtmlUnitDriver", null );
        assertTrue(tar.isHtmlUnit());
        assertFalse(tar.isInternetExplorer());
        assertTrue(tar.isClassLoaded());
        assertThat(tar.humanReadable(), equalTo("HtmlUnitDriver"));
    }

    @Test
    public void testSauceIE() {
        TargetWebBrowser tar = TargetWebBrowserFactory.getTargetWebBrowser("iexplore","7", null );
        assertTrue(tar.isInternetExplorer());
        assertFalse(tar.isChrome());
        assertFalse(tar.isClassLoaded());
    }

    @Test
    public void testSauceFF() {
        TargetWebBrowser tar = TargetWebBrowserFactory.getTargetWebBrowser("firefox","3.6", null );
        assertTrue(tar.isFirefox());
        assertFalse(tar.isChrome());
        assertFalse(tar.isClassLoaded());
        assertThat(tar.humanReadable(), equalTo("firefox:3.6"));
    }

    @Test
    public void testSauceChrome() {
        TargetWebBrowser tar = TargetWebBrowserFactory.getTargetWebBrowser("chrome","", null );
        assertTrue(tar.isChrome());
        assertFalse(tar.isFirefox());
        assertFalse(tar.isClassLoaded());
        assertThat(tar.humanReadable(), equalTo("chrome:"));
    }

    @Test
    public void testSauceSafari() {
        TargetWebBrowser tar = TargetWebBrowserFactory.getTargetWebBrowser("safari","5", null );
        assertTrue(tar.isSafari());
        assertFalse(tar.isFirefox());
        assertFalse(tar.isClassLoaded());
    }
}
