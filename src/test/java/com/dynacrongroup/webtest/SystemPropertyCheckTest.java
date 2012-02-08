package com.dynacrongroup.webtest;

import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPropertyCheckTest {

    private static final Logger log = LoggerFactory
	    .getLogger(SystemPropertyCheckTest.class);

    @Test
    public void listSystemProperties() {
	Properties p = System.getProperties();
	for (Object key : p.keySet()) {
	    log.trace("System Property: " + key.toString() + "="
		    + p.getProperty(key.toString()));
	}

	Map<String, String> env = System.getenv();
	for (String key : env.keySet()) {
	    log.trace("Environment Property: " + key + "=" + env.get(key));
	}
    }

    @Test
    public void anotherCheck() {
	assertTrue(true);
    }
}
