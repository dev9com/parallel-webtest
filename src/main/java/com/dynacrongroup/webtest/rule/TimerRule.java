package com.dynacrongroup.webtest.rule;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * This TestWatcher reports on test progress in Sauce Labs using the JavascriptExecutor.
 * <p/>
 * User: yurodivuie
 * Date: 3/9/12
 * Time: 8:52 AM
 */
public class TimerRule extends TestWatcher {

    Timing timer;

    public TimerRule() {
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
