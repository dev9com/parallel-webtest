package com.dynacrongroup.webtest.util;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: yurodivuie
 * Date: 3/5/12
 * Time: 2:11 PM
 */
public class PathTest {

    private static final Logger LOG = LoggerFactory.getLogger(PathTest.class);

    @Test
    public void testLocalhost() {
        Path p = new Path("localhost");
        assertTrue(p.isLocal());
    }

    @Test
    @Ignore("This is only the case if mapped in the host file.")
    public void testSystemName() {
        LOG.info("using {}", SystemName.getSystemName());

        Path p = new Path(SystemName.getSystemName());
        assertTrue(p.isLocal());
    }


    @Test
    public void testGoogle() {
        Path p = new Path("www.google.com");
        assertFalse(p.isLocal());
    }

    @Test
    public void testNumbers() {
        Path p = new Path("127.0.0.1");
        assertTrue(p.isLocal());
    }
}
