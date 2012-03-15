package com.dynacrongroup.webtest.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a simple timer, used to track how long tests are taking to run. The logback-test-xml file
 * shows how to configure this test to generate a CSV file containing these timings.
 */
public class Timing {


    private final static Logger log = LoggerFactory.getLogger(Timing.class);
    /**
     * Counters for timing
     */
    private final String timerName;
    private long startTime;

    /**
     * Duration between timer start/stop.
     */
    private Long duration = null;

    /**
     * Creates new timer which is logged with the given name.
     * @param timerName
     */
    public Timing(String timerName) {
        this.timerName = timerName;
    }

    /**
     * Records start time.
     */
    public void start() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Logs duration of method to timing log.
     */
    public void stop() {
        duration = System.currentTimeMillis() - startTime;
        log.trace(timerName + "," + duration);
    }

    public Long getDuration() {
        return duration;
    }
}
