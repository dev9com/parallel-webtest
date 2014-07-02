package com.dev9.webtest.jtidy;

import org.junit.rules.ErrorCollector;
import org.w3c.tidy.TidyMessage;

/**
 * A TidyMessageListener that uses a JUnit 4.7+ ErrorCollector to report failures.
 */
public class CollectorTMListener extends AbstractTMListener{

    private ErrorCollector errorCollector;

    /**
     * Constructs a tidy message listener that reports messages of the given level or higher to an ErrorCollector
     * as throwables.
     *
     * @param errorCollector    An ErrorCollector that should be used as a com.dev9.webtest.rule from the test case.
     * @param thresholdLevel    The minimum severity level that will be displayed.
     */
    public CollectorTMListener(ErrorCollector errorCollector, TidyMessage.Level thresholdLevel) {
        super(thresholdLevel);
        this.errorCollector = errorCollector;
    }

    /**
     * Constructs a tidy message listener that reports messages of the given level or higher to an ErrorCollector
     * as throwables.  Uses default AbstractTMListener threshold.
     *
     * @param errorCollector    An ErrorCollector that should be used as a com.dev9.webtest.rule from the test case.
     */
    public CollectorTMListener(ErrorCollector errorCollector) {
        super();
        this.errorCollector = errorCollector;
    }


    /**
     * A stub to provide a common interface.  Actual verification is part of the ErrorCollector com.dev9.webtest.rule.
     * @throws AssertionError    Never thrown
     */
    @Override
    public void verify() throws AssertionError {
        // Does nothing.  Verification happens automatically with ErrorCollector com.dev9.webtest.rule.
    }

    @Override
    protected void reportMessage(TidyMessage tidyMessage) {
        errorCollector.addError(new AssertionError(formatTidyMessage(tidyMessage)));
    }
}
