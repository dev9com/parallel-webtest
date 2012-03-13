package com.dynacrongroup.webtest.rule;

import org.junit.Test;
import org.junit.runner.Description;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: yurodivuie
 * Date: 3/13/12
 * Time: 10:18 AM
 */
public class TimerRuleTest {

    /**
     * Not an exhaustive test; just a sanity check.
     */
    @Test
    public void verifyTimerReturnsANumber() throws InterruptedException {
        TimerRule rule = new TimerRule();
        Description mockDescription = mock(Description.class);

        when(mockDescription.getClassName()).thenReturn("class");
        when(mockDescription.getMethodName()).thenReturn("method");

        rule.starting(mockDescription);

        Thread.sleep(500);

        rule.finished(mockDescription);

        assertNotNull(rule.timer.duration);
        assertThat(rule.timer.duration, greaterThan(Long.valueOf(499)));

    }
}
