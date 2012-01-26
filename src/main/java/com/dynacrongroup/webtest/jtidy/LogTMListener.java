package com.dynacrongroup.webtest.jtidy;

import org.slf4j.Logger;
import org.w3c.tidy.TidyMessage;

/**
 * A TidyMessageListener that uses a log to report failures.
 */
public class LogTMListener extends AbstractTMListener {

    private Logger log;

    /**
     * Constructs a tidy message listener that reports messages of the given level or higher to the log at info level.
     *
     * @param log   A log that will be reported to
     * @param thresholdLevel The minimum severity level that will be displayed.
     */
    public LogTMListener(Logger log, TidyMessage.Level thresholdLevel) {
        super(thresholdLevel);
        this.log = log;
    }

    /**
     * Constructs a tidy message listener that reports messages of the given level or higher to the log at info level.
     * Uses default AbstractTMListener threshold
     *
     * @param log   A log that will be reported to
     */
    public LogTMListener(Logger log) {
        super();
        this.log = log;
    }

    /**
     * A stub to provide a common interface.  Used to show that verification is complete for a
     * given run.
     * @throws AssertionError    Never thrown
     */
    @Override
    public void verify() throws AssertionError {
        log.info("Tidy verification complete.  Review log above for results.");
    }

    @Override
    protected void reportMessage(TidyMessage tidyMessage) {
        log.info(formatTidyMessage(tidyMessage) );
    }
}
