package com.dynacrongroup.webtest.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.dynacrongroup.webtest.TargetWebBrowser;

/**
 * Sample WebDriver test case.
 * 
 */
public class TargetWebBrowserTest {
    @Test
    public void testIEDriver() {
        TargetWebBrowser tar = new TargetWebBrowser("byclass","org.openqa.selenium.ie.InternetExplorerDriver" );
        assertTrue(tar.isInternetExplorer());
        assertFalse(tar.isChrome());
        assertTrue(tar.isClassLoaded());
    }
    
    @Test
    public void testFFDriver() {
        TargetWebBrowser tar = new TargetWebBrowser("byclass","org.openqa.selenium.firefox.FirefoxDriver" );
        assertTrue(tar.isFirefox());
        assertFalse(tar.isChrome());
        assertTrue(tar.isClassLoaded());
    }
    
    @Test
    public void testChromeDriver() {
        TargetWebBrowser tar = new TargetWebBrowser("byclass","org.openqa.selenium.chrome.ChromeDriver" );
        assertTrue(tar.isChrome());
        assertFalse(tar.isInternetExplorer());
        assertTrue(tar.isClassLoaded());
    }
    
    @Test
    public void testHtmlDriver() {
        TargetWebBrowser tar = new TargetWebBrowser("byclass","org.openqa.selenium.htmlunit.HtmlunitDriver" );
        assertTrue(tar.isHtmlUnit());
        assertFalse(tar.isInternetExplorer());
        assertTrue(tar.isClassLoaded());
    }
    
    @Test
    public void testSauceIE() {
        TargetWebBrowser tar = new TargetWebBrowser("iexplore","7" );
        assertTrue(tar.isInternetExplorer());
        assertFalse(tar.isChrome());
        assertFalse(tar.isClassLoaded());
    }
    
    @Test
    public void testSauceFF() {
        TargetWebBrowser tar = new TargetWebBrowser("firefox","7" );
        assertTrue(tar.isFirefox());
        assertFalse(tar.isChrome());
        assertFalse(tar.isClassLoaded());
    }
    
    @Test
    public void testSauceChrome() {
        TargetWebBrowser tar = new TargetWebBrowser("chrome","" );
        assertTrue(tar.isChrome());
        assertFalse(tar.isFirefox());
        assertFalse(tar.isClassLoaded());
    }
    
    @Test
    public void testSauceSafari() {
        TargetWebBrowser tar = new TargetWebBrowser("safari","5" );
        assertTrue(tar.isSafari());
        assertFalse(tar.isFirefox());
        assertFalse(tar.isClassLoaded());
    }
}
