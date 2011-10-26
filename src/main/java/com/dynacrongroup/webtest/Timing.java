package com.dynacrongroup.webtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This is a simple timer, used to track how long tests are taking to run. The logback-test-xml file
 * shows how to configure this test to generate a CSV file containing these timings. 
 */
public class Timing {

    private final static Logger log = LoggerFactory.getLogger(Timing.class);
    /** Counters for timing */
    private final TargetWebBrowser target;
    private final String testName;
    private long startTime;

    public Timing(TargetWebBrowser target, String testName) {
	this.target = target;
	this.testName = testName;
    }

    public void start() {
	startTime = System.currentTimeMillis();
    }

    public void stop() {
	log.trace(testName + "," + target.humanReadable() + ","
		+ (System.currentTimeMillis() - startTime));
    }
}
