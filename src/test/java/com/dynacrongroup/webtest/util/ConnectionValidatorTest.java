package com.dynacrongroup.webtest.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: yurodivuie
 * Date: 2/14/12
 * Time: 9:20 AM
 */
public class ConnectionValidatorTest {

    @Test
    public void tryGoogle() {
        assertTrue("Google should be accessible", ConnectionValidator.verifyConnection("http://www.google.com"));
    }

    @Test
    public void tryWithError() {
        assertFalse("Garbage should not be connectible", ConnectionValidator.verifyConnection("http://)(&)(*&"));
    }
}
