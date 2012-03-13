package com.dynacrongroup.webtest.rule;

import com.dynacrongroup.webtest.sauce.SauceREST;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * User: yurodivuie
 * Date: 3/13/12
 * Time: 9:37 AM
 */
public class FinalTestStatusRuleTest {

    String testJobId = "1234";
    FinalTestStatusRule rule;

    @Before
    public void prepareRuleWithMock() {
        rule = new FinalTestStatusRule(testJobId);
        rule.sauceREST = mock(SauceREST.class);
    }

    @Test
    public void testPassed() {
        rule.classFinished(mock(Description.class));
        verify(rule.sauceREST).jobPassed(testJobId);
    }

    @Test
    public void testFailed() {
        rule.failed(new Throwable(), mock(Description.class));
        rule.classFinished(mock(Description.class));
        verify(rule.sauceREST).jobFailed(testJobId);
    }

}
