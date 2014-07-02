package com.dev9.webtest.rule;

import com.google.common.annotations.VisibleForTesting;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * This TestWatcher reports on test progress in Sauce Labs using the JavascriptExecutor.
 * <p/>
 * User: yurodivuie
 * Date: 3/9/12
 * Time: 8:52 AM
 */
public class MethodTimer extends TestWatcher {

    @VisibleForTesting
    Timing timer;

    public MethodTimer() {
    }

    @Override
    protected void starting(Description description) {
        String timerName = String.format("%s.%s",
        description.getClassName(),
                description.getMethodName());
        timer = new Timing(timerName);
        timer.start();
    }


    @Override
    protected void finished(Description description) {
        timer.stop();
    }
}
